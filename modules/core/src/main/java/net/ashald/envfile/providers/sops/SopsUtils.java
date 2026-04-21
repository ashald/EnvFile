package net.ashald.envfile.providers.sops;

import net.ashald.envfile.exceptions.InvalidEnvFileException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class SopsUtils {

    private static Supplier<String> executableSupplier = null;
    public static void setExecutableSupplier(Supplier<String> supplier) {
        executableSupplier = supplier;
    }

    public static final List<String> SOPS_KEYWORDS = List.of("sops", "lastmodified", "version");

    public static boolean isSopsFile(final String content) {
        return SOPS_KEYWORDS.stream().allMatch(content::contains);
    }

    public static String decrypt(final File file) throws InvalidEnvFileException {
        String executable = executableSupplier != null ? executableSupplier.get() : "sops";
        ProcessBuilder builder = new ProcessBuilder(executable, "-d", file.getAbsolutePath());
        builder.redirectErrorStream(true);
        try {
            Process p = builder.start();
            p.waitFor(10, TimeUnit.SECONDS);
            String content;
            try (Scanner scanner = new Scanner(p.getInputStream(), StandardCharsets.UTF_8)) {
                content = scanner.useDelimiter("\\A").next();
            }
            if (p.exitValue() != 0) {
                throw new InvalidEnvFileException("""
                        Failed to decrypt file %s
                        SOPS exit code: %d
                        %s""".formatted(file.getAbsolutePath(), p.exitValue(), content));
            }
            return content;
        } catch (IOException | InterruptedException e) {
            throw new InvalidEnvFileException("Failed to decrypt file " + file.getAbsolutePath(), e);
        }
    }
}
