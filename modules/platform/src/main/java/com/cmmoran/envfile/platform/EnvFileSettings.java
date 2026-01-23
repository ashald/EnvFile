package com.cmmoran.envfile.platform;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
@Builder
public class EnvFileSettings {

    @NonNull
    Boolean pluginEnabled;
    @NonNull
    Boolean envVarsSubstitutionEnabled;
    @NonNull
    Boolean pathMacroSupported;
    @NonNull
    Boolean ignoreMissing;
    @NonNull
    Boolean enableExperimentalIntegrations;
    @NonNull
    List<EnvFileEntry> entries;

    public boolean isPluginEnabledEnabled() {
        return getPluginEnabled();
    }

    public boolean isSubstituteEnvVarsEnabled() {
        return getEnvVarsSubstitutionEnabled();
    }

    public boolean isPathMacroSupported() {
        return getPathMacroSupported();
    }

    public boolean isIgnoreMissing() {
        return getIgnoreMissing();
    }

    public boolean isEnableExperimentalIntegrations() {
        return getEnableExperimentalIntegrations();
    }

    public List<EnvFileEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }
}
