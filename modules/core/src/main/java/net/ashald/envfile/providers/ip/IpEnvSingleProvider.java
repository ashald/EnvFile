package net.ashald.envfile.providers.ip;

import net.ashald.envfile.EnvSingleProvider;
import net.ashald.envfile.exceptions.EnvSingleErrorException;
import net.ashald.envfile.utils.NetworkInterfaceUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;


public class IpEnvSingleProvider implements EnvSingleProvider {


    public IpEnvSingleProvider(boolean shouldSubstituteEnvVar) {

    }

    @Override
    public @NotNull Map<String, String> process(@NotNull Map<String, String> runConfigEnv, String envVar, String selectedOption, @NotNull Map<String, String> aggregatedEnv) throws EnvSingleErrorException, IOException {
        Map<String, String> result = new HashMap<>(aggregatedEnv);

        String ip = null;
        try {
            ip = NetworkInterfaceUtil.getIpV4(selectedOption);
        } catch (NoSuchElementException e) {
            throw new EnvSingleErrorException(e.getMessage());
        }

        result.put(envVar, ip);
        return result;
    }

    @Override
    public boolean isEditable() {
        return true;
    }

}
