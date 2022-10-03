package net.ashald.envfile.providers;

import net.ashald.envfile.exceptions.InvalidEnvFileException;

import java.util.Map;

@FunctionalInterface
public interface EnvFileParser {

    Map<String, String> parse(String content) throws InvalidEnvFileException;
}
