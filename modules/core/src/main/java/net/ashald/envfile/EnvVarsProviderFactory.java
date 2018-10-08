package net.ashald.envfile;

import org.jetbrains.annotations.NotNull;


public interface EnvVarsProviderFactory {

    @NotNull EnvFileParser createParser();

    @NotNull String getTitle();

}
