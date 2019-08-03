package net.ashald.envfile.platform.ui.table;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.UIUtil;
import net.ashald.envfile.platform.EnvVarsEntry;
import net.ashald.envfile.platform.EnvFileEntry;
import net.ashald.envfile.platform.EnvSingleEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class EnvVarsSourceColumnInfo extends ColumnInfo<EnvVarsEntry, String> {
    public EnvVarsSourceColumnInfo() {
        super("Source");
    }

    @Override
    public boolean isCellEditable(EnvVarsEntry envVarsEntry) {
        return envVarsEntry.isEditable() && envVarsEntry.isEnabled();
    }

    @Nullable
    @Override
    public TableCellEditor getEditor(EnvVarsEntry envVarsEntry) {
        return new DefaultCellEditor(new JBTextField());
    }

    @Override
    public void setValue(EnvVarsEntry envVarsEntry, String value) {
        if (envVarsEntry instanceof EnvFileEntry) {
            ((EnvFileEntry) envVarsEntry).setPath(value == null ? "" : value);
        } else {
            ((EnvSingleEntry) envVarsEntry).setSelectedOption(value == null ? "" : value);
        }
    }

    @Nullable
    @Override
    public String valueOf(EnvVarsEntry envVarsEntry) {
        if (envVarsEntry instanceof EnvFileEntry) {
            return ((EnvFileEntry) envVarsEntry).getPath();
        } else {
            return ((EnvSingleEntry) envVarsEntry).getSelectedOption();
        }
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
                    setText(((EnvSingleEntry) p0).getSelectedOption());
                }

                return renderer;
            }
        };
    }
}
