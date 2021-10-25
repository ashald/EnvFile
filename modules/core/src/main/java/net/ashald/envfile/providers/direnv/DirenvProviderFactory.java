package net.ashald.envfile.providers.direnv;


import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.EnvVarsProviderFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public @Nullable Condition<VirtualFile> getFileFilter() {
        return file -> file.getName().equals(".envrc");
    }

    @Override
    public boolean showHiddenFiles() {
        return true;
    }

}
