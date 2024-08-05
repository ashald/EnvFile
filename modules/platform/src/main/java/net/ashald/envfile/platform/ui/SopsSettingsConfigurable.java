package net.ashald.envfile.platform.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import net.ashald.envfile.platform.SopsSettings;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class SopsSettingsConfigurable implements Configurable {

    private TextFieldWithBrowseButton sopsExecutablePathField;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "EnvFile SOPS";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        sopsExecutablePathField = new TextFieldWithBrowseButton();
        sopsExecutablePathField.addBrowseFolderListener(
                "Select SOPS Executable",
                "Select the path to the SOPS executable",
                null,
                FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor()
        );

        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(0, 0, 0, 8);
        panel.add(new JLabel("SOPS executable path:"), labelConstraints);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = 0;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weightx = 1.0;
        panel.add(sopsExecutablePathField, fieldConstraints);

        GridBagConstraints fillerConstraints = new GridBagConstraints();
        fillerConstraints.gridx = 0;
        fillerConstraints.gridy = 1;
        fillerConstraints.weighty = 1.0;
        panel.add(new JPanel(), fillerConstraints);

        return panel;
    }

    @Override
    public boolean isModified() {
        return !sopsExecutablePathField.getText().equals(SopsSettings.getInstance().sopsExecutablePath);
    }

    @Override
    public void apply() {
        SopsSettings.getInstance().sopsExecutablePath = sopsExecutablePathField.getText();
    }

    @Override
    public void reset() {
        sopsExecutablePathField.setText(SopsSettings.getInstance().sopsExecutablePath);
    }
}
