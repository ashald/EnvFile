package net.ashald.envfile.platform;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * Represents an EnvFile entry from the run configuration config table.
 */
@Data
@Builder
public class EnvFileEntry {

    @NonNull
    private final String parserId;

    private String path;

    @NonNull
    private Boolean enabled;

    @NonNull
    private Boolean executable;

    public boolean isEnabled() {
        return Boolean.TRUE.equals(getEnabled());
    }

    public boolean isExecutable() {
        return Boolean.TRUE.equals(getExecutable());
    }

}
