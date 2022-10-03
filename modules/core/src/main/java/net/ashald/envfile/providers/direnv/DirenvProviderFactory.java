package net.ashald.envfile.providers.direnv;


import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.EnvVarsProviderFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DirenvProviderFactory implements EnvVarsProviderFactory {

    @Override
    public EnvVarsProvider createProvider(Map<String, String> baseEnvVars, Consumer<String> logger) {
        return new DirenvProvider();
    }

    @Override
    public @NotNull String getTitle() {
        return ".envrc";
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public @Nullable Predicate<String> getFileNamePredicate() {
        return name -> name.equals(".envrc");
    }

    @Override
    public boolean showHiddenFiles() {
        return true;
    }

}
