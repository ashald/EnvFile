package net.ashald.envfile.platform;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class EnvFileType extends LanguageFileType {
    public static final EnvFileType INSTANCE = new EnvFileType();

    private EnvFileType() {
        super(EnvFileLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Env File";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Env File";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "env";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return AllIcons.FileTypes.Config;
    }
}
