package net.ashald.envfile;

import org.jetbrains.annotations.NotNull;


public interface EnvProviderFactory<T extends  EnvProvider> {

    @NotNull
    T createProvider(boolean shouldSubstituteEnvVar);

    @NotNull String getTitle();

}
