package net.ashald.envfile;

import org.apache.commons.text.StringSubstitutor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.project.Project;

public abstract class AbstractEnvVarsProvider implements EnvVarsProvider {
    private boolean isEnvVarSubstitutionEnabled;

    public AbstractEnvVarsProvider(boolean shouldSubstituteEnvVar) {
        isEnvVarSubstitutionEnabled = shouldSubstituteEnvVar;
    }

    @NotNull
    protected abstract Map<String, String> getEnvVars(@NotNull Map<String, String> runConfigEnv, String path) throws EnvFileErrorException, IOException;

    @Override
    public boolean isEditable() {
        return true;
    }

    @NotNull
    @Override
    public Map<String, String> process(@NotNull Map<String, String> runConfigEnv, String path, @NotNull Map<String, String> aggregatedEnv, @NotNull Project project) throws EnvFileErrorException, IOException {
        Map<String, String> result = new HashMap<>(aggregatedEnv);
        Map<String, String> overrides = getEnvVars(runConfigEnv, path);

        for (Map.Entry<String, String> entry : overrides.entrySet()) {
            result.put(entry.getKey(), renderValue(entry.getValue(), result, project));
        }

        return result;
    }

    @NotNull
    private String renderValue(String template, @NotNull Map<String, String> context, Project project) {
        // replace $PROJECT_DIR$ by project path
        PathMacroManager macroManager = PathMacroManager.getInstance(project);
        String stage1 = macroManager.expandPath(template);

        if (!isEnvVarSubstitutionEnabled) {
            return stage1;
        }
        // resolve taking into account default values
        String stage2 = new StringSubstitutor(context).replace(stage1);
        // if ${FOO} was not resolved - replace it with empty string as it would've worked in bash
        String stage3 = new StringSubstitutor(key -> context.getOrDefault(key, "")).replace(stage2);

        return stage3;
    }
}
