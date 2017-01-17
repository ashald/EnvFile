package net.ashald.envfile.platform.ui.table;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.ColumnInfo;
import net.ashald.envfile.platform.EnvFileEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class EnvFilePathColumnInfo extends ColumnInfo<EnvFileEntry, EnvFileEntry> {
    public EnvFilePathColumnInfo() {
        super("Path");
    }

    @Nullable
    @Override
    public EnvFileEntry valueOf(EnvFileEntry envFileEntry) {
        return envFileEntry;
    }

    @Override
    public TableCellRenderer getRenderer(final EnvFileEntry p0) {
        return new DefaultTableCellRenderer() {
            @NotNull
            @Override
            public Component getTableCellRendererComponent(@NotNull JTable table,
                                                           Object value,
                                                           boolean isSelected,
                                                           boolean hasFocus,
                                                           int row,
                                                           int column) {
                final Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                EnvFileEntry entry = (EnvFileEntry) value;
                setText(entry.getPath());
                setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                setBorder(null);

                if (entry.isEnabled()) {
                    if (!entry.validatePath()) {
                        setForeground(JBColor.RED);
                        setToolTipText("File doesn't exist!");
                    }
                } else {
                    setForeground(JBColor.GRAY);
                }

                return renderer;
            }
        };
    }
}

