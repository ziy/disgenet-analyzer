package edu.cmu.cs.ziy.bio.disgenet;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class YamlSequenceDumper implements Closeable, Flushable, AutoCloseable {

  private Yaml yaml;

  private Writer writer;

  private List<Object> data;

  public YamlSequenceDumper(Writer writer) {
    this.yaml = new Yaml();
    this.writer = writer;
    this.data = Lists.newArrayList();
  }

  public YamlSequenceDumper(String filePath, Charset charset) throws FileNotFoundException {
    this.yaml = new Yaml();
    this.writer = Files.newWriter(new File(filePath), charset);
    this.data = Lists.newArrayList();
  }

  public void add(Object datum) {
    data.add(datum);
  }

  @Override
  public void close() throws IOException {
    flush();
    writer.close();
  }

  @Override
  public void flush() throws IOException {
    yaml.dump(data, writer);
    data.clear();
  }

}
