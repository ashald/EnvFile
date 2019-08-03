package net.ashald.envfile;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public interface EnvProviderFactory<T extends  EnvProvider> {

    @NotNull
    T createProvider(boolean shouldSubstituteEnvVar);

    @NotNull String getTitle();

}
