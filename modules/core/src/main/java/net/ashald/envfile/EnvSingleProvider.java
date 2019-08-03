package net.ashald.envfile;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;


public interface EnvSingleProvider extends EnvVarsProvider {

    @NotNull Map<String, String> process(@NotNull Map<String, String> runConfigEnv, String envVar, String selectedOption, @NotNull Map<String, String> aggregatedEnv) throws EnvFileErrorException, IOException;

    boolean isEditable();

}
