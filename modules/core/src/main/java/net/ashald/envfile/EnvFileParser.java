package net.ashald.envfile;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;


public interface EnvFileParser {

    @NotNull Map<String, String> process(@NotNull String path, @NotNull Map<String, String> source) throws EnvFileErrorException, IOException;

}
