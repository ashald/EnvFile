package net.ashald.envfile.providers.runconfig;

import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.EnvVarsProviderFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

import java.util.Map;
import java.util.function.Consumer;

public class RunConfigEnvVarsProviderFactory implements EnvVarsProviderFactory {

    @Override
    public EnvVarsProvider createProvider(Map<String, String> baseEnvVars, Consumer<String> logger) {
        return new RunConfigEnvVarsProvider(baseEnvVars);
    }

    @Override
    public @NotNull String getTitle() {
        return "Run Config";
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public @Nullable Predicate<String> getFileNamePredicate() {
        return null;
    }

    @Override
    public boolean showHiddenFiles() {
        return false;
    }

}
