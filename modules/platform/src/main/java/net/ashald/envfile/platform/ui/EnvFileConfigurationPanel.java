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
import net.ashald.envfile.platform.EnvFileEntry;
import net.ashald.envfile.platform.EnvFileSettings;
import net.ashald.envfile.platform.EnvVarsProviderExtension;
import net.ashald.envfile.platform.ui.table.EnvFileIsActiveColumnInfo;
import net.ashald.envfile.platform.ui.table.EnvFilePathColumnInfo;
import net.ashald.envfile.platform.ui.table.EnvFileTypeColumnInfo;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

class EnvFileConfigurationPanel<T extends RunConfigurationBase<?>> extends JPanel {
  private static final int MAX_RECENT = 5;
  private static final LinkedList<EnvFileEntry> recent = new LinkedList<>();
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
    ColumnInfo<EnvFileEntry, Boolean> IS_ACTIVE = new EnvFileIsActiveColumnInfo();
    ColumnInfo<EnvFileEntry, String> FILE = new EnvFilePathColumnInfo();
    ColumnInfo<EnvFileEntry, EnvFileEntry> TYPE = new EnvFileTypeColumnInfo();

    envFilesModel = new ListTableModel<>(IS_ACTIVE, FILE, TYPE);

    // Create Table
    envFilesTable = new TableView<>(envFilesModel);
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
    useEnvFileCheckBox.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            envFilesTable.setEnabled(useEnvFileCheckBox.isSelected());
            substituteEnvVarsCheckBox.setEnabled(useEnvFileCheckBox.isSelected());
            supportPathMacroCheckBox.setEnabled(useEnvFileCheckBox.isSelected());
            ignoreMissingCheckBox.setEnabled(useEnvFileCheckBox.isSelected());
          }
        });
    substituteEnvVarsCheckBox =
        new JCheckBox("Substitute Environment Variables (${FOO} / ${BAR:-default} / $${ESCAPED})");
    substituteEnvVarsCheckBox.addActionListener(
        e ->
            envFilesModel
                .getItems()
                .forEach(
                    envFileEntry ->
                        envFileEntry.setSubstitutionEnabled(
                            substituteEnvVarsCheckBox.isSelected())));
    supportPathMacroCheckBox =
        new JCheckBox("Process JetBrains path macro references ($PROJECT_DIR$)");
    ignoreMissingCheckBox = new JCheckBox("Ignore missing files");
    experimentalIntegrationsCheckBox =
        new JCheckBox("Enable experimental integrations (e.g. Gradle) - may break any time!");

    // TODO: come up with a generic approach for this
    envFilesModel.addRow(
        new EnvFileEntry(
            runConfig, "runconfig", null, true, substituteEnvVarsCheckBox.isSelected()));

    addPossibleEnvrc(envFilesModel, runConfig);
    // Create Toolbar - Add/Remove/Move actions
    final ToolbarDecorator envFilesTableDecorator = ToolbarDecorator.createDecorator(envFilesTable);

    final AnActionButtonUpdater updater = e -> useEnvFileCheckBox.isSelected();

    envFilesTableDecorator
        .setAddAction(button -> doAddAction(button, envFilesTable, envFilesModel))
        .setAddActionUpdater(updater)
        .setRemoveActionUpdater(
            e -> {
              boolean allEditable = true;

              for (EnvFileEntry entry : envFilesTable.getSelectedObjects()) {
                if (!entry.isEditable()) {
                  allEditable = false;
                  break;
                }
              }

              return updater.isEnabled(e)
                  && envFilesTable.getSelectedRowCount() >= 1
                  && allEditable;
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

  private void addPossibleEnvrc(
      ListTableModel<EnvFileEntry> envFilesModel, RunConfigurationBase<?> configuration) {
    @SuppressWarnings("deprecation") VirtualFile envrcFile =
        configuration.getProject().getBaseDir().findFileByRelativePath(".envrc");
    if (Objects.requireNonNull(envrcFile).exists()) {
      envFilesModel.addRow(
          new EnvFileEntry(
              runConfig,
              "direnv",
              envrcFile.getPath(),
              true,
              substituteEnvVarsCheckBox.isSelected()));
    }
  }

  private void setUpColumnWidth(
      TableView<EnvFileEntry> table,
      int columnIdx,
      ColumnInfo<EnvFileEntry, ?> columnInfo,
      int extend) {
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
      final ListTableModel<EnvFileEntry> model) {
    DefaultActionGroup actionGroup = new DefaultActionGroup(null, false);

    for (final EnvVarsProviderExtension extension :
        EnvVarsProviderExtension.getParserExtensions()) {
      if (!extension
          .getFactory()
          .createProvider(substituteEnvVarsCheckBox.isSelected())
          .isEditable()) {
        continue;
      }

      final String title = String.format("%s file", extension.getFactory().getTitle());
      AnAction anAction =
          new AnAction(title) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
              final FileChooserDescriptor chooserDescriptor =
                  FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor()
                      .withTitle(String.format("Select %s", title));

              Project project = runConfig.getProject();

              VirtualFile selectedFile = FileChooser.chooseFile(chooserDescriptor, project, null);

              if (selectedFile != null) {
                addSelectedFile(selectedFile, model, extension, table);
              }
            }
          };
      actionGroup.add(anAction);
    }
    synchronized (recent) {
      if (!recent.isEmpty()) {
        actionGroup.addSeparator("Recent");

        for (final EnvFileEntry entry : recent) {
          String title = String.format("%s -> %s", entry.getTypeTitle(), entry.getPath());
          String shortTitle =
              title.length() < 81 ? title : title.replaceFirst("(.{39}).+(.{39})", "$1...$2");
          AnAction anAction =
              new AnAction(shortTitle, title, null) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                  ArrayList<EnvFileEntry> newList = new ArrayList<>(model.getItems());
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

    final String popupPlace = ActionPlaces.getActionGroupPopupPlace(getClass().getSimpleName());
    final ListPopup popup =
        new PopupFactoryImpl.ActionGroupPopup(
            "Add...",
            actionGroup,
            DataManager.getInstance().getDataContext(this),
            false,
            false,
            false,
            false,
            null,
            -1,
            Conditions.alwaysTrue(),
            popupPlace);

    popup.show(Objects.requireNonNull(button.getPreferredPopupPoint()));
  }

  private void addSelectedFile(VirtualFile selectedFile, ListTableModel<EnvFileEntry> model, EnvVarsProviderExtension extension, TableView<EnvFileEntry> table) {
    String selectedPath = selectedFile.getPath();
    String baseDir = runConfig.getProject().getBasePath();
    assert baseDir != null;
    if (selectedPath.startsWith(baseDir)) {
      selectedPath = selectedPath.substring(baseDir.length() + 1);
    }

    ArrayList<EnvFileEntry> newList = new ArrayList<>(model.getItems());
    final EnvFileEntry newOptions =
        new EnvFileEntry(
            runConfig,
            extension.getId(),
            selectedPath,
            true,
            substituteEnvVarsCheckBox.isSelected());
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
        envFilesModel.getItems(),
        ignoreMissingCheckBox.isSelected(),
        experimentalIntegrationsCheckBox.isSelected());
  }

  void setState(EnvFileSettings state) {
    useEnvFileCheckBox.setSelected(state.isEnabled());
    substituteEnvVarsCheckBox.setSelected(state.isSubstituteEnvVarsEnabled());
    supportPathMacroCheckBox.setSelected(state.isPathMacroSupported());
    ignoreMissingCheckBox.setSelected(state.isIgnoreMissing());
    experimentalIntegrationsCheckBox.setSelected(state.isEnableExperimentalIntegrations());

    envFilesTable.setEnabled(state.isEnabled());
    substituteEnvVarsCheckBox.setEnabled(state.isEnabled());
    supportPathMacroCheckBox.setEnabled(state.isEnabled());
    ignoreMissingCheckBox.setEnabled(state.isEnabled());
    experimentalIntegrationsCheckBox.setEnabled(state.isEnabled());

    envFilesModel.setItems(new ArrayList<>(state.getEntries()));
  }
}
