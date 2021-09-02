package net.ashald.envfile.providers.dotenv;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import net.ashald.envfile.AbstractEnvVarsProvider;
import net.ashald.envfile.EnvFileErrorException;

public class DotEnvFileParser extends AbstractEnvVarsProvider {

    public DotEnvFileParser(boolean shouldSubstituteEnvVar) {
        super(shouldSubstituteEnvVar);
    }

    @NotNull
    @Override
    protected Map<String, String> getEnvVars(@NotNull Map<String, String> runConfigEnv, @NotNull String path) throws EnvFileErrorException {
        Map<String, String> result = new LinkedHashMap<>();

        try {
            List<String> lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
            String multiLineKey = null;
            StringBuilder multiLineValueAccumulator = null;
            for (String l: lines) {
                String strippedLine = l.trim();
                if (strippedLine.startsWith("#")) {
                    continue;
                }

                if(multiLineValueAccumulator != null) {
                    String strippedLineWithoutComments = removeComments(strippedLine);
                    int doubleQuoteIndex = strippedLineWithoutComments.indexOf('"');
                    if(doubleQuoteIndex > -1) {
                        multiLineValueAccumulator.append("\n").append(strippedLineWithoutComments, 0, doubleQuoteIndex);
                        result.put(multiLineKey, multiLineValueAccumulator.toString());
                        multiLineKey = null;
                        multiLineValueAccumulator = null;
                    } else {
                        multiLineValueAccumulator.append("\n").append(strippedLineWithoutComments);
                    }
                } else if (strippedLine.contains("=")) {
                    String[] tokens = strippedLine.split("=", 2);
                    String key = tokens[0];
                    String rawValue = tokens[1].trim();
                    if(rawValue.startsWith("\"") && !rawValue.endsWith("\"")) {
                        multiLineKey = key;
                        multiLineValueAccumulator = new StringBuilder(removeComments(rawValue.substring(1)));
                    } else {
                        String value = trim(rawValue);
                        result.put(key, value);
                    }
                }
            }
        } catch (IOException ex) {
            throw new EnvFileErrorException(ex);
        }

        return result;
    }

    private static String trim(String value) {
        String trimmed = value.trim();

        if ((trimmed.startsWith("\"") && trimmed.endsWith("\"")) || (trimmed.startsWith("'") && trimmed.endsWith("'")))
            return trimmed.substring(1, trimmed.length() - 1).replace("\\n", "\n");

        return removeComments(trimmed);
    }

    private static String removeComments(String trimmed) {
        return trimmed.replaceAll("\\s#.*$", "").replaceAll("(\\s)\\\\#", "$1#").trim();
    }
}
