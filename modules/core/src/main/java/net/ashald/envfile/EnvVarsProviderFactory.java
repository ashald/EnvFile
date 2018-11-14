package net.ashald.envfile;

import org.jetbrains.annotations.NotNull;


public interface EnvVarsProviderFactory {

    @NotNull
    EnvVarsProvider createProvider(boolean shouldSubstituteEnvVar);

    @NotNull String getTitle();

}
