package net.ashald.envfile.platform;

import java.util.Collections;
import java.util.List;

public class EnvFileSettings {

    private final boolean pluginEnabled;
    private final boolean envVarsSubstitutionEnabled;
    private final boolean pathMacroSupported;
    private final boolean ignoreMissing;
    private final boolean enableExperimentalIntegrations;
    private final List<EnvFileEntry> entries;

    public EnvFileSettings(
            boolean isEnabled,
            boolean substituteVars,
            boolean pathMacroSupported,
            List<EnvFileEntry> envFileEntries,
            boolean ignoreMissing,
            boolean experimentalInegrations
    ) {
        pluginEnabled = isEnabled;
        envVarsSubstitutionEnabled = substituteVars;
        this.pathMacroSupported = pathMacroSupported;
        this.ignoreMissing = ignoreMissing;
        entries = envFileEntries;
        enableExperimentalIntegrations = experimentalInegrations;
    }

    public boolean isEnabled() {
        return pluginEnabled;
    }

    public boolean isSubstituteEnvVarsEnabled() {
        return envVarsSubstitutionEnabled;
    }

    public boolean isPathMacroSupported() {
        return pathMacroSupported;
    }

    public boolean isIgnoreMissing() {
        return ignoreMissing;
    }

    public boolean isEnableExperimentalIntegrations() {
        return enableExperimentalIntegrations;
    }

    public List<EnvFileEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }
}
