package net.ashald.envfile;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public interface EnvVarProviderFactory<T extends  EnvProvider> extends EnvProviderFactory {

    @NotNull
    T createProvider(boolean shouldSubstituteEnvVar);

    @NotNull String getTitle();

    @NotNull
    List<String> getOptions();

    @NotNull
    String getDescription();

}
