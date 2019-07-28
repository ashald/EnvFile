package net.ashald.envfile.providers.runconfig;

import net.ashald.envfile.AbstractEnvVarsFileProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RunConfigEnvVarsProvider extends AbstractEnvVarsFileProvider {

    public RunConfigEnvVarsProvider(boolean shouldSubstituteEnvVar) {
        super(shouldSubstituteEnvVar);
    }

    @NotNull
    @Override
    protected Map<String, String> getEnvVars(@NotNull Map<String, String> runConfigEnv, String path) {
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
