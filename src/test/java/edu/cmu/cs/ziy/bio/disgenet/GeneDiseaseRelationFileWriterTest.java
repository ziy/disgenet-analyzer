package edu.cmu.cs.ziy.bio.disgenet;

import java.io.IOException;

import org.junit.Test;
import org.tmatesoft.sqljet.core.SqlJetException;

import edu.cmu.cs.ziy.bio.disgenet.GeneDiseaseRelationFileWriter.Type;

public class GeneDiseaseRelationFileWriterTest {

  @Test
  public void testRelationFromGeneDiseaseNetworkOneLayer() throws SqlJetException, IOException {
    GeneDiseaseRelationFileWriter writer = new GeneDiseaseRelationFileWriter("data/DisGeNETv2.db",
            Type.GENE_DISEASE_NETWORK);
    writer.writeQa4dsInputYamlFile("target-assessment.template.one-layer", 1,
            "result/disgenet-genediseasenetwork-one-layer.yaml");
    writer.close();
  }

  @Test
  public void testRelationFromGeneDiseaseNetworkTwoLayers() throws SqlJetException, IOException {
    GeneDiseaseRelationFileWriter writer = new GeneDiseaseRelationFileWriter("data/DisGeNETv2.db",
            Type.GENE_DISEASE_NETWORK);
    writer.writeQa4dsInputYamlFile("target-assessment.template.two-layers", 1,
            "result/disgenet-genediseasenetwork-two-layers.yaml");
    writer.close();
  }

  @Test
  public void testWriteRelationFromGeneAttributesOneLayer() throws SqlJetException, IOException {
    GeneDiseaseRelationFileWriter writer = new GeneDiseaseRelationFileWriter("data/DisGeNETv2.db",
            Type.GENE_ATTRIBUTES);
    writer.writeQa4dsInputYamlFile("target-assessment.template.one-layer", 1,
            "result/disgenet-geneattributes-one-layer.yaml");
    writer.close();
  }

  @Test
  public void testWriteRelationFromGeneAttributesTwoLayers() throws SqlJetException, IOException {
    GeneDiseaseRelationFileWriter writer = new GeneDiseaseRelationFileWriter("data/DisGeNETv2.db",
            Type.GENE_ATTRIBUTES);
    writer.writeQa4dsInputYamlFile("target-assessment.template.two-layers", 1,
            "result/disgenet-geneattributes-two-layers.yaml");
    writer.close();
  }

}
