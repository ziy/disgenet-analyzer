package edu.cmu.cs.ziy.bio.disgenet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tmatesoft.sqljet.core.SqlJetException;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Table;
import com.google.common.io.Files;

public class GeneDiseaseRelationLoaderTest {

  private static GeneDiseaseRelationLoader loader;

  private static Table<String, String, List<String>> table;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    loader = new GeneDiseaseRelationLoader(new File("data/DisGeNETv2.db"));
    table = loader.getRelationFromGeneAttributes();
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    loader.close();
  }

  @Test
  public void testGenes() throws SqlJetException, IOException {
    List<String> genes = Ordering.from(String.CASE_INSENSITIVE_ORDER).immutableSortedCopy(
            table.rowKeySet());
    String geneString = Joiner.on('\n').join(genes);
    Files.write(geneString, new File("result/disgenet-genes.txt"), Charsets.UTF_8);
  }

  @Test
  public void testDiseases() throws SqlJetException, IOException {
    List<String> diseases = Ordering.from(String.CASE_INSENSITIVE_ORDER).immutableSortedCopy(
            table.columnKeySet());
    String diseaseString = Joiner.on('\n').join(diseases);
    Files.write(diseaseString, new File("result/disgenet-diseases.txt"), Charsets.UTF_8);
  }

  @Test
  public void testRelations() throws SqlJetException, IOException {
    SetMultimap<String, String> gene2diseases = HashMultimap.create();
    for (String gene : table.rowKeySet()) {
      gene2diseases.putAll(gene, table.row(gene).keySet());
    }
    List<String> entryStrings = Lists.newArrayList();
    for (Entry<String, String> e : gene2diseases.entries()) {
      entryStrings.add(e.getKey() + "\t" + e.getValue());
    }
    String relationString = Joiner.on('\n').join(entryStrings);
    Files.write(relationString, new File("result/disgenet-relations.txt"), Charsets.UTF_8);
  }

}
