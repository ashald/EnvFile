package net.ashald.envfile.providers.yaml;

import net.ashald.envfile.AbstractEnvVarsProvider;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class YamlEnvFileParser extends AbstractEnvVarsProvider {

    public YamlEnvFileParser(boolean shouldSubstituteEnvVar) {
        super(shouldSubstituteEnvVar);
    }

    @NotNull
    @Override
    public Map<String, String> getEnvVars(Map<String, String> runConfigEnv, @NotNull InputStream content) {
        return new Yaml().load(content);
    }
}
