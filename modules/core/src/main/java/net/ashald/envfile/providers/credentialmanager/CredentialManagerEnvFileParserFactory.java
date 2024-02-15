package net.ashald.envfile.providers.credentialmanager;

import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.EnvVarsProviderFactory;
import net.ashald.envfile.providers.EnvFileExecutor;
import net.ashald.envfile.providers.EnvFileReader;
import net.ashald.envfile.providers.SingleFileEnvVarsProvider;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;
import java.util.function.Consumer;

public class CredentialManagerEnvFileParserFactory implements EnvVarsProviderFactory {
    private static final Yaml YAML = new Yaml();

    @Override
    public EnvVarsProvider createProvider(Map<String, String> baseEnvVars, Consumer<String> logger) {
        return SingleFileEnvVarsProvider.builder()
                .reader(EnvFileReader.DEFAULT)
                .executor(EnvFileExecutor.DEFAULT)
                .parser(
                        new CredentialManagerEnvFileParser(YAML)
                )
                .logger(logger)
                .build();
    }

    @NotNull
    public String getTitle() {
        return "CredentialManager/YAML";
    }

    @Override
    public boolean isEditable() {
        return true;
    }

}
