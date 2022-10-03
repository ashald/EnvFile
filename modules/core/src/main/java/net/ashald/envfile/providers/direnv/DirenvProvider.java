package net.ashald.envfile.providers.direnv;

import net.ashald.envfile.AbstractEnvVarsProvider;
import net.ashald.envfile.EnvFileErrorException;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DirenvProvider extends AbstractEnvVarsProvider {

    public DirenvProvider(boolean shouldSubstituteEnvVar) {
        super(shouldSubstituteEnvVar);
    }

    @NotNull
    @Override
    protected Map<String, String> getEnvVars(@NotNull Map<String, String> runConfigEnv, String path) throws EnvFileErrorException {
        Path envrcPath = Paths.get(path);
        String workingDir = envrcPath.getParent().toString();
        return importDirenv(workingDir);
    }

    @NotNull
    private Map<String, String> importDirenv(String workingDir) throws EnvFileErrorException {
        try {
            Process process = executeDirenv(workingDir, "export", "json");
            int exitValue = process.waitFor();

            if (exitValue != 0) {
                try (InputStream input = process.getErrorStream()) {
                    String error = (new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))).lines().collect(Collectors.joining());

                    if (error.contains(" is blocked")) {
                        executeDirenv(workingDir, "allow").waitFor();
                    } else {
                        throw new EnvFileErrorException(error);
                    }
                }

                return importDirenv(workingDir);
            } else {
                try (InputStream input = process.getInputStream()) {
                    Optional<Map<String, String>> result = Optional.ofNullable(new Yaml().load(input));
                    return result.orElse(new HashMap<>());
                }
            }
        } catch (Exception ex) {
            throw new EnvFileErrorException(ex);
        }
    }

    private Process executeDirenv(String workingDir, String... args) throws IOException {
        String[] newArgs = new String[args.length + 1];
        System.arraycopy(args, 0, newArgs, 1, args.length);
        newArgs[0] = "direnv";
        ProcessBuilder pb = new ProcessBuilder(newArgs);
        pb.directory(new File(workingDir));
        return pb.start();
    }

}
