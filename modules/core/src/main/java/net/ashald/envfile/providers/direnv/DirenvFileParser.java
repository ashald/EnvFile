package net.ashald.envfile.providers.direnv;

import net.ashald.envfile.AbstractEnvVarsProvider;
import net.ashald.envfile.EnvFileErrorException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class DirenvFileParser extends AbstractEnvVarsProvider {
  private final DirenvService service;
  public DirenvFileParser(boolean shouldSubstituteEnvVar) {
    super(shouldSubstituteEnvVar);
    service = new DirenvService();
  }

  @NotNull
  @Override
  protected Map<String, String> getEnvVars(@NotNull Map<String, String> runConfigEnv, String path) throws EnvFileErrorException, IOException {
    service.setOuterEnv(runConfigEnv);
    File envrcFile = Paths.get(path).toFile();
    service.doExec(envrcFile);
    if(service.hasError()) {
      System.out.println("Error: " + service.getError());
      service.doAllow(envrcFile);
      service.doExec(envrcFile);
    }
    return service.getEnv();
  }
}
