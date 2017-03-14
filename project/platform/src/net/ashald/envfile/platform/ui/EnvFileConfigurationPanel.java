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
import net.ashald.envfile.platform.EnvFileEntry;
import net.ashald.envfile.platform.EnvFileParserExtension;
import net.ashald.envfile.platform.EnvFileSettings;
import net.ashald.envfile.platform.ui.table.EnvFileIsActiveColumnInfo;
import net.ashald.envfile.platform.ui.table.EnvFilePathColumnInfo;
import net.ashald.envfile.platform.ui.table.EnvFileTypeColumnInfo;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

class EnvFileConfigurationPanel<T extends RunConfigurationBase> extends JPanel {
    private final RunConfigurationBase runConfig;

    private final JCheckBox useEnvFileCheckBox;
    private final ListTableModel<EnvFileEntry> envFilesModel;
    private final TableView<EnvFileEntry> envFilesTable;

    EnvFileConfigurationPanel(T configuration) {
        runConfig = configuration;

        // Define Model
        ColumnInfo<EnvFileEntry, Boolean> IS_ACTIVE = new EnvFileIsActiveColumnInfo();
        ColumnInfo<EnvFileEntry, String> FILE = new EnvFilePathColumnInfo();
        ColumnInfo<EnvFileEntry, EnvFileEntry> TYPE = new EnvFileTypeColumnInfo();

        envFilesModel = new ListTableModel<EnvFileEntry>(IS_ACTIVE, FILE, TYPE);

        // Create Table
        envFilesTable = new TableView<EnvFileEntry>(envFilesModel);
        envFilesTable.getEmptyText().setText("No environment variables files selected");

        setUpColumnWidth(envFilesTable, 0, IS_ACTIVE, 20);
        setUpColumnWidth(envFilesTable, 2, TYPE, 50);

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
            }
        });

        // Create Toolbar - Add/Remove/Move actions
        final ToolbarDecorator envFilesTableDecorator = ToolbarDecorator.createDecorator(envFilesTable);

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
                doAddAction(button, envFilesTable, envFilesModel);
            }
        })
                .setAddActionUpdater(updater)
                .setRemoveActionUpdater(new AnActionButtonUpdater() {
                    @Override
                    public boolean isEnabled(AnActionEvent e) {
                        return updater.isEnabled(e) && envFilesTable.getSelectedRowCount() >= 1;
                    }
                });

        // Compose UI
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, JBUI.scale(5), JBUI.scale(5)));
        checkboxPanel.add(useEnvFileCheckBox);

        JPanel envFilesTableDecoratorPanel = envFilesTableDecorator.createPanel();
        Dimension size = new Dimension(-1, 150);
        envFilesTableDecoratorPanel.setMinimumSize(size);
        envFilesTableDecoratorPanel.setPreferredSize(size);

        setLayout(new BorderLayout());
        add(checkboxPanel, BorderLayout.NORTH);
        add(envFilesTableDecoratorPanel, BorderLayout.CENTER);
    }

    private void setUpColumnWidth(TableView<EnvFileEntry> table, int columnIdx, ColumnInfo columnInfo, int extend) {
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

    private void doAddAction(AnActionButton button, final TableView<EnvFileEntry> table, final ListTableModel<EnvFileEntry> model) {
        final JBPopupFactory popupFactory = JBPopupFactory.getInstance();
        DefaultActionGroup actionGroup = new DefaultActionGroup(null, false);

        for (final EnvFileParserExtension extension : EnvFileParserExtension.getParserExtensions()) {
            extension.getId();

            final String title = String.format("%s file", extension.getParser().getTitle());
            AnAction anAction = new AnAction(title) {
                @Override
                public void actionPerformed(AnActionEvent e) {
                    final FileChooserDescriptor chooserDescriptor = FileChooserDescriptorFactory
                            .createSingleFileNoJarsDescriptor()
                            .withTitle(String.format("Select %s", title));

                    Project project = runConfig.getProject();

                    VirtualFile selectedFile = FileChooser.chooseFile(chooserDescriptor, project, null);

                    if (selectedFile != null) {
                        ArrayList<EnvFileEntry> newList = new ArrayList<EnvFileEntry>(model.getItems());
                        EnvFileEntry newOptions = new EnvFileEntry(runConfig, extension.getId(), selectedFile.getPath(), true);
                        newList.add(newOptions);
                        model.setItems(newList);
                        int index = model.getRowCount() - 1;
                        model.fireTableRowsInserted(index, index);
                        table.setRowSelectionInterval(index, index);
                    }
                }
            };
            actionGroup.add(anAction);
        }

        final ListPopup popup =
                popupFactory.createActionGroupPopup("Add...", actionGroup,
                        SimpleDataContext.getProjectContext(runConfig.getProject()), false, false, false, null,
                        -1, Conditions.<AnAction>alwaysTrue());
        popup.show(button.getPreferredPopupPoint());
    }

    EnvFileSettings getState() {
        return new EnvFileSettings(useEnvFileCheckBox.isSelected(), envFilesModel.getItems());
    }

    void setState(EnvFileSettings state) {
        useEnvFileCheckBox.setSelected(state.isEnabled());
        envFilesTable.setEnabled(state.isEnabled());
        envFilesModel.setItems(new ArrayList<EnvFileEntry>(state.getEntries()));
    }
}
