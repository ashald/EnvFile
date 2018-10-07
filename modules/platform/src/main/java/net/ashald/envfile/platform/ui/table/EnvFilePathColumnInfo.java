package net.ashald.envfile.platform.ui.table;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.ColumnInfo;
import net.ashald.envfile.platform.EnvFileEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class EnvFilePathColumnInfo extends ColumnInfo<EnvFileEntry, String> {
    public EnvFilePathColumnInfo() {
        super("Path");
    }

    @Override
    public boolean isCellEditable(EnvFileEntry envFileEntry) {
        return envFileEntry.isEnabled();
    }

    @Nullable
    @Override
    public TableCellEditor getEditor(EnvFileEntry envFileEntry) {
        return new DefaultCellEditor(new JBTextField());
    }

    @Override
    public void setValue(EnvFileEntry envFileEntry, String value) {
        envFileEntry.setPath(value == null ? "" : value);
    }

    @Nullable
    @Override
    public String valueOf(EnvFileEntry envFileEntry) {
        return envFileEntry.getPath();
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
                setText(p0.getPath());
                setBackground(table.getBackground());
                setBorder(null);

                if (p0.isEnabled()) {
                    if (!p0.validatePath()) {
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

