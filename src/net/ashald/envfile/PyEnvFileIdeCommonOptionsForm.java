package net.ashald.envfile;

import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CollectionComboBoxModel;
import com.jetbrains.python.PyIdeCommonOptionsForm;
import com.jetbrains.python.run.PyCommonOptionsFormData;
import net.ashald.envfile.formats.EnvFileFormatExtension;
import net.ashald.envfile.parsers.EnvFileParserExtension;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;


public class PyEnvFileIdeCommonOptionsForm extends PyIdeCommonOptionsForm {

    protected JCheckBox useEnvFileCheckBox;
    protected JPanel envFileSelectorPanel;
    protected JPanel envFilePanel;
    protected TextFieldWithBrowseButton envFilePathTextField;
    protected JComboBox envFileFormatComboBox;

    protected final VirtualFile baseDir;

    protected String envFilePath;
    protected String envFileParserId;

    protected static <T> T findChildComponent(Container container, Class<T> clazz) {
        Collection<Component> componentList = Arrays.asList(container.getComponents());
        for (Object o : componentList) {
            if (o != null && o.getClass() == clazz) {
                return clazz.cast(o);
            }
        }

        throw new NoSuchElementException(
                String.format(
                        "Cannot find '%s' among '%s' components", clazz.getName(), container.getClass().getName()
                )
        );
    }

    public PyEnvFileIdeCommonOptionsForm(PyCommonOptionsFormData data) {
        super(data);

        baseDir = data.getProject().getBaseDir();
        initUI(data.getProject());
    }

    protected void initUI(final Project project) {
        JComponent mainPanel = getMainPanel();

        JPanel commonOptionsPanel = (JPanel) mainPanel.getComponent(1);  // Main PyCommonOptionsForm container
        EnvironmentVariablesComponent envVarsComponent = findChildComponent(commonOptionsPanel, EnvironmentVariablesComponent.class);


        useEnvFileCheckBox = new JCheckBox("Read from file:");
        envFilePathTextField = new TextFieldWithBrowseButton();
        envFileFormatComboBox = new ComboBox(
                new CollectionComboBoxModel(EnvFileParserExtension.getParserExtensions()));

        envFileSelectorPanel = new JPanel();
        envFileSelectorPanel.setLayout(new BorderLayout(2, 2));
        envFileSelectorPanel.add(envFilePathTextField, BorderLayout.CENTER);
        envFileSelectorPanel.add(envFileFormatComboBox, BorderLayout.EAST);

        envFilePanel = new JPanel();

        envFilePanel.setLayout(new BorderLayout(37, 2));
        envFilePanel.add(useEnvFileCheckBox, BorderLayout.WEST);
        envFilePanel.add(envFileSelectorPanel, BorderLayout.CENTER);

        envVarsComponent.add(envFilePanel, BorderLayout.AFTER_LAST_LINE);

        useEnvFileCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setEnvFileEnabled(useEnvFileCheckBox.isSelected());
            }
        });
        setEnvFileEnabled(useEnvFileCheckBox.isSelected());

        final FileChooserDescriptor chooserDescriptor = FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor()
                .withTitle("Select Environment Variables File");

        envFilePathTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VirtualFile suggestedFile = getSuggestedEnvFile(project.getBaseDir());

                boolean isSuggestedFileIsHidden = suggestedFile != null &&
                        suggestedFile.getName().charAt(0) == '.' && SystemInfo.isUnix;

                VirtualFile selectedFile = FileChooser.chooseFile(
                        chooserDescriptor.withShowHiddenFiles(isSuggestedFileIsHidden), project, suggestedFile);
                String selectedFilePath = selectedFile == null
                        ? envFilePathTextField.getText() : selectedFile.getPath();

                envFilePathTextField.setText(selectedFilePath);
            }
        });

        envFilePathTextField.getTextField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        envFileFieldChanged(envFilePathTextField.getText());
                    }
                });
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        envFileFieldChanged(envFilePathTextField.getText());
                    }
                });
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        envFileFieldChanged(envFilePathTextField.getText());
                    }
                });
            }
        });

        envFileFormatComboBox.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        envFileParserChanged((EnvFileParserExtension) envFileFormatComboBox.getSelectedItem());
                    }
                });
    }

    protected VirtualFile getSuggestedEnvFile(VirtualFile baseDir) {
        VirtualFile suggestedFile;

        String currentFile = getEnvFilePath();
        if (currentFile == null || currentFile.isEmpty()) {
            suggestedFile = baseDir.findChild(".env");
        } else {
            suggestedFile = getFile(currentFile);
        }
        return suggestedFile;
    }

    protected VirtualFile getFile(String path) {
        if (path == null) {
            return null;
        }

        VirtualFile file;
        if (FileUtil.isAbsolute(path)) {
            file = LocalFileSystem.getInstance().findFileByPath(FileUtil.toSystemIndependentName(path));
        } else {
            file = baseDir.findFileByRelativePath(path);
        }
        return file;
    }

    protected void envFileFieldChanged(String newEnvFilePath) {
        if (newEnvFilePath == null || newEnvFilePath.equals(envFilePath)) {
            return;
        }

        newEnvFilePath = newEnvFilePath.trim();

        VirtualFile oldEnvFile = getFile(envFilePath);
        VirtualFile newEnvFile = getFile(newEnvFilePath);

        envFilePath = newEnvFilePath;

        if (envFileParserId != null) {
            if (oldEnvFile != null && newEnvFile != null) {
                String oldExtension = oldEnvFile.getExtension();
                String newExtension = newEnvFile.getExtension();
                if (oldExtension == null && newExtension == null) {
                    return;
                }
                if (oldExtension != null && newExtension != null && oldExtension.compareToIgnoreCase(newExtension) == 0) {
                    return;
                }
            }
        }

        if (newEnvFile == null || newEnvFile.getExtension() == null) {
            setEnvFileParserByExtension(null);
        } else {
            setEnvFileParserByExtension(newEnvFile.getExtension());
        }
    }

    public String getEnvFilePath() {
        return envFilePath == null ? "" : envFilePath;
    }

    public void setEnvFilePath(String path) {
        envFilePathTextField.setText(FileUtil.toSystemDependentName(path));
    }

    public void setEnvFileParserByExtension(String extension) {
        extension = extension == null ? "" : extension.toLowerCase();
        String parserId = EnvFileFormatExtension.getParserIdByExtension(extension);
        setEnvFileParserById(parserId);
    }

    public void setEnvFileParserById(String parserId) {
        EnvFileParserExtension suggested = null;
        if (parserId != null && !parserId.isEmpty()) {
            suggested = EnvFileParserExtension.getParserExtensionById(parserId);
        }
        envFileFormatComboBox.setSelectedItem(suggested);
    }

    public String getEnvFileParser() {
        return envFileParserId;
    }

    protected void envFileParserChanged(EnvFileParserExtension handler) {
        envFileParserId = handler == null ? null : handler.getId();
    }

    protected void setEnvFileEnabled(Boolean isEnabled) {
        useEnvFileCheckBox.setSelected(isEnabled);
        envFilePathTextField.setEnabled(isEnabled);
        envFileFormatComboBox.setEnabled(isEnabled);
    }

    protected boolean getEnvFileEnabled() {
        return useEnvFileCheckBox.isSelected();
    }

    @Override
    public Map<String, String> getEnvs() {
        EnvFileSettings settings = new EnvFileSettings(super.getEnvs());

        settings.putIsEnabled(getEnvFileEnabled());
        settings.putEnvFilePath(FileUtil.toSystemIndependentName(getEnvFilePath()));
        settings.putEnvFileParser(getEnvFileParser());

        return settings.getEnvVars();
    }

    @Override
    public void setEnvs(Map<String, String> envs) {
        EnvFileSettings settings = new EnvFileSettings(envs);

        setEnvFileEnabled(settings.popIsEnabled());

        String path = FileUtil.toSystemDependentName(settings.popEnvFilePath());
        envFileFieldChanged(path);
        setEnvFilePath(path);

        setEnvFileParserById(settings.popEnvFileParser());

        super.setEnvs(settings.getEnvVars());
    }
}