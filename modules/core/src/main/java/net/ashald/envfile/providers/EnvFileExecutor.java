package net.ashald.envfile.providers;

import lombok.val;
import net.ashald.envfile.exceptions.InvalidEnvFileException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Execute given command with given environment, and return standard output
 * Throw {@link InvalidEnvFileException} in case file cannot be executed.
 */
@FunctionalInterface
public interface EnvFileExecutor {
    EnvFileExecutor DEFAULT = new ReadStdoutAsUtf8();

    String execute(String cmd, Map<String, String> environment) throws InvalidEnvFileException;

    class ReadStdoutAsUtf8 implements EnvFileExecutor {

        @Override
        public String execute(String cmd, Map<String, String> environment) throws InvalidEnvFileException {
            val processBuilder = new ProcessBuilder(cmd);
            processBuilder.environment().putAll(environment);
            try {
                val process = processBuilder.start();
                val stdout = process.getInputStream();
                return dumpStream(stdout);
            } catch (IOException e) {
                throw new InvalidEnvFileException(e);
            }
        }


        private static String dumpStream(final InputStream inputStream) {
            val reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            return new BufferedReader(reader)
                    .lines()
                    .collect(Collectors.joining("\n"));
        }
    }
}
