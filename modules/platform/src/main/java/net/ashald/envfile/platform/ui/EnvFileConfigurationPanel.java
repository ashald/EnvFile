package net.ashald.envfile.platform.ui;

import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.*;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.ListTableModel;
import net.ashald.envfile.EnvVarProviderFactory;
import net.ashald.envfile.EnvVarsFileProvider;
import net.ashald.envfile.platform.*;
import net.ashald.envfile.platform.ui.table.EnvFileIsActiveColumnInfo;
import net.ashald.envfile.platform.ui.table.EnvFileSourceColumnInfo;
import net.ashald.envfile.platform.ui.table.EnvFileNameColumnInfo;
import net.ashald.envfile.platform.ui.table.EnvFileTypeColumnInfo;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;


class EnvFileConfigurationPanel<T extends RunConfigurationBase> extends JPanel {
    private static final int MAX_RECENT = 5;
    private static final LinkedList<EnvEntry> recent = new LinkedList<EnvEntry>();
    private final RunConfigurationBase runConfig;

    private final JCheckBox useEnvFileCheckBox;
    private final JCheckBox substituteEnvVarsCheckBox;
    private final JCheckBox supportPathMacroCheckBox;
    private final JCheckBox ignoreMissingCheckBox;
    private final JCheckBox experimentalIntegrationsCheckBox;
    private final ListTableModel<EnvEntry> envModel;
    private final TableView<EnvEntry> envTable;

    EnvFileConfigurationPanel(T configuration) {
        runConfig = configuration;

        // Define Model
        ColumnInfo<EnvEntry, Boolean> IS_ACTIVE = new EnvFileIsActiveColumnInfo();
        ColumnInfo<EnvEntry, String> SOURCE = new EnvFileSourceColumnInfo();
        ColumnInfo<EnvEntry, EnvEntry> TYPE = new EnvFileTypeColumnInfo();
        ColumnInfo<EnvEntry, EnvEntry> NAME = new EnvFileNameColumnInfo();

        envModel = new ListTableModel<>(IS_ACTIVE, TYPE, NAME, SOURCE);

        // Create Table
        envTable = new TableView<>(envModel);
        envTable.getEmptyText().setText("No environment variables files selected");

        setUpColumnWidth(envTable, 0, IS_ACTIVE, 20);
        setUpColumnWidth(envTable, 1, TYPE, 50);

        envTable.setColumnSelectionAllowed(false);
        envTable.setShowGrid(false);
        envTable.setDragEnabled(true);
        envTable.setShowHorizontalLines(false);
        envTable.setShowVerticalLines(false);
        envTable.setIntercellSpacing(new Dimension(0, 0));

        // Create global activation flag
        useEnvFileCheckBox = new JCheckBox("Enable EnvFile");
        useEnvFileCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                envTable.setEnabled(useEnvFileCheckBox.isSelected());
                substituteEnvVarsCheckBox.setEnabled(useEnvFileCheckBox.isSelected());
                supportPathMacroCheckBox.setEnabled(useEnvFileCheckBox.isSelected());
                ignoreMissingCheckBox.setEnabled(useEnvFileCheckBox.isSelected());
            }
        });
        substituteEnvVarsCheckBox = new JCheckBox("Substitute Environment Variables (${FOO} / ${BAR:-default} / $${ESCAPED})");
        substituteEnvVarsCheckBox.addActionListener(e -> envModel.getItems().forEach(envFileEntry -> envFileEntry.setSubstitutionEnabled(substituteEnvVarsCheckBox.isSelected())));
        supportPathMacroCheckBox = new JCheckBox("Process JetBrains path macro references ($PROJECT_DIR$)");
        ignoreMissingCheckBox = new JCheckBox("Ignore missing files");
        experimentalIntegrationsCheckBox = new JCheckBox("Enable experimental integrations (e.g. Gradle) - may break any time!");

        // TODO: come up with a generic approach for this
        envModel.addRow(new EnvFileEntry(runConfig, "runconfig", null, true, substituteEnvVarsCheckBox.isSelected()));

        // Create Toolbar - Add/Remove/Move actions
        final ToolbarDecorator envFilesTableDecorator = ToolbarDecorator.createDecorator(envTable);

        final AnActionButtonUpdater updater = new AnActionButtonUpdater() {
            @Override
            public boolean isEnabled(AnActionEvent e) {
                return useEnvFileCheckBox.isSelected();
            }
        };

        envFilesTableDecorator
                .setAddAction(new AnActionButtonRunnable() {
            @Override
            public void run(AnActionButton button) {
                doAddAction(button, envTable, envModel);
            }
        })
                .setAddActionUpdater(updater)
                .setRemoveActionUpdater(new AnActionButtonUpdater() {
                    @Override
                    public boolean isEnabled(AnActionEvent e) {
                        boolean allEditable = true;

                        for (EnvEntry entry : envTable.getSelectedObjects()) {
                            if (!entry.isEditable()) {
                                allEditable = false;
                                break;
                            }
                        }

                        return updater.isEnabled(e) && envTable.getSelectedRowCount() >= 1 && allEditable;
                    }
                });

        JPanel optionsPanel = new JPanel();
        optionsPanel.setBorder(JBUI.Borders.empty(5, 22, 5, 5));
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

        optionsPanel.add(substituteEnvVarsCheckBox);
        optionsPanel.add(supportPathMacroCheckBox);
        optionsPanel.add(ignoreMissingCheckBox);
        optionsPanel.add(experimentalIntegrationsCheckBox);

        // Compose UI
        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
        checkboxPanel.add(useEnvFileCheckBox);
        checkboxPanel.add(optionsPanel);

        optionsPanel.setLocation(100, 100);

        JPanel envFilesTableDecoratorPanel = envFilesTableDecorator.createPanel();
        Dimension size = new Dimension(-1, 150);
        envFilesTableDecoratorPanel.setMinimumSize(size);
        envFilesTableDecoratorPanel.setPreferredSize(size);

        setLayout(new BorderLayout());
        add(checkboxPanel, BorderLayout.NORTH);
        add(envFilesTableDecoratorPanel, BorderLayout.CENTER);
    }

    private void setUpColumnWidth(TableView<EnvEntry> table, int columnIdx, ColumnInfo columnInfo, int extend) {
        JTableHeader tableHeader = table.getTableHeader();
        FontMetrics fontMetrics = tableHeader.getFontMetrics(tableHeader.getFont());

        int preferredWidth = fontMetrics.stringWidth(columnInfo.getName()) + extend;

        table.getColumnModel().getColumn(columnIdx).setCellRenderer(new BooleanTableCellRenderer());
        TableColumn tableColumn = tableHeader.getColumnModel().getColumn(columnIdx);
        tableColumn.setWidth(preferredWidth);
        tableColumn.setPreferredWidth(preferredWidth);
        tableColumn.setMinWidth(preferredWidth);
        tableColumn.setMaxWidth(preferredWidth);
    }

    private void doAddAction(AnActionButton button, final TableView<EnvEntry> table, final ListTableModel<EnvEntry> model) {
        final JBPopupFactory popupFactory = JBPopupFactory.getInstance();
        DefaultActionGroup actionGroup = new DefaultActionGroup(null, false);

        for (final EnvVarsProviderExtension extension : EnvVarsProviderExtension.getParserExtensions()) {
            if (!extension.getFactory().createProvider(substituteEnvVarsCheckBox.isSelected()).isEditable()) {
                continue;
            }

            final String title;
            AnAction anAction;

            if (extension.getFactory().createProvider(false) instanceof EnvVarsFileProvider) {
                title = String.format("%s file", extension.getFactory().getTitle());

                anAction = new AnAction(title) {
                    @Override
                    public void actionPerformed(AnActionEvent e) {
                        final FileChooserDescriptor chooserDescriptor = FileChooserDescriptorFactory
                                .createSingleFileNoJarsDescriptor()
                                .withTitle(String.format("Select %s", title));

                        Project project = runConfig.getProject();

                        VirtualFile selectedFile = FileChooser.chooseFile(chooserDescriptor, project, null);

                        if (selectedFile != null) {
                            String selectedPath = selectedFile.getPath();
                            String baseDir = runConfig.getProject().getBaseDir().getPath();
                            if (selectedPath.startsWith(baseDir)) {
                                selectedPath = selectedPath.substring(baseDir.length() + 1);
                            }

                            final EnvFileEntry newOptions = new EnvFileEntry(runConfig, extension.getId(), selectedPath, true, substituteEnvVarsCheckBox.isSelected());
                            addOption(newOptions, table, model);
                        }
                    }
                };
            } else {
                title = extension.getFactory().getTitle();

                anAction = new AnAction(title) {
                    @Override
                    public void actionPerformed(AnActionEvent e) {
                        EnvVarProviderFactory factory = (EnvVarProviderFactory) extension.getFactory();
                        List list = factory.getOptions();
                        String description = factory.getDescription();

                        EnvVarDialog dialog = new EnvVarDialog(extension.getFactory().getTitle(), description, list);
                        if(dialog.showAndGet()) {
                            final EnvVarEntry newOptions = new EnvVarEntry(runConfig, extension.getId(),
                                    dialog.getEnvVarName(), dialog.getSelectedOption(), true,
                                    substituteEnvVarsCheckBox.isSelected());
                            addOption(newOptions, table, model);
                        }
                    }
                };
            }


            actionGroup.add(anAction);
        }
        synchronized (recent) {
            if (!recent.isEmpty()) {
                actionGroup.addSeparator("Recent");

                for (final EnvEntry entry : recent) {
                    String title;
                    String shortTitle;
                    if (entry instanceof EnvFileEntry) {
                        title = String.format("%s -> %s", entry.getTypeTitle(), ((EnvFileEntry) entry).getPath());
                    } else {
                        title = String.format("%s -> %s", entry.getTypeTitle(), ((EnvVarEntry) entry).getSelectedOption());
                    }
                    shortTitle = title.length() < 81 ? title : title.replaceFirst("(.{39}).+(.{39})", "$1...$2");

                    AnAction anAction = new AnAction(shortTitle, title, null) {
                        @Override
                        public void actionPerformed(AnActionEvent e) {
                            ArrayList<EnvEntry> newList = new ArrayList<EnvEntry>(model.getItems());
                            newList.add(entry);
                            model.setItems(newList);
                            int index = model.getRowCount() - 1;
                            model.fireTableRowsInserted(index, index);
                            table.setRowSelectionInterval(index, index);
                            synchronized (recent) {
                                recent.remove(entry);
                                recent.addFirst(entry);
                            }
                        }
                    };
                    actionGroup.add(anAction);
                }
            }
        }

        final ListPopup popup =
                popupFactory.createActionGroupPopup("Add...", actionGroup,
                        SimpleDataContext.getProjectContext(runConfig.getProject()), false, false, false, null,
                        -1, Conditions.<AnAction>alwaysTrue());
        popup.show(button.getPreferredPopupPoint());
    }

    private void addOption(EnvEntry newOptions, final TableView<EnvEntry> table, final ListTableModel<EnvEntry> model) {
        ArrayList<EnvEntry> newList = new ArrayList<EnvEntry>(model.getItems());

        newList.add(newOptions);
        model.setItems(newList);
        int index = model.getRowCount() - 1;
        model.fireTableRowsInserted(index, index);
        table.setRowSelectionInterval(index, index);

        synchronized (recent) {
            recent.remove(newOptions);
            recent.addFirst(newOptions);
            if (recent.size() > MAX_RECENT) recent.removeLast();
        }
    }

    EnvFileSettings getState() {
        return new EnvFileSettings(
                useEnvFileCheckBox.isSelected(),
                substituteEnvVarsCheckBox.isSelected(),
                supportPathMacroCheckBox.isSelected(),
                envModel.getItems(),
                ignoreMissingCheckBox.isSelected(),
                experimentalIntegrationsCheckBox.isSelected()
        );
    }

    void setState(EnvFileSettings state) {
        useEnvFileCheckBox.setSelected(state.isEnabled());
        substituteEnvVarsCheckBox.setSelected(state.isSubstituteEnvVarsEnabled());
        supportPathMacroCheckBox.setSelected(state.isPathMacroSupported());
        ignoreMissingCheckBox.setSelected(state.isIgnoreMissing());
        experimentalIntegrationsCheckBox.setSelected(state.isEnableExperimentalIntegrations());

        envTable.setEnabled(state.isEnabled());
        substituteEnvVarsCheckBox.setEnabled(state.isEnabled());
        supportPathMacroCheckBox.setEnabled(state.isEnabled());
        ignoreMissingCheckBox.setEnabled(state.isEnabled());
        experimentalIntegrationsCheckBox.setEnabled(state.isEnabled());

        envModel.setItems(new ArrayList<>(state.getEntries()));
    }
}


