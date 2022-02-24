package net.ashald.envfile.platform;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DirenvFileType extends LanguageFileType {
    static final DirenvFileType INSTANCE = new DirenvFileType();

    private DirenvFileType() {
        super(DirenvFileLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Direnv File";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Direnv file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "envrc";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return AllIcons.FileTypes.Config;
    }
}
