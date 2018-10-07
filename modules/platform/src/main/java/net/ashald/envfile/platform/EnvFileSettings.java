package net.ashald.envfile.platform;

import java.util.Collections;
import java.util.List;

public class EnvFileSettings {

    private final boolean pluginEnabled;
    private final List<EnvFileEntry> entries;

    public EnvFileSettings(boolean isEnabled, List<EnvFileEntry> envFileEntries) {
        pluginEnabled = isEnabled;
        entries = envFileEntries;
    }

    public boolean isEnabled() {
        return pluginEnabled;
    }

    public List<EnvFileEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }
}
