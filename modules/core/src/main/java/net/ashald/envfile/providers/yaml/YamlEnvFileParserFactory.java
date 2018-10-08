package net.ashald.envfile.providers.yaml;

import net.ashald.envfile.EnvFileParser;
import net.ashald.envfile.EnvVarsProviderFactory;
import org.jetbrains.annotations.NotNull;

public class YamlEnvFileParserFactory implements EnvVarsProviderFactory {

    @Override
    public @NotNull EnvFileParser createParser() {
        return new YamlEnvFileParser();
    }

    @NotNull
    public String getTitle() {
        return "JSON/YAML";
    }

}
