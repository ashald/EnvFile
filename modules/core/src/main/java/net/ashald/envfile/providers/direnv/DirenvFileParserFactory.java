package net.ashald.envfile.providers.direnv;

import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.EnvVarsProviderFactory;
import org.jetbrains.annotations.NotNull;

public class DirenvFileParserFactory implements EnvVarsProviderFactory {
  @NotNull
  @Override
  public EnvVarsProvider createProvider(boolean shouldSubstituteEnvVar) {
    return new DirenvFileParser(shouldSubstituteEnvVar);
  }

  @NotNull
  @Override
  public String getTitle() {
    return ".envrc";
  }
}
