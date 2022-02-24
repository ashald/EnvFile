package net.ashald.envfile.products.webstorm;

import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.lang.javascript.buildTools.npm.rc.NpmRunConfigurationEditor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.options.SettingsEditorGroup;
import net.ashald.envfile.platform.ui.EnvFileConfigurationEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class EnvRunConfigurationEditor extends NpmRunConfigurationEditor {
  private final SettingsEditorGroup<EnvRunConfiguration> group = new SettingsEditorGroup<>();
  public EnvRunConfigurationEditor(@NotNull RunConfiguration configuration) {
    super(configuration.getProject());
    EnvFileConfigurationEditor<EnvRunConfiguration> envFileEditor = new EnvFileConfigurationEditor<>((EnvRunConfiguration) configuration);
    group.addEditor("Npm", new Faker(this));
    group.addEditor("EnvFile", envFileEditor);
  }

  public SettingsEditorGroup<EnvRunConfiguration> getGroup() {
    return group;
  }

  private static class Faker extends SettingsEditor<EnvRunConfiguration> {
    private final EnvRunConfigurationEditor peer;

    public Faker(EnvRunConfigurationEditor peer) {
      super();
      this.peer = peer;
    }

    @Override
    protected void resetEditorFrom(@NotNull EnvRunConfiguration envRunConfiguration) {
      peer.resetEditorFrom(envRunConfiguration);
    }

    @Override
    protected void applyEditorTo(@NotNull EnvRunConfiguration envRunConfiguration) throws ConfigurationException {
      peer.applyEditorTo(envRunConfiguration);
    }

    @Override
    protected @NotNull JComponent createEditor() {
      return peer.createEditor();
    }
  }
}
