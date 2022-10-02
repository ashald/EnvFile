package net.ashald.envfile;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;


public interface EnvVarsProvider {

    /**
     * @param runConfigEnv  - env vars provided by run config originally
     * @param aggregatedEnv - env vars rendered so far
     * @param path          - path to the file
     * @throws EnvFileErrorException
     * @throws IOException
     */
    @NotNull
    Map<String, String> process(
            @NotNull Map<String, String> runConfigEnv,
            @NotNull Map<String, String> aggregatedEnv,
            String path
    )
            throws EnvFileErrorException, IOException;

    boolean isEditable();

    boolean isFileLocationValid(File file);
}
