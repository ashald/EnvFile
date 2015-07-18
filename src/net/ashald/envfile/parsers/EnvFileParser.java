package net.ashald.envfile.parsers;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;


public interface EnvFileParser {

    @NotNull Map<String, String> readFile(@NotNull String path) throws EnvFileErrorException, IOException;

    @NotNull String getTitle();
}
