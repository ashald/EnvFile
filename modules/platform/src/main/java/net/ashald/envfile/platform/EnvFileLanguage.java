package net.ashald.envfile.platform;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

public class EnvFileLanguage extends Language {
    static final EnvFileLanguage INSTANCE = new EnvFileLanguage();

    private EnvFileLanguage() {
        super(".env");
    }

    @Override
    public boolean isCaseSensitive() {
        return true;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Env File";
    }
}
