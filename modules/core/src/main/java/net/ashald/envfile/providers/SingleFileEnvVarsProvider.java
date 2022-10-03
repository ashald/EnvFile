package net.ashald.envfile.providers;

import lombok.Builder;
import lombok.val;
import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.exceptions.EnvFileException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;

@Builder
public class SingleFileEnvVarsProvider implements EnvVarsProvider {

    @NotNull
    private final EnvFileExecutor executor;

    @NotNull
    private final EnvFileReader reader;

    @NotNull
    private final EnvFileParser parser;

    @Override
    public Map<String, String> getEnvVars(File file, boolean isExecutable, Map<String, String> context)
            throws EnvFileException {

        val content = isExecutable
                ? executor.execute(file.getAbsolutePath(), context)
                : reader.read(file);

        return parser.parse(content);
    }
}
