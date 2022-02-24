package net.ashald.envfile.providers.direnv;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DirenvService {

  private static final JsonFactory jsonFactory = new JsonFactory();
  private static final List<Pattern> IGNORES =
      Arrays.stream("^direnv_.*\n^_$\n^shlvl$\n^xpc_.*\n^pwd$\n^oldpwd$".split("\n"))
          .map(p -> Pattern.compile("(?i)" + p))
          .collect(Collectors.toList());

  private Map<String, String> outerEnv;
  private Map<String, String> env = new LinkedHashMap<>();
  private String error = "";

  public Map<String, String> getEnv() {
    return Collections.unmodifiableMap(env);
  }

  public void setOuterEnv(Map<String, String> outerEnv) {
    this.outerEnv = outerEnv;
  }

  public String getError() {
    return error;
  }

  public boolean hasError() {
    return error != null && error.length() != 0;
  }

  public void doExec(File envrcFile) {
    try {
      Process process =
          executeDirenv(
              envrcFile, "${PATH}/direnv", "exec", "/", "${PATH}/direnv", "export", "json");
      if (process.waitFor() != 0) {
        handleError(process);
        System.out.println("ERROR:" + this.error);
        return;
      }
      String result = readFully(process.getInputStream());
      System.out.println("DIRENV:" + result);
      JsonParser parser = jsonFactory.createParser(result);
      handleDirenvOutput(parser);
    } catch (Exception e) {
      e.printStackTrace();
      this.error = e.getMessage();
    }
  }

  public void doAllow(File envrcFile) {
    try {
      Process process = executeDirenv(envrcFile, "${PATH}/direnv", "allow");

      if (process.waitFor() != 0) {
        handleError(process);
      }
    } catch (Exception e) {
      e.printStackTrace();
      this.error = e.getMessage();
    }
  }

  private Map<String, String> filterMap(Map<String, String> env) {
    return env.entrySet().stream()
        .filter(
            e ->
                noMatch(e.getKey())
                    && (noSystemEnvCollision(e.getKey(), e.getValue())
                        || noCurrentPathTailCollision(e.getKey(), e.getValue(), env)))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private void handleDirenvOutput(JsonParser parser) throws IOException {
    Map<String, String> env = new LinkedHashMap<>();
    while (parser.nextToken() != null) {
      if (parser.currentToken() == JsonToken.FIELD_NAME) {
        switch (parser.nextToken()) {
          case VALUE_NULL:
            env.remove(parser.currentName());
            break;
          case VALUE_STRING:
            env.put(parser.currentName(), parser.getValueAsString());
            break;
        }
      }
    }
    this.env = filterMap(env);
  }

  private void handleError(Process process) {
    this.error = readFully(process.getErrorStream());
  }

  private String readFully(InputStream is) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      is.transferTo(baos);
      return baos.toString();
    } catch (IOException e) {
      e.printStackTrace();
      this.error = "Could not read error:" + e.getMessage();
    }
    return "";
  }

  private Process executeDirenv(File envrcFile, String... args) throws IOException {
    File workingDir = envrcFile.getParentFile().getAbsoluteFile();
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.directory(workingDir);
    if (outerEnv != null) {
      processBuilder.environment().putAll(outerEnv);
    }
    List<String> argsList = new ArrayList<>();
    for (String arg : args) {
      if (arg.matches("\\$\\{PATH[}]/?.*")) {
        arg = findInPath(arg.replaceAll("\\$\\{PATH[}]/?", ""));
      }
      argsList.add(arg);
    }
    processBuilder.command(argsList);
    return processBuilder.start();
  }

  private String findInPath(String direnv) {
    String[] paths = outerEnv.get("PATH").split(":");
    for (String path : paths) {
      File potential = Paths.get(path, direnv).toFile();
      if (potential.exists()) {
        return potential.getPath();
      }
    }
    return direnv;
  }

  private static boolean noCurrentPathTailCollision(
      String key, String value, Map<String, String> cenv) {
    return (key.equalsIgnoreCase("path")
        && cenv.containsKey(key)
        && !cenv.get(key).endsWith(value));
  }

  private boolean noSystemEnvCollision(String key, String value) {
    return outerEnv == null
        ? !System.getenv().containsKey(key) || !System.getenv(key).equals(value)
        : !outerEnv.containsKey(key) || !outerEnv.get(key).equals(value);
  }

  private static boolean noMatch(String key) {
    return IGNORES.stream().noneMatch(i -> i.matcher(key).matches());
  }
}
