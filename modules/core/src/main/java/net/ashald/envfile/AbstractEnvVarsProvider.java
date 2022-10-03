package net.ashald.envfile;

import lombok.AllArgsConstructor;
import org.apache.commons.text.StringSubstitutor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public abstract class AbstractEnvVarsProvider implements EnvVarsProvider {
    private final boolean isEnvVarSubstitutionEnabled;

    protected abstract Map<String, String> getEnvVars(
            Map<String, String> runConfigEnv,
            InputStream content,
            String path
    ) throws EnvFileErrorException, IOException;

    @Override
    public boolean isEditable() {
        return true;
    }

    @NotNull
    @Override
    public Map<String, String> process(
            @NotNull Map<String, String> runConfigEnv,
            @NotNull Map<String, String> aggregatedEnv,
            InputStream content,
            String path
    ) throws EnvFileErrorException, IOException {
        Map<String, String> result = new HashMap<>(aggregatedEnv);
        Map<String, String> overrides = getEnvVars(runConfigEnv, content, path);

        for (Map.Entry<String, String> entry : overrides.entrySet()) {
            result.put(entry.getKey(), renderValue(entry.getValue(), result));
        }

        return result;
    }

    @NotNull
    private String renderValue(String template, @NotNull Map<String, String> context) {
        if (!isEnvVarSubstitutionEnabled) {
            return template;
        }
        // resolve taking into account default values
        String stage1 = new StringSubstitutor(context).replace(template);
        // if ${FOO} was not resolved - replace it with empty string as it would've worked in bash
        String stage2 = new StringSubstitutor(key -> context.getOrDefault(key, "")).replace(stage1);

        return stage2;
    }

    @Override
    public boolean isFileLocationValid(File file) {
        return file != null && file.exists();
    }
}
