package net.ashald.envfile.platform;

import com.intellij.openapi.fileTypes.*;
import org.jetbrains.annotations.NotNull;

public class EnvFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(EnvFileType.INSTANCE, new ExactFileNameMatcher(".env"));
        fileTypeConsumer.consume(DirenvFileType.INSTANCE, new ExactFileNameMatcher(".envrc"));
        fileTypeConsumer.consume(EnvFileType.INSTANCE, new ExtensionFileNameMatcher("env"));
        fileTypeConsumer.consume(EnvFileType.INSTANCE, new WildcardFileNameMatcher(".env.*"));
        fileTypeConsumer.consume(DirenvFileType.INSTANCE, new WildcardFileNameMatcher(".envrc.*"));
    }
}
