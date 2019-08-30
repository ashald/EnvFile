package net.ashald.envfile.providers.scriptenv;

import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.EnvVarsProviderFactory;
import org.jetbrains.annotations.NotNull;

public class ScriptRunnerParserFactory implements EnvVarsProviderFactory {

    @Override
    public @NotNull
    EnvVarsProvider createProvider(boolean shouldSubstituteEnvVar) {
        return new ScriptRunnerParser(shouldSubstituteEnvVar);
    }

    @Override
    public @NotNull String getTitle() {
        return "script";
    }

}
