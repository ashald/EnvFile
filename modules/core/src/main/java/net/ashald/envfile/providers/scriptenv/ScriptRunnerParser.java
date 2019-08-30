package net.ashald.envfile.providers.scriptenv;

import com.intellij.openapi.diagnostic.Logger;
import net.ashald.envfile.EnvFileErrorException;
import net.ashald.envfile.providers.dotenv.DotEnvFileParser;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScriptRunnerParser extends DotEnvFileParser {

    public ScriptRunnerParser(boolean shouldSubstituteEnvVar) {
        super(shouldSubstituteEnvVar);
    }

    @NotNull
    @Override
    protected Map<String, String> getEnvVars(@NotNull Map<String, String> runConfigEnv, @NotNull String path) throws EnvFileErrorException {

        ProcessBuilder processBuilder = new ProcessBuilder(path);
        try {
            // set the CWD to the base folder of the referenced file
            processBuilder.directory(Paths.get(path).getParent().toFile());
            processBuilder.redirectErrorStream(true);

            // start the process
            Process process = processBuilder.start();

            // wait here until it finishes (should we set a timeout here?)
            int exitVal = process.waitFor();

            List<String> returnedLines =
                    new BufferedReader(new InputStreamReader(process.getInputStream(),
                            StandardCharsets.UTF_8)).lines().collect(Collectors.toList());

            String allLines = StringUtils.join(returnedLines, "\n");
            if (exitVal != 0) {
                throw new EnvFileErrorException("EnvFile provider " + path + " returned a non-zero exit code, " + exitVal + ". \n" + allLines);
            }

            Logger.getInstance(ScriptRunnerParser.class).debug(
                    "Got following results from " + path + ": \n" + allLines);

            // parse the returned output as .env content and return it to the RunConfig
            return parseLines(returnedLines);


        } catch (IOException | InterruptedException e) {
            throw new EnvFileErrorException(e);
        }

    }

}