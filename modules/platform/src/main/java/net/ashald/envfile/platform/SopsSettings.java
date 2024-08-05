package net.ashald.envfile.platform;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import net.ashald.envfile.providers.sops.SopsUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "SopsSettings", storages = @Storage("envfile.sops.xml"))
public class SopsSettings implements PersistentStateComponent<SopsSettings> {

    private static final boolean IS_MAC = System.getProperty("os.name").toLowerCase().contains("mac");
    public static final String DEFAULT_SOPS_EXECUTABLE = IS_MAC ? "/opt/homebrew/bin/sops" : "sops";

    public String sopsExecutablePath = DEFAULT_SOPS_EXECUTABLE;

    public SopsSettings() {
        SopsUtils.setExecutableSupplier(this::getSopsExecutablePath);
    }

    public static SopsSettings getInstance() {
        return ApplicationManager.getApplication().getService(SopsSettings.class);
    }

    @NotNull
    @Override
    public SopsSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull SopsSettings state) {
        this.sopsExecutablePath = state.sopsExecutablePath;
    }

    @NotNull
    public String getSopsExecutablePath() {
        return (sopsExecutablePath != null && !sopsExecutablePath.isBlank()) ? sopsExecutablePath : DEFAULT_SOPS_EXECUTABLE;
    }
}
