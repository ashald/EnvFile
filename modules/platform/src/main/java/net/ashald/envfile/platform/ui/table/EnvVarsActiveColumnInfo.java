package net.ashald.envfile.platform.ui.table;

import com.intellij.ui.BooleanTableCellRenderer;
import com.intellij.util.ui.ColumnInfo;
import net.ashald.envfile.platform.EnvVarsEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class EnvVarsActiveColumnInfo extends ColumnInfo<EnvVarsEntry, Boolean> {
    public EnvVarsActiveColumnInfo() {
        super("Enabled");
    }

    @Nullable
    @Override
    public Boolean valueOf(EnvVarsEntry envVarsEntry) { return envVarsEntry.isEnabled(); }

    @Override
    public Class getColumnClass() {
        return Boolean.class;
    }

    @Override
    public void setValue(EnvVarsEntry element, Boolean checked) {
        element.setEnable(checked);
    }

    @Override
    public boolean isCellEditable(EnvVarsEntry envFileEntry) {
        return envFileEntry.isEditable();
    }

    @Nullable
    @Override
    public TableCellRenderer getRenderer(EnvVarsEntry envFileEntry) {
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
}
