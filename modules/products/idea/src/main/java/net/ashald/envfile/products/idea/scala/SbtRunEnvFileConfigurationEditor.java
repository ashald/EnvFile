package net.ashald.envfile.products.idea.scala;

import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.sbt.runner.SbtRunConfiguration;
import org.jetbrains.sbt.runner.SbtRunConfigurationForm;

import javax.swing.*;

public class SbtRunEnvFileConfigurationEditor extends SettingsEditor<SbtRunConfiguration> {

    private final SbtRunConfigurationForm form;

    public SbtRunEnvFileConfigurationEditor(Project project, SbtRunConfiguration configuration) {
        form = new SbtRunEnvFileConfigurationForm(project, configuration);
    }

    @Override
    protected void resetEditorFrom(@NotNull SbtRunConfiguration s) {
        form.apply(s);
    }

    protected void applyEditorTo(@NotNull SbtRunConfiguration s) {
        s.apply(form);
    }

    @NotNull
    @Override
    public JComponent createEditor() {
        return form.getMainPanel();
    }
}
