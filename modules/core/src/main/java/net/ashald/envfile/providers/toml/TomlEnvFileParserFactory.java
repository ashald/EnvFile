package net.ashald.envfile.providers.toml;

import com.moandjiezana.toml.Toml;
import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.EnvVarsProviderFactory;
import net.ashald.envfile.providers.EnvFileExecutor;
import net.ashald.envfile.providers.EnvFileReader;
import net.ashald.envfile.providers.SingleFileEnvVarsProvider;

import java.util.Map;
import java.util.function.Consumer;

public class TomlEnvFileParserFactory implements EnvVarsProviderFactory {
    private static final Toml TOML = new Toml();

    @Override
    public EnvVarsProvider createProvider(Map<String, String> baseEnvVars, Consumer<String> logger) {
        return SingleFileEnvVarsProvider.builder()
                .reader(EnvFileReader.DEFAULT)
                .executor(EnvFileExecutor.DEFAULT)
                .parser(
                        new TomlEnvFileParser(TOML)
                )
                .logger(logger)
                .build();
    }

    @Override
    public String getTitle() {
        return "TOML";
    }

    @Override
    public boolean isEditable() {
        return true;
    }
}
