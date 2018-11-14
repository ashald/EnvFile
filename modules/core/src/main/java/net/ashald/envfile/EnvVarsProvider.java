package net.ashald.envfile;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;

import com.intellij.openapi.project.Project;


public interface EnvVarsProvider {

    @NotNull Map<String, String> process(@NotNull Map<String, String> runConfigEnv, String path, @NotNull Map<String, String> aggregatedEnv, @NotNull Project project) throws EnvFileErrorException, IOException;

    boolean isEditable();
}
