package net.ashald.envfile.platform;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
@Builder
public class EnvFileSettings {

    @NonNull
    private final Boolean pluginEnabled;
    @NonNull
    private final Boolean envVarsSubstitutionEnabled;
    @NonNull
    private final Boolean pathMacroSupported;
    @NonNull
    private final Boolean ignoreMissing;
    @NonNull
    private final Boolean enableExperimentalIntegrations;
    @NonNull
    private final List<EnvFileEntry> entries;

    public boolean isPluginEnabledEnabled() {
        return Boolean.TRUE.equals(getPluginEnabled());
    }

    public boolean isSubstituteEnvVarsEnabled() {
        return Boolean.TRUE.equals(getEnvVarsSubstitutionEnabled());
    }

    public boolean isPathMacroSupported() {
        return Boolean.TRUE.equals(getPathMacroSupported());
    }

    public boolean isIgnoreMissing() {
        return Boolean.TRUE.equals(getIgnoreMissing());
    }

    public boolean isEnableExperimentalIntegrations() {
        return Boolean.TRUE.equals(getEnableExperimentalIntegrations());
    }

    public List<EnvFileEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }
}
