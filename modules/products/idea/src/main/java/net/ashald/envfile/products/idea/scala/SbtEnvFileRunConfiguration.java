package net.ashald.envfile.products.idea.scala;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.options.SettingsEditorGroup;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.sbt.runner.SbtRunConfiguration;

public class SbtEnvFileRunConfiguration extends SbtRunConfiguration {
    public SbtEnvFileRunConfiguration(Project project, ConfigurationFactory configurationFactory, String name) {
        super(project, configurationFactory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        SettingsEditor<SbtRunConfiguration> sbtTasksEditor = new SbtRunEnvFileConfigurationEditor(project(), this);

        SettingsEditorGroup<SbtRunConfiguration> group = new SettingsEditorGroup<>();
        group.addEditor("Configuration", sbtTasksEditor);
        SbtRunConfigurationExtensionManager.getInstance().appendEditors(this, group);
        return group;
    }
}
