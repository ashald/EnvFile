package net.ashald.envfile.platform.ui.table;

import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.BooleanTableCellRenderer;
import com.intellij.util.ui.ColumnInfo;
import net.ashald.envfile.platform.EnvFileEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;

public class EnvFileIsExecutableColumnInfo extends ColumnInfo<EnvFileEntry, Boolean> {
    public EnvFileIsExecutableColumnInfo() {
        super("Executable");
    }

    @Nullable
    @Override
    public Boolean valueOf(EnvFileEntry envFileEntry) {
        return envFileEntry.getExecutable();
    }

    @Override
    public Class<?> getColumnClass() {
        return Boolean.class;
    }

    @Override
    public void setValue(EnvFileEntry element, Boolean checked) {
        element.setExecutable(checked);
    }

    @Override
    public boolean isCellEditable(EnvFileEntry envFileEntry) {
        return envFileEntry.getPath() != null;
    }

    @Nullable
    @Override
    public TableCellRenderer getRenderer(EnvFileEntry envFileEntry) {
        return new BooleanTableCellRenderer() {
            @NotNull
            @Override
            public Component getTableCellRendererComponent(@NotNull JTable table,
                                                           Object value,
                                                           boolean isSelected,
                                                           boolean hasFocus,
                                                           int row,
                                                           int column) {
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
    }

    @Override
    public @NlsContexts.Tooltip @Nullable String getTooltipText() {
        return "Execute given file and parse content from standard output";
    }
}
