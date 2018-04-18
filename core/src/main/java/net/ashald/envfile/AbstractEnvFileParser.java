package net.ashald.envfile;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractEnvFileParser implements EnvFileParser {

    @NotNull
    protected abstract Map<String, String> readFile(@NotNull String path) throws EnvFileErrorException, IOException;

    @NotNull
    @Override
    public Map<String, String> process(@NotNull String path, @NotNull Map<String, String> source) throws EnvFileErrorException, IOException {
        Map<String, String> env = new HashMap<String, String>(source);

        Map<String, String> overrides = readFile(path);

        env.putAll(overrides);

        return env;
    }
}
