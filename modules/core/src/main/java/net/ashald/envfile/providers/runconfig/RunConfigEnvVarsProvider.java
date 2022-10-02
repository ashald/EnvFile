package net.ashald.envfile.providers.runconfig;

import net.ashald.envfile.AbstractEnvVarsProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class RunConfigEnvVarsProvider extends AbstractEnvVarsProvider {
    public static final String PARSER_ID = "runconfig";

    public RunConfigEnvVarsProvider(boolean shouldSubstituteEnvVar) {
        super(shouldSubstituteEnvVar);
    }

    @NotNull
    @Override
    protected Map<String, String> getEnvVars(@NotNull Map<String, String> runConfigEnv, InputStream content) {
        return new HashMap<>(runConfigEnv);
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public boolean isFileLocationValid(File file) {
        return true; // no file needed
    }
}
