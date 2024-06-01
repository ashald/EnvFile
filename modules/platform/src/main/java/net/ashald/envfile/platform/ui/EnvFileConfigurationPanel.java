package net.ashald.envfile.platform.ui;

import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonUpdater;
import com.intellij.ui.BooleanTableCellRenderer;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.popup.PopupFactoryImpl;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.ListTableModel;
import lombok.val;
import net.ashald.envfile.EnvVarsProviderFactory;
import net.ashald.envfile.platform.EnvFileEntry;
import net.ashald.envfile.platform.EnvFileSettings;
import net.ashald.envfile.platform.EnvVarsProviderExtension;
import net.ashald.envfile.platform.ui.table.EnvFileIsActiveColumnInfo;
import net.ashald.envfile.platform.ui.table.EnvFileIsExecutableColumnInfo;
import net.ashald.envfile.platform.ui.table.EnvFilePathColumnInfo;
import net.ashald.envfile.platform.ui.table.EnvFileTypeColumnInfo;
import net.ashald.envfile.providers.toml.TomlEnvFileParser;
import net.ashald.envfile.providers.runconfig.RunConfigEnvVarsProvider;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;


class EnvFileConfigurationPanel<T extends RunConfigurationBase<?>> extends JPanel {
    private static final int MAX_RECENT = 5;
    private static final LinkedList<EnvFileEntry> RECENT = new LinkedList<>();
    private final RunConfigurationBase<?> runConfig;

    private final JCheckBox useEnvFileCheckBox;
    private final JCheckBox substituteEnvVarsCheckBox;
    private final JCheckBox supportPathMacroCheckBox;
    private final JCheckBox ignoreMissingCheckBox;
    private final JCheckBox experimentalIntegrationsCheckBox;
    private final ListTableModel<EnvFileEntry> envFilesModel;
    private final TableView<EnvFileEntry> envFilesTable;

    EnvFileConfigurationPanel(T configuration) {
        runConfig = configuration;

        // Define Model
        ColumnInfo<EnvFileEntry, Boolean> columnIsActive = new EnvFileIsActiveColumnInfo();
        ColumnInfo<EnvFileEntry, Boolean> columnIsExecutable = new EnvFileIsExecutableColumnInfo();
        ColumnInfo<EnvFileEntry, String> columnFile = new EnvFilePathColumnInfo(runConfig.getProject());
        ColumnInfo<EnvFileEntry, EnvFileEntry> columnType = new EnvFileTypeColumnInfo();

        envFilesModel = new ListTableModel<>(columnIsActive, columnIsExecutable, columnFile, columnType);

        // Create Table
        envFilesTable = new TableView<>(envFilesModel);
        envFilesTable.getEmptyText().setText("No environment variables files selected");

        setUpColumnWidth(envFilesTable, 0, columnIsActive, 20);
        setUpColumnWidth(envFilesTable, 1, columnIsExecutable, 20);
        setUpColumnWidth(envFilesTable, 3, columnType, 50);

        envFilesTable.setColumnSelectionAllowed(false);
        envFilesTable.setShowGrid(false);
        envFilesTable.setDragEnabled(true);
        envFilesTable.setShowHorizontalLines(false);
        envFilesTable.setShowVerticalLines(false);
        envFilesTable.setIntercellSpacing(new Dimension(0, 0));

        // Create global activation flag
        useEnvFileCheckBox = new JCheckBox("Enable EnvFile");
        useEnvFileCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                envFilesTable.setEnabled(useEnvFileCheckBox.isSelected());
                substituteEnvVarsCheckBox.setEnabled(useEnvFileCheckBox.isSelected());
                supportPathMacroCheckBox.setEnabled(useEnvFileCheckBox.isSelected());
                ignoreMissingCheckBox.setEnabled(useEnvFileCheckBox.isSelected());
                experimentalIntegrationsCheckBox.setEnabled(useEnvFileCheckBox.isSelected());
            }
        });
        substituteEnvVarsCheckBox = new JCheckBox("Substitute Environment Variables (${FOO} / ${BAR:-default} / $${ESCAPED})");
        supportPathMacroCheckBox = new JCheckBox("Process JetBrains path macro references ($PROJECT_DIR$)");
        ignoreMissingCheckBox = new JCheckBox("Ignore missing files");
        experimentalIntegrationsCheckBox = new JCheckBox("Enable experimental integrations (e.g. Gradle) - may break any time!");

        // TODO: come up with a generic approach for this
        envFilesModel.addRow(
                EnvFileEntry.builder()
                        .parserId(RunConfigEnvVarsProvider.PARSER_ID)
                        .enabled(true)
                        .executable(false)
                        .build()
        );

        // Create Toolbar - Add/Remove/Move actions
        final ToolbarDecorator envFilesTableDecorator = ToolbarDecorator.createDecorator(envFilesTable);

        final AnActionButtonUpdater updater = e -> useEnvFileCheckBox.isSelected();

        envFilesTableDecorator
                .setAddAction(button -> doAddAction(button, envFilesTable, envFilesModel))
                .setAddActionUpdater(updater)
                .setRemoveActionUpdater(e -> {
                    boolean allEditable = true;

                    for (EnvFileEntry entry : envFilesTable.getSelectedObjects()) {
                        val editable =
                                EnvVarsProviderExtension.getParserFactoryById(entry.getParserId())
                                        .map(EnvVarsProviderFactory::isEditable)
                                        .orElse(true);

                        if (!editable) {
                            allEditable = false;
                            break;
                        }
                    }

                    return updater.isEnabled(e) && envFilesTable.getSelectedRowCount() >= 1 && allEditable;
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

    private void setUpColumnWidth(TableView<EnvFileEntry> table, int columnIdx, ColumnInfo<?, ?> columnInfo, int extend) {
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

    private void doAddAction(
            AnActionButton button,
            final TableView<EnvFileEntry> table,
            final ListTableModel<EnvFileEntry> model
    ) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        for (final EnvVarsProviderExtension extension : EnvVarsProviderExtension.getParserExtensions()) {
            if (!extension.getFactory().isEditable()) {
                continue;
            }

            final String title = String.format("%s file", extension.getFactory().getTitle());
            AnAction anAction = new AnAction(title) {
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

                        ArrayList<EnvFileEntry> newList = new ArrayList<EnvFileEntry>(model.getItems());
                        final EnvFileEntry newOptions = EnvFileEntry.builder()
                                .parserId(extension.getId())
                                .path(selectedPath)
                                .enabled(true)
                                .executable(false)
                                .build();

                        newList.add(newOptions);
                        model.setItems(newList);
                        int index = model.getRowCount() - 1;
                        model.fireTableRowsInserted(index, index);
                        table.setRowSelectionInterval(index, index);

                        synchronized (RECENT) {
                            RECENT.remove(newOptions);
                            RECENT.addFirst(newOptions);
                            if (RECENT.size() > MAX_RECENT) RECENT.removeLast();
                        }
                    }
                }
            };
            actionGroup.add(anAction);
        }
        synchronized (RECENT) {
            if (!RECENT.isEmpty()) {
                actionGroup.addSeparator("Recent");

                for (final EnvFileEntry entry : RECENT) {
                    val typeTitle = EnvVarsProviderExtension.getParserFactoryById(entry.getParserId())
                            .map(EnvVarsProviderFactory::getTitle)
                            .orElse(String.format("<%s>", entry.getParserId()));

                    String title = String.format("%s -> %s", typeTitle, entry.getPath());
                    String shortTitle = title.length() < 81 ? title : title.replaceFirst("(.{39}).+(.{39})", "$1...$2");
                    AnAction anAction = new AnAction(shortTitle, title, null) {
                        @Override
                        public void actionPerformed(AnActionEvent e) {
                            ArrayList<EnvFileEntry> newList = new ArrayList<EnvFileEntry>(model.getItems());
                            newList.add(entry);
                            model.setItems(newList);
                            int index = model.getRowCount() - 1;
                            model.fireTableRowsInserted(index, index);
                            table.setRowSelectionInterval(index, index);
                            synchronized (RECENT) {
                                RECENT.remove(entry);
                                RECENT.addFirst(entry);
                            }
                        }
                    };
                    actionGroup.add(anAction);
                }
            }
        }

        final String popupPlace = ActionPlaces.getActionGroupPopupPlace(getClass().getSimpleName());
        final ListPopup popup =
                new PopupFactoryImpl.ActionGroupPopup(
                        "Add...", actionGroup, DataManager.getInstance().getDataContext(this),
                        false, false, false, false,
                        null, -1, Conditions.<AnAction>alwaysTrue(), popupPlace);

        if (button.getPreferredPopupPoint() != null) {
            popup.show(button.getPreferredPopupPoint());
        }
    }

    EnvFileSettings getState() {
        return EnvFileSettings.builder()
                .pluginEnabled(useEnvFileCheckBox.isSelected())
                .envVarsSubstitutionEnabled(substituteEnvVarsCheckBox.isSelected())
                .pathMacroSupported(supportPathMacroCheckBox.isSelected())
                .ignoreMissing(ignoreMissingCheckBox.isSelected())
                .enableExperimentalIntegrations(experimentalIntegrationsCheckBox.isSelected())
                .entries(envFilesModel.getItems())
                .build();
    }

    void setState(EnvFileSettings state) {
        useEnvFileCheckBox.setSelected(state.isPluginEnabledEnabled());
        substituteEnvVarsCheckBox.setSelected(state.isSubstituteEnvVarsEnabled());
        supportPathMacroCheckBox.setSelected(state.isPathMacroSupported());
        ignoreMissingCheckBox.setSelected(state.isIgnoreMissing());
        experimentalIntegrationsCheckBox.setSelected(state.isEnableExperimentalIntegrations());

        envFilesTable.setEnabled(state.isPluginEnabledEnabled());
        substituteEnvVarsCheckBox.setEnabled(state.isPluginEnabledEnabled());
        supportPathMacroCheckBox.setEnabled(state.isPluginEnabledEnabled());
        ignoreMissingCheckBox.setEnabled(state.isPluginEnabledEnabled());
        experimentalIntegrationsCheckBox.setEnabled(state.isPluginEnabledEnabled());

        envFilesModel.setItems(new ArrayList<>(state.getEntries()));
    }
}
