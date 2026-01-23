package com.cmmoran.envfile.providers.dotenv;

import com.cmmoran.envfile.EnvVarsProvider;
import com.cmmoran.envfile.EnvVarsProviderFactory;
import com.cmmoran.envfile.providers.EnvFileExecutor;
import com.cmmoran.envfile.providers.EnvFileReader;
import com.cmmoran.envfile.providers.SingleFileEnvVarsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

public class DotEnvFileParserFactory implements EnvVarsProviderFactory {

    @Override
    public EnvVarsProvider createProvider(Map<String, String> baseEnvVars, Consumer<String> logger) {
        return SingleFileEnvVarsProvider.builder()
                .reader(EnvFileReader.DEFAULT)
                .executor(EnvFileExecutor.DEFAULT)
                .parser(DotEnvFileParser.INSTANCE)
                .logger(logger)
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
