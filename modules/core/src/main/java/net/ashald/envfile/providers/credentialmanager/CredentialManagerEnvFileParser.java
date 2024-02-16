package net.ashald.envfile.providers.credentialmanager;

import com.microsoft.credentialstorage.SecretStore;
import com.microsoft.credentialstorage.StorageProvider;
import com.microsoft.credentialstorage.model.StoredCredential;
import lombok.AllArgsConstructor;
import net.ashald.envfile.providers.EnvFileParser;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CredentialManagerEnvFileParser implements EnvFileParser {
    private final Yaml yaml;
    private final SecretStore<StoredCredential> credentialStorage = StorageProvider.getCredentialStorage(true, StorageProvider.SecureOption.REQUIRED);

    @Override
    public Map<String, String> parse(String content) {
        Map<String, Object> root = yaml.load(content);
        ArrayList<String> credentials =(ArrayList<String>)root.get("credential");
        if (credentials != null) {
            return credentials.stream()
                    .map(this::extractSecret)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(c -> c.getUsername(), c -> c.getPassword()));
        }
        return Collections.emptyMap();
    }

    Credential extractSecret(String genericCredential) {
        StoredCredential secretStore = credentialStorage.get(genericCredential);
        if(secretStore != null) {
            return new Credential(secretStore.getUsername(), secretStore.getPassword());
        }
        return null;
    }

    private static class Credential {
        private final String username;
        private final String password;

        public Credential(String username, char[] password) {
            this.username = username;
            this.password = new String(password);
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

    }
}
