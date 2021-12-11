package net.ashald.envfile.platform;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class EnvUtil {
    /**
     * Mirrors {@link GeneralCommandLine#getEffectiveEnvironment()} without adding the overrides set for the command line.
     * This basically is the parent environment (if it's enabled) and PWD set to the working directory of the command line.
     *
     * @return A new map with the effective environment
     */
    public static Map<String, String> getInitialEnv(@NotNull GeneralCommandLine cmdline) {
        Map<String, String> environment = new HashMap<>();
        if (cmdline.getParentEnvironmentType() != GeneralCommandLine.ParentEnvironmentType.NONE) {
            environment.putAll(cmdline.getParentEnvironment());
        }

        if (SystemInfo.isUnix) {
            File workDirectory = cmdline.getWorkDirectory();
            if (workDirectory != null) {
                environment.put("PWD", FileUtil.toSystemDependentName(workDirectory.getAbsolutePath()));
            }
        }
        return environment;
    }
}
