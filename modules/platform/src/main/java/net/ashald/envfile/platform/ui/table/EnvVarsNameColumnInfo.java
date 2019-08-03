package net.ashald.envfile.platform.ui.table;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.UIUtil;
import net.ashald.envfile.platform.EnvVarsEntry;
import net.ashald.envfile.platform.EnvFileEntry;
import net.ashald.envfile.platform.EnvSingleEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class EnvVarsNameColumnInfo extends ColumnInfo<EnvVarsEntry, EnvVarsEntry> {
    public EnvVarsNameColumnInfo() {
        super("Name");
    }

    @Nullable
    @Override
    public EnvVarsEntry valueOf(EnvVarsEntry envFileEntry) {
        return envFileEntry;
    }

    @Override
    public TableCellRenderer getRenderer(final EnvVarsEntry p0) {
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
                EnvVarsEntry entry = (EnvVarsEntry) value;

                if (entry instanceof EnvFileEntry) {
                    String path = ((EnvFileEntry) entry).getPath();
                    if (path != null) {
                        String[] splittedPath = path.split("/");
                        setText(splittedPath[splittedPath.length - 1]);
                    }
                } else {
                    setText(((EnvSingleEntry)entry).getEnvVarName());
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
