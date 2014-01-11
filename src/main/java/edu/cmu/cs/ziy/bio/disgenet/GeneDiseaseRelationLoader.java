package edu.cmu.cs.ziy.bio.disgenet;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

import com.google.common.base.Splitter;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

public class GeneDiseaseRelationLoader {

  private SqlJetDb db;

  public GeneDiseaseRelationLoader(File dbFile) throws SqlJetException {
    db = SqlJetDb.open(dbFile, false);
    db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
  }

  public void close() throws SqlJetException {
    db.close();
  }

  /*
   * geneattributes table
   */
  private static final String GENE_ATTRIBUTES_TABLE_NAME = "geneattributes";

  private static final String GENE_NAME_COL_NAME = "geneName";

  private static final String[] DISEASE_NAME_COL_NAMES = {
      "UNIPROT_GV_assocDiseaseNames",
      "CTD_M_assocDiseaseNames",
      "CTD_T_assocDiseaseNames",
      "GAD_GV_assocDiseaseNames",
      "MGD_M_assocDiseaseNames",
      "CTD_MOUSE_M_assocDiseaseNames",
      "CTD_MOUSE_T_assocDiseaseNames",
      "LHGDN_M_assocDiseaseNames",
      "LHGDN_GV_assocDiseaseNames",
      "LHGDN_AE_assocDiseaseNames",
      "LHGDN_MP_assocDiseaseNames" };

  public Table<String, String, List<String>> getRelationFromGeneAttributes() throws SqlJetException {
    ISqlJetCursor cursor = db.getTable(GENE_ATTRIBUTES_TABLE_NAME).open();
    Table<String, String, List<String>> table = HashBasedTable.create();
    if (!cursor.eof()) {
      do {
        String gname = Iterables.getOnlyElement(getStrings(cursor, GENE_NAME_COL_NAME).keys());
        Multimap<String, String> dname2field = getStrings(cursor, DISEASE_NAME_COL_NAMES);
        for (String dname : dname2field.keys()) {
          if (!table.contains(gname, dname)) {
            table.put(gname, dname, Lists.<String> newArrayList());
          }
          table.get(gname, dname).addAll(dname2field.get(dname));
        }
      } while (cursor.next());
    }
    return table;
  }

  /*
   * genediseasenetwork table
   */
  private static final String GENE_DISEASE_NETWORK_TABLE_NAME = "genediseasenetwork";

  private static final String GENE_ID_COL_NAME = "geneId";

  private static final String DISEASE_ID_COL_NAME = "diseaseId";

  private static final String DISEASE_ATTRIBUTES_TABLE_NAME = "diseaseattributes";

  private static final String DISEASE_NAME_COL_NAME = "diseaseName";

  private static final String ASSOCIATE_TYPE_COL_NAME = "associationType";

  public Table<String, String, List<String>> getRelationFromGeneDiseaseNetwork()
          throws SqlJetException {
    // load gene id2name cache
    Map<Long, String> gid2name = Maps.newHashMap();
    ISqlJetCursor cursor = db.getTable(GENE_ATTRIBUTES_TABLE_NAME).open();
    if (!cursor.eof()) {
      do {
        gid2name.put(cursor.getInteger(GENE_ID_COL_NAME), cursor.getString(GENE_NAME_COL_NAME));
      } while (cursor.next());
    }
    // load disease id2name cache
    Map<String, String> did2name = Maps.newHashMap();
    cursor = db.getTable(DISEASE_ATTRIBUTES_TABLE_NAME).open();
    if (!cursor.eof()) {
      do {
        did2name.put(cursor.getString(DISEASE_ID_COL_NAME), cursor.getString(DISEASE_NAME_COL_NAME));
      } while (cursor.next());
    }
    // create relation table
    Table<String, String, List<String>> table = HashBasedTable.create();
    cursor = db.getTable(GENE_DISEASE_NETWORK_TABLE_NAME).open();
    if (!cursor.eof()) {
      do {
        String gname = gid2name.get(cursor.getInteger(GENE_ID_COL_NAME));
        String dname = did2name.get(cursor.getString(DISEASE_ID_COL_NAME));
        if (!table.contains(gname, dname)) {
          table.put(gname, dname, Lists.<String> newArrayList());
        }
        table.get(gname, dname).add(cursor.getString(ASSOCIATE_TYPE_COL_NAME));
      } while (cursor.next());
    }
    return table;
  }

  public static Multimap<String, String> getStrings(ISqlJetCursor cursor, String... fieldNames)
          throws SqlJetException {
    Multimap<String, String> value2fields = HashMultimap.create();
    for (String fieldName : fieldNames) {
      String values = cursor.getString(fieldName);
      Splitter splitter = Splitter.on(';').trimResults().omitEmptyStrings();
      for (String value : splitter.split(values)) {
        value2fields.put(value, fieldName);
      }
    }
    return value2fields;
  }
}
