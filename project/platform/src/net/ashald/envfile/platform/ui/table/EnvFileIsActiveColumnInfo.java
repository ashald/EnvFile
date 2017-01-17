package net.ashald.envfile.platform.ui.table;

import com.intellij.util.ui.ColumnInfo;
import net.ashald.envfile.platform.EnvFileEntry;
import org.jetbrains.annotations.Nullable;

public class EnvFileIsActiveColumnInfo extends ColumnInfo<EnvFileEntry, Boolean> {
    public EnvFileIsActiveColumnInfo() {
        super("Enabled");
    }

    @Nullable
    @Override
    public Boolean valueOf(EnvFileEntry envFileEntry) { return envFileEntry.isEnabled(); }

    @Override
    public Class getColumnClass() {
        return Boolean.class;
    }

    @Override
    public void setValue(EnvFileEntry element, Boolean checked) {
        element.setEnable(checked);
    }

    @Override
    public boolean isCellEditable(EnvFileEntry envFileEntry) {
        return true;
    }
}
