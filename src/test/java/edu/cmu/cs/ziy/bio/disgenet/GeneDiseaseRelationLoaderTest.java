package edu.cmu.cs.ziy.bio.disgenet;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tmatesoft.sqljet.core.SqlJetException;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Table;
import com.google.common.io.Files;

public class GeneDiseaseRelationLoaderTest {

  private static GeneDiseaseRelationLoader loader;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    loader = new GeneDiseaseRelationLoader(new File("data/DisGeNETv2.db"));
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    loader.close();
  }

  @Test
  public void test() throws SqlJetException, IOException {
    Table<String, String, List<String>> table = loader.getRelationFromGeneDiseaseNetwork();
    String geneNames = Joiner.on('\n').join(table.rowKeySet());
    Files.write(geneNames, new File("result/disgenet-genes.txt"), Charsets.UTF_8);
    String diseaseNames = Joiner.on('\n').join(table.columnKeySet());
    Files.write(diseaseNames, new File("result/disgenet-diseases.txt"), Charsets.UTF_8);
  }

}
