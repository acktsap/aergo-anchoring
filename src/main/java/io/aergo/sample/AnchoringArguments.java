package io.aergo.sample;

import com.beust.jcommander.Parameter;
import lombok.Getter;
import lombok.Setter;

public class AnchoringArguments {

  @Getter
  @Setter
  @Parameter(names = {"-h", "--help"}, description = "show help", help = true)
  private boolean help = false;

  @Getter
  @Setter
  @Parameter(names = {"-g", "--keygen"}, description = "generate new key")
  private boolean keyGen = false;

  @Getter
  @Setter
  @Parameter(names = {"-p", "--password"}, description = "password to encrypt key")
  private String password;

  @Getter
  @Setter
  @Parameter(names = {"-l", "--listen"}, description = "listen smart event")
  private boolean listening = false;

}
