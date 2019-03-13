package io.aergo.sample;

import com.beust.jcommander.JCommander;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

public class AppLauncher {

  public static final String configProperty = "config.file";

  public static void main(String[] args) throws Exception {
    final String configFile = System.getProperty(configProperty);
    final Properties props = new Properties();
    try (final InputStream inputStream = new FileInputStream(new File(configFile))) {
      final Reader reader = new InputStreamReader(inputStream, "UTF-8");
      props.load(reader);
    }

    final AnchoringArguments arguments = new AnchoringArguments();
    final JCommander jCommander = new JCommander(arguments);
    jCommander.parse(args);
    if (arguments.isHelp()) {
      jCommander.usage();
      return;
    }
    new AnchoringRunner(props).run(arguments);
  }

}
