package net.ashald.envfile.providers;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.val;
import net.ashald.envfile.exceptions.InvalidEnvFileException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Execute given command with given environment, and return standard output
 * Throw {@link InvalidEnvFileException} in case file cannot be executed.
 */
@FunctionalInterface
public interface EnvFileExecutor {
    EnvFileExecutor DEFAULT = new ReadStdoutAsUtf8();

    Output execute(String cmd, Map<String, String> environment) throws InvalidEnvFileException;

    @Value
    @Builder
    class Output {
        @NonNull
        private final Stream<String> stderr;

        @NonNull
        private final Stream<String> stdout;
    }

    class ReadStdoutAsUtf8 implements EnvFileExecutor {

        @Override
        public Output execute(String cmd, Map<String, String> environment) throws InvalidEnvFileException {
            val processBuilder = new ProcessBuilder(cmd);
            processBuilder.environment().putAll(environment);
            try {
                val process = processBuilder.start();
                val stdout = process.getInputStream();
                val stderr = process.getErrorStream();
                return Output.builder()
                        .stdout(
                                streamLines(stdout)
                        )
                        .stderr(
                                streamLines(stderr)
                        )
                        .build();
            } catch (IOException e) {
                throw new InvalidEnvFileException(e);
            }
        }

        private static Stream<String> streamLines(final InputStream inputStream) {
            val reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            return new BufferedReader(reader)
                    .lines();
        }
    }
}
