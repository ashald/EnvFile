package net.ashald.envfile.providers.ip;

import net.ashald.envfile.EnvSingleProvider;
import net.ashald.envfile.exceptions.EnvSingleErrorException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;


public class IpEnvSingleProvider implements EnvSingleProvider {


    public IpEnvSingleProvider(boolean shouldSubstituteEnvVar) {

    }


    @Override
    public @NotNull Map<String, String> process(@NotNull Map<String, String> runConfigEnv, String envVar, String selectedOption, @NotNull Map<String, String> aggregatedEnv) throws EnvSingleErrorException, IOException {
        return null;
    }

    @Override
    public boolean isEditable() {
        return true;
    }

}
