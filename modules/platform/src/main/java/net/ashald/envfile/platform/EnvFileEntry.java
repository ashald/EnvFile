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
        return getFile(runConfig.getProject().getBaseDir(), path).exists();
    }

    public boolean validateType() {
        return getProvider() != null;
    }

    public Map<String, String> process(Map<String, String> source) throws IOException, EnvFileErrorException {
        EnvVarsProvider parser = getProvider();

        if (isEnabled() && parser != null) {
            return parser.process(path, source);
        }

        return source;
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

    private File getFile(VirtualFile baseDir, String systemPath) {
        if (!FileUtil.isAbsolute(systemPath)) {
            VirtualFile virtualFile = baseDir.findFileByRelativePath(systemPath);
            if (virtualFile != null) {
                systemPath = virtualFile.getPath();
            }
        }
        return new File(systemPath);
    }

}
