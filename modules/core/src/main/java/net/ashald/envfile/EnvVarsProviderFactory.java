package net.ashald.envfile;

import org.jetbrains.annotations.NotNull;


public interface EnvVarsProviderFactory<T extends EnvVarsProvider> {

    @NotNull
    T createProvider(boolean shouldSubstituteEnvVar);

    @NotNull String getTitle();

}
