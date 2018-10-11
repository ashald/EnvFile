package net.ashald.envfile;

import org.jetbrains.annotations.NotNull;


public interface EnvVarsProviderFactory {

    @NotNull
    EnvVarsProvider createParser();

    @NotNull String getTitle();

}
