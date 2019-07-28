package net.ashald.envfile;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;


public interface EnvVarsFileProvider extends EnvProvider {

    @NotNull Map<String, String> process(@NotNull Map<String, String> runConfigEnv, String path, @NotNull Map<String, String> aggregatedEnv) throws EnvFileErrorException, IOException;

    boolean isFileLocationValid(File file);
}
