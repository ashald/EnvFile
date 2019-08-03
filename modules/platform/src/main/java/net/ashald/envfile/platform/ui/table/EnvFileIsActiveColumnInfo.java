package net.ashald.envfile.platform.ui.table;

import com.intellij.ui.BooleanTableCellRenderer;
import com.intellij.util.ui.ColumnInfo;
import net.ashald.envfile.platform.EnvEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class EnvFileIsActiveColumnInfo extends ColumnInfo<EnvEntry, Boolean> {
    public EnvFileIsActiveColumnInfo() {
        super("Enabled");
    }

    @Nullable
    @Override
    public Boolean valueOf(EnvEntry envFileEntry) { return envFileEntry.isEnabled(); }

    @Override
    public Class getColumnClass() {
        return Boolean.class;
    }

    @Override
    public void setValue(EnvEntry element, Boolean checked) {
        element.setEnable(checked);
    }

    @Override
    public boolean isCellEditable(EnvEntry envFileEntry) {
        return envFileEntry.isEditable();
    }

    @Nullable
    @Override
    public TableCellRenderer getRenderer(EnvEntry envFileEntry) {
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
