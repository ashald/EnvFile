package net.ashald.envfile.providers.dotenv;

import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.EnvVarsProviderFactory;
import net.ashald.envfile.providers.EnvFileExecutor;
import net.ashald.envfile.providers.EnvFileReader;
import net.ashald.envfile.providers.SingleFileEnvVarsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class DotEnvFileParserFactory implements EnvVarsProviderFactory {

    @Override
    public EnvVarsProvider createProvider(Map<String, String> baseEnvVars) {
        return SingleFileEnvVarsProvider.builder()
                .reader(EnvFileReader.DEFAULT)
                .executor(EnvFileExecutor.DEFAULT)
                .parser(DotEnvFileParser.INSTANCE)
                .build();
    }


    @Override
    public @NotNull String getTitle() {
        return ".env";
    }

    @Override
    public boolean isEditable() {
        return true;
    }

}
