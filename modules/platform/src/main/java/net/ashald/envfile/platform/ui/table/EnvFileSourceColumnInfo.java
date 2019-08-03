package net.ashald.envfile.platform.ui.table;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.UIUtil;
import net.ashald.envfile.platform.EnvEntry;
import net.ashald.envfile.platform.EnvFileEntry;
import net.ashald.envfile.platform.EnvVarEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class EnvFileSourceColumnInfo extends ColumnInfo<EnvEntry, EnvEntry> {
    public EnvFileSourceColumnInfo() {
        super("Source");
    }

    @Nullable
    @Override
    public EnvEntry valueOf(EnvEntry envFileEntry) {
        return envFileEntry;
    }

    @Override
    public TableCellRenderer getRenderer(final EnvEntry p0) {
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
                EnvEntry entry = (EnvEntry) value;

                if (entry instanceof EnvFileEntry) {
                    setText(((EnvFileEntry) entry).getPath());
                } else {
                    setText(((EnvVarEntry)entry).getSelectedOption());
                }
                setBorder(null);

                if (entry.isEnabled()) {
                    if (!entry.validateType()) {
                        setForeground(JBColor.RED);
                        setToolTipText("Parser not found!");
                    }


                } else {
                    setForeground(UIUtil.getLabelDisabledForeground());
                }

                return renderer;
            }
        };
    }
}
