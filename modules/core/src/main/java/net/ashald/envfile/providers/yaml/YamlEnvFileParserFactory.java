package net.ashald.envfile.providers.yaml;

import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.EnvVarsProviderFactory;
import net.ashald.envfile.providers.EnvFileExecutor;
import net.ashald.envfile.providers.EnvFileReader;
import net.ashald.envfile.providers.SingleFileEnvVarsProvider;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public class YamlEnvFileParserFactory implements EnvVarsProviderFactory {
    private static final Yaml YAML = new Yaml();

    @Override
    public EnvVarsProvider createProvider(Map<String, String> baseEnvVars) {
        return SingleFileEnvVarsProvider.builder()
                .reader(EnvFileReader.DEFAULT)
                .executor(EnvFileExecutor.DEFAULT)
                .parser(
                        new YamlEnvFileParser(YAML)
                )
                .build();
    }

    @NotNull
    public String getTitle() {
        return "JSON/YAML";
    }

    @Override
    public boolean isEditable() {
        return true;
    }

}
