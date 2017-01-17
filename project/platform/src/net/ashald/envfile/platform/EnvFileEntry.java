package net.ashald.envfile.platform;

import com.intellij.execution.configuration.AbstractRunConfiguration;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import net.ashald.envfile.EnvFileErrorException;
import net.ashald.envfile.EnvFileParser;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class EnvFileEntry {

    private final RunConfigurationBase runConfig;
    private final String parserId;
    private final String path;

    private boolean enabled;

    public EnvFileEntry(RunConfigurationBase envFileRunConfig, String envFileParserId, String envFilePath, Boolean envFileIsEnabled) {
        runConfig = envFileRunConfig;
        parserId = envFileParserId;
        path = envFilePath;
        setEnable(envFileIsEnabled);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnable(boolean enable) {
        enabled = enable;
    }

    public String getTypeTitle() {
        EnvFileParser parser = getParser();
        return parser == null ? String.format("<%s>", parserId) : parser.getTitle();
    }

    public String getPath() {
        return path;
    }

    public String getParserId() {
        return parserId;
    }

    public boolean validatePath() {
        return getFile(runConfig.getProject().getBaseDir(), path).exists();
    }

    public boolean validateType() {
        return getParser() != null;
    }

    public Map<String, String> process(Map<String, String> source) throws IOException, EnvFileErrorException {
        EnvFileParser parser = getParser();

        if (isEnabled() && parser != null) {
            return parser.process(path, source);
        }

        return source;
    }

    @Nullable
    private EnvFileParser getParser() {
        EnvFileParserExtension extension = EnvFileParserExtension.getParserExtensionById(parserId);
        return extension == null ? null : extension.getParser();
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
