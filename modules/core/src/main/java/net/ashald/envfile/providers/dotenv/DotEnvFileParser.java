package net.ashald.envfile.providers.dotenv;

import lombok.NonNull;
import net.ashald.envfile.AbstractEnvVarsProvider;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DotEnvFileParser extends AbstractEnvVarsProvider {

    public DotEnvFileParser(boolean shouldSubstituteEnvVar) {
        super(shouldSubstituteEnvVar);
    }

    private static List<String> readAllLines(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        List<String> data = new ArrayList<>();
        for (String line; (line = br.readLine()) != null; ) {
            data.add(line);
        }
        return data;
    }

    @NotNull
    @Override
    protected Map<String, String> getEnvVars(
            Map<String, String> runConfigEnv,
            @NonNull InputStream content,
            @NonNull String path
    ) throws IOException {
        Map<String, String> result = new LinkedHashMap<>();
        List<String> lines = readAllLines(content);
        String multiLineKey = null;
        StringBuilder multiLineValueAccumulator = null;

        for (String l : lines) {
            String strippedLine = l.trim();
            if (strippedLine.startsWith("#")) {
                continue;
            }

            if (multiLineValueAccumulator != null) {
                String strippedLineWithoutComments = removeComments(strippedLine);
                int doubleQuoteIndex = strippedLineWithoutComments.indexOf('"');
                if (doubleQuoteIndex > -1) {
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
                if (key.startsWith("export ")) {
                    key = key.substring(7).trim();
                }
                String rawValue = tokens[1].trim();
                if (rawValue.startsWith("\"") && !rawValue.endsWith("\"")) {
                    multiLineKey = key;
                    multiLineValueAccumulator = new StringBuilder(removeComments(rawValue.substring(1)));
                } else {
                    String value = trim(rawValue);
                    result.put(key, value);
                }
            }
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
