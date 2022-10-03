package net.ashald.envfile.platform.ui.table;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.UIUtil;
import lombok.val;
import net.ashald.envfile.EnvVarsProviderFactory;
import net.ashald.envfile.platform.EnvFileEntry;
import net.ashald.envfile.platform.EnvVarsProviderExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class EnvFileTypeColumnInfo extends ColumnInfo<EnvFileEntry, EnvFileEntry> {
    public EnvFileTypeColumnInfo() {
        super("Type");
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
            public Component getTableCellRendererComponent(
                    @NotNull JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {
                final Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                EnvFileEntry entry = (EnvFileEntry) value;
                val typeTitle = EnvVarsProviderExtension.getParserFactoryById(entry.getParserId())
                        .map(EnvVarsProviderFactory::getTitle)
                        .orElse(String.format("<%s>", entry.getParserId()));
                setText(typeTitle);
                setBorder(null);

                if (entry.isEnabled()) {
                    if (!EnvVarsProviderExtension.getParserFactoryById(entry.getParserId()).isPresent()) {
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
