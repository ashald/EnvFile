package com.cmmoran.envfile.providers;

import com.cmmoran.envfile.exceptions.InvalidEnvFileException;

import java.util.Map;

@FunctionalInterface
public interface EnvFileParser {

    Map<String, String> parse(String content) throws InvalidEnvFileException;
}
