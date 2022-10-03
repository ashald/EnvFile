package net.ashald.envfile.providers.direnv;


import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.EnvVarsProviderFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class DirenvProviderFactory implements EnvVarsProviderFactory {

    @NotNull
    @Override
    public EnvVarsProvider createProvider(boolean shouldSubstituteEnvVar) {
        return new DirenvProvider(shouldSubstituteEnvVar);
    }

    @Override
    public @NotNull String getTitle() {
        return ".envrc";
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
