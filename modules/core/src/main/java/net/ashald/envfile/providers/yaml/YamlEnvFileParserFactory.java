package net.ashald.envfile.providers.yaml;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.EnvVarsProviderFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class YamlEnvFileParserFactory implements EnvVarsProviderFactory {

    @Override
    public @NotNull
    EnvVarsProvider createProvider(boolean shouldSubstituteEnvVar) {
        return new YamlEnvFileParser(shouldSubstituteEnvVar);
    }

    @NotNull
    public String getTitle() {
        return "JSON/YAML";
    }

    @Override
    public @Nullable Condition<VirtualFile> getFileFilter() {
        return null;
    }

    @Override
    public boolean showHiddenFiles() {
        return false;
    }

}
