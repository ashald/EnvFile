package com.cmmoran.envfile.platform;

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
        return getEnabled();
    }

    public boolean isExecutable() {
        return getExecutable();
    }

}
