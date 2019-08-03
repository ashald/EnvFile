package net.ashald.envfile.platform.ui;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBTextField;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EnvVarDialog extends DialogWrapper {

    private String description;
    private List<String> options;

    private final JComboBox jComboBox;
    private final JTextField jInput;


    public EnvVarDialog(String title, String description, List<String> options) {
        super(true); // use current window as parent
        setTitle(title);
        this.description = description;
        this.options = options;

        this.jInput = new JBTextField();
        this.jComboBox = new ComboBox();

        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
       JPanel dialogPanel = new JPanel(new GridLayout(3, 1));

        JLabel label = new JLabel(description);
        dialogPanel.add(label);

        dialogPanel.add(jInput);

        options.stream().forEach(option -> jComboBox.addItem(option));
        dialogPanel.add(jComboBox);

        return dialogPanel;
    }

    @Override
    protected void doOKAction() {
        if (this.getOKAction().isEnabled()) {
            if (!this.jInput.getText().isEmpty() && !(this.jComboBox.getSelectedIndex() == -1)) {
                this.close(0);
            }
        }
    }

    public String getEnvVarName() {
        return this.jInput.getText();
    }

    public String getSelectedOption() {
        return (String) this.jComboBox.getSelectedItem();
    }
}
