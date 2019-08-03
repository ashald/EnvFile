package net.ashald.envfile.providers.ip;

import net.ashald.envfile.EnvFileErrorException;
import net.ashald.envfile.EnvVarsProvider;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class IpEnvVarProvider implements EnvVarsProvider {


    public IpEnvVarProvider(boolean shouldSubstituteEnvVar) {

    }

    @Override
    public @NotNull Map<String, String> process(@NotNull Map<String, String> runConfigEnv, String envVar, String selectedOption, @NotNull Map<String, String> aggregatedEnv) throws EnvFileErrorException, IOException {
        return null;
    }

    @Override
    public boolean isEditable() {
        return true;
    }

}
