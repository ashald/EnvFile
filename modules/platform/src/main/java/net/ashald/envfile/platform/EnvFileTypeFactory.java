package net.ashald.envfile.platform;

import com.intellij.openapi.fileTypes.*;
import org.jetbrains.annotations.NotNull;

public class EnvFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(EnvFileType.INSTANCE, new ExactFileNameMatcher(".env"));
        fileTypeConsumer.consume(EnvFileType.INSTANCE, new WildcardFileNameMatcher("*.env"));
        fileTypeConsumer.consume(EnvFileType.INSTANCE, new WildcardFileNameMatcher(".env.*"));
    }
}
