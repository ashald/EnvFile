package net.ashald.envfile.platform.ui.table;

import com.intellij.ui.BooleanTableCellRenderer;
import com.intellij.util.ui.ColumnInfo;
import net.ashald.envfile.EnvVarsProviderFactory;
import net.ashald.envfile.platform.EnvFileEntry;
import net.ashald.envfile.platform.EnvVarsProviderExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;

public class EnvFileIsActiveColumnInfo extends ColumnInfo<EnvFileEntry, Boolean> {
    public EnvFileIsActiveColumnInfo() {
        super("Enabled");
    }

    @Nullable
    @Override
    public Boolean valueOf(EnvFileEntry envFileEntry) {
        return envFileEntry.isEnabled();
    }

    @Override
    public Class<?> getColumnClass() {
        return Boolean.class;
    }

    @Override
    public void setValue(EnvFileEntry element, Boolean checked) {
        element.setEnabled(checked);
    }

    @Override
    public boolean isCellEditable(EnvFileEntry envFileEntry) {
        return EnvVarsProviderExtension.getParserFactoryById(envFileEntry.getParserId())
                .map(EnvVarsProviderFactory::isEditable)
                .orElse(true);
    }

    @Nullable
    @Override
    public TableCellRenderer getRenderer(EnvFileEntry envFileEntry) {
        return new BooleanTableCellRenderer() {
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
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
    }
}
