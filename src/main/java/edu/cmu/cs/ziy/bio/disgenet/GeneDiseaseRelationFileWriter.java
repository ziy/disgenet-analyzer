package edu.cmu.cs.ziy.bio.disgenet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tmatesoft.sqljet.core.SqlJetException;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

public class GeneDiseaseRelationFileWriter {

  public static enum Type {
    GENE_ATTRIBUTES, GENE_DISEASE_NETWORK
  }

  private GeneDiseaseRelationLoader loader;

  private Table<String, String, List<String>> table;

  public GeneDiseaseRelationFileWriter(String dbPath, Type type) throws SqlJetException {
    System.out.println("Loading ...");
    loader = new GeneDiseaseRelationLoader(new File(dbPath));
    switch (type) {
      case GENE_ATTRIBUTES:
        table = loader.getRelationFromGeneAttributes();
        break;
      case GENE_DISEASE_NETWORK:
        table = loader.getRelationFromGeneDiseaseNetwork();
        break;
    }
  }

  public void writeQa4dsInputYamlFile(String templatePath, int gsThreshold, String yamlPath)
          throws IOException {
    System.out.println("Creating object and dumping...");
    YamlSequenceDumper dumper = new YamlSequenceDumper(yamlPath, Charsets.UTF_8);
    // add template
    Map<String, String> templateData = Maps.newHashMap();
    templateData.put("template", templatePath);
    dumper.add(templateData);
    dumper.flush();
    // add variable values, candidatas and gs
    for (String dname : getDiseaseNames()) {
      // add variable values
      Map<String, String> var2value = Maps.newHashMap();
      var2value.put("DISEASE", dname);
      var2value.put("PHENOTYPE", getPhenotypeName(dname));
      var2value.put("NORMAL_TISSUE", getNormalTissueName(dname));
      var2value.put("DISEASE_TISSUE", getDiseaseTissueName(dname));
      var2value.put("DISEASE_CELL", getDiseaseCellName(dname));
      Map<String, Map<String, String>> valuesData = Maps.newHashMap();
      valuesData.put("values", var2value);
      dumper.add(valuesData);
      // add candidates
      Map<String, List<String>> candidateData = Maps.newHashMap();
      ImmutableList<String> geneList = ImmutableList.copyOf(getGeneNames());
      candidateData.put("candidates", geneList);
      dumper.add(candidateData);
      // add gs
      Map<String, List<String>> gname2relations = table.column(dname);
      List<String> gs = Lists.newArrayList();
      for (String gname : gname2relations.keySet()) {
        if (gname2relations.get(gname).size() >= gsThreshold) {
          gs.add(gname);
        }
      }
      Map<String, List<String>> gsData = Maps.newHashMap();
      gsData.put("gs", gs);
      dumper.add(gsData);
      dumper.flush();
    }
    dumper.close();
  }

  public void close() throws SqlJetException {
    loader.close();
  }

  private static String getPhenotypeName(String diseaseName) {
    return diseaseName + " phenotype";
  }

  private static String getNormalTissueName(String diseaseName) {
    return diseaseName.replaceAll("disease", "").replaceAll("cancer", "") + " tissue";
  }

  private static String getDiseaseTissueName(String diseaseName) {
    return diseaseName + " tissue";
  }

  private static String getDiseaseCellName(String diseaseName) {
    return diseaseName + " cell";
  }

  private Set<String> getGeneNames() {
    return table.rowKeySet();
  }

  private Set<String> getDiseaseNames() {
    return table.columnKeySet();
  }

}
