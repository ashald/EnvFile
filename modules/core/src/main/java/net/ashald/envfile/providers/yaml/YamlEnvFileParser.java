package net.ashald.envfile.providers.yaml;

import net.ashald.envfile.AbstractEnvFileProvider;
import net.ashald.envfile.EnvFileErrorException;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class YamlEnvFileParser extends AbstractEnvFileProvider {

    public YamlEnvFileParser(boolean shouldSubstituteEnvVar) {
        super(shouldSubstituteEnvVar);
    }

    @NotNull
    @Override
    public Map<String, String> getEnvVars(@NotNull Map<String, String> runConfigEnv, @NotNull String path) throws EnvFileErrorException, IOException {
        Map<String, String> result;
        try (InputStream input = new FileInputStream(new File(path))) {
            result = new Yaml().load(input);
        } catch (ClassCastException e) {
            throw new EnvFileErrorException(
                    String.format("Cannot read '%s' as YAML dict - not all keys and/or values are strings", path), e);
        } catch (FileNotFoundException e) {
            throw new EnvFileErrorException(e);
        }
        if (result == null) {
            result = new HashMap<>();
        }
        return result;
    }
}
