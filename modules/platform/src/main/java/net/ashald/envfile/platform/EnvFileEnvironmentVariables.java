package net.ashald.envfile.platform;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.project.Project;
import net.ashald.envfile.EnvFileErrorException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class EnvFileEnvironmentVariables {

    private final EnvFileSettings envFileSettings;

    public EnvFileEnvironmentVariables(EnvFileSettings envFileSettings) {
        this.envFileSettings = envFileSettings;
    }

    public Map<String, String> render(
            @NotNull Project project,
            @NotNull Map<String, String> runConfigEnv,
            boolean includeParentEnv
    ) throws ExecutionException {
        Map<String, String> baseEnv = new HashMap<>();

        if (includeParentEnv) {
            baseEnv.putAll(
                    new GeneralCommandLine()
                            .withParentEnvironmentType(
                                    GeneralCommandLine.ParentEnvironmentType.CONSOLE
                            )
                            .getParentEnvironment()
            );
        }

        baseEnv.putAll(runConfigEnv);

        if (envFileSettings == null || !envFileSettings.isEnabled()) {
            return new HashMap<>(baseEnv);
        }

        Map<String, String> result = new HashMap<>();
        for (EnvFileEntry entry : envFileSettings.getEntries()) {
            try {
                result = entry.process(baseEnv, result, envFileSettings.isIgnoreMissing());
            } catch (EnvFileErrorException | IOException e) {
                throw new ExecutionException(e);
            }
        }
        if (envFileSettings.isPathMacroSupported()) {
            // replace $PROJECT_DIR$ by project path
            PathMacroManager macroManager = PathMacroManager.getInstance(project);
            result = result.entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            o -> macroManager.expandPath(o.getValue())
                    ));
        }
        return result;
    }
}
