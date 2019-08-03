package net.ashald.envfile.platform.ui.table;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.UIUtil;
import net.ashald.envfile.platform.EnvEntry;
import net.ashald.envfile.platform.EnvFileEntry;
import net.ashald.envfile.platform.EnvVarEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class EnvFileNameColumnInfo extends ColumnInfo<EnvEntry, String> {
    public EnvFileNameColumnInfo() {
        super("Name");
    }

    @Override
    public boolean isCellEditable(EnvEntry envFileEntry) {
        return envFileEntry.isEditable() && envFileEntry.isEnabled();
    }

    @Nullable
    @Override
    public TableCellEditor getEditor(EnvEntry envFileEntry) {
        return new DefaultCellEditor(new JBTextField());
    }

    @Override
    public void setValue(EnvEntry envFileEntry, String value) {
       // envFileEntry.setPath(value == null ? "" : value);
    }

    @Nullable
    @Override
    public String valueOf(EnvEntry envFileEntry) {
        //return envFileEntry.getPath();
        return "";
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
                setBorder(null);
                if (p0.getParserId().equals("runconfig")) { // TODO make generic
                    setText("<Run Configuration Env Vars>");
                    setForeground(UIUtil.getLabelDisabledForeground());
                } else if (p0 instanceof EnvFileEntry) {
                    setText(((EnvFileEntry) p0).getPath());

                    if (p0.isEnabled()) {
                        if (!((EnvFileEntry) p0).validatePath()) {
                            setForeground(JBColor.RED);
                            setToolTipText("File doesn't exist!");
                        }
                    } else {
                        setForeground(UIUtil.getLabelDisabledForeground());
                    }
                }

                else {
                    setText(((EnvVarEntry) p0).getEnvVarName());
                }

                return renderer;
            }
        };
    }
}
