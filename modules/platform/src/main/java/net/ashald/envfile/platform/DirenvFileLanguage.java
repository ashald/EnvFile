package net.ashald.envfile.platform;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

public class DirenvFileLanguage extends Language {
    static final DirenvFileLanguage INSTANCE = new DirenvFileLanguage();

    private DirenvFileLanguage() {
        super(".envrc");
    }

    @Override
    public boolean isCaseSensitive() {
        return true;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Direnv File";
    }
}
