package net.ashald.envfile.platform;

import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import net.ashald.envfile.EnvFileErrorException;
import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.EnvVarsProviderFactory;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class EnvFileEntry {

    private final RunConfigurationBase runConfig;

    private final String parserId;
    private String path;
    private boolean isEnabled;

    public EnvFileEntry(RunConfigurationBase envFileRunConfig, String envFileParserId, String envFilePath, boolean enabled) {
        runConfig = envFileRunConfig;
        parserId = envFileParserId;
        path = envFilePath;
        setEnable(enabled);
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnable(boolean enable) {
        isEnabled = enable;
    }

    public String getTypeTitle() {
        EnvVarsProviderFactory factory = getProviderFactory();
        return factory == null ? String.format("<%s>", parserId) : factory.getTitle();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String value) {
        path = value;
    }

    public String getParserId() {
        return parserId;
    }

    public boolean validatePath() {
        File file = getFile();
        return file == null || file.exists();
    }

    public boolean validateType() {
        return getProvider() != null;
    }

    public Map<String, String> process(Map<String, String> runConfigEnv, Map<String, String> aggregatedEnv) throws IOException, EnvFileErrorException {
        EnvVarsProvider parser = getProvider();

        if (isEnabled() && parser != null) {
            File file = getFile();
            return parser.process(runConfigEnv, file == null ? null : file.getPath(), aggregatedEnv);
        }

        return aggregatedEnv;
    }

    public boolean isEditable() {
        EnvVarsProvider provider = getProvider();
        return provider != null && getProvider().isEditable();
    }

    @Nullable
    private EnvVarsProviderFactory getProviderFactory() {
        EnvVarsProviderExtension extension = EnvVarsProviderExtension.getParserExtensionById(parserId);
        return extension == null ? null : extension.getFactory();
    }

    @Nullable
    private EnvVarsProvider getProvider() {
        EnvVarsProviderFactory factory = getProviderFactory();
        return factory == null ? null : factory.createProvider();
    }

    private File getFile() {
        if (path == null) {
            return null;
        }
        String resolvedPath = path;
        if (!FileUtil.isAbsolute(resolvedPath)) {
            VirtualFile virtualFile = runConfig.getProject().getBaseDir().findFileByRelativePath(resolvedPath);
            if (virtualFile != null) {
                resolvedPath = virtualFile.getPath();
            }
        }
        return new File(resolvedPath);
    }
}
