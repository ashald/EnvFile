package net.ashald.envfile;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public interface EnvSingleProviderFactory extends EnvVarsProviderFactory<EnvSingleProvider> {

    @NotNull
    List<String> getOptions();

    @NotNull
    String getDescription();

}
