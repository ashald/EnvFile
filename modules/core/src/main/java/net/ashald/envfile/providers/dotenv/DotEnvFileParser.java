package net.ashald.envfile.providers.dotenv;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.ashald.envfile.providers.EnvFileParser;

import java.util.LinkedHashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DotEnvFileParser implements EnvFileParser {
    public static final DotEnvFileParser INSTANCE = new DotEnvFileParser();
    private static final String ANY_NEW_LINE = "\\R";

    @Override
    public Map<String, String> parse(String data) {
        Map<String, String> result = new LinkedHashMap<>();

        String multiLineKey = null;
        StringBuilder multiLineValueAccumulator = null;

        for (String l : data.split(ANY_NEW_LINE)) {
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
