package net.ashald.envfile.providers.dotenv;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        Pattern doublequote = Pattern.compile("^(.*[^\\\\])?\"");
        Pattern quotedValue = Pattern.compile("^\"(.*[^\\\\])?\"$");

        String multiLineKey = null;
        StringBuilder multiLineValueAccumulator = null;

        for (String l : data.split(ANY_NEW_LINE)) {
            String strippedLine = l.trim();
            if (strippedLine.startsWith("#")) {
                continue;
            }

            if (multiLineValueAccumulator != null) {
                String strippedLineWithoutComments = removeComments(strippedLine);
                Matcher valueEndMatcher = doublequote.matcher(strippedLineWithoutComments);
                if(valueEndMatcher.find()){
                    int doubleQuoteIndex = valueEndMatcher.end() - 1;
                    multiLineValueAccumulator.append("\n").append(removeEscapedDoubleQuotes(strippedLineWithoutComments), 0, doubleQuoteIndex);
                    result.put(multiLineKey, multiLineValueAccumulator.toString());
                    multiLineKey = null;
                    multiLineValueAccumulator = null;
                } else {
                    multiLineValueAccumulator.append("\n").append(removeEscapedDoubleQuotes(strippedLineWithoutComments));
                }
            } else if (strippedLine.contains("=")) {
                String[] tokens = strippedLine.split("=", 2);
                String key = tokens[0];
                if (key.startsWith("export ")) {
                    key = key.substring(7).trim();
                }
                String rawValue = tokens[1].trim();
                Matcher quotedValueMatcher = quotedValue.matcher(rawValue);
                if (!rawValue.startsWith("\"") || quotedValueMatcher.find()) {
                    String value = trim(rawValue);
                    result.put(key, removeEscapedDoubleQuotes(value));
                } else {
                    multiLineKey = key;
                    String value = removeComments(rawValue.substring(1));
                    value = removeEscapedDoubleQuotes(value);
                    multiLineValueAccumulator = new StringBuilder(value);
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

    private static String removeEscapedDoubleQuotes(String value) {
        return value.replaceAll("\\\\\"", "\"");
    }
}
