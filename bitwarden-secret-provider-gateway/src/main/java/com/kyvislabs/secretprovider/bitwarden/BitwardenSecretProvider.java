package com.kyvislabs.secretprovider.bitwarden;

import com.bitwarden.sdk.BitwardenClient;
import com.bitwarden.sdk.BitwardenSettings;
import com.bitwarden.sdk.schema.SecretIdentifierResponse;
import com.inductiveautomation.ignition.common.lifecycle.Lifecycle;
import com.inductiveautomation.ignition.gateway.secrets.*;


import java.io.IOException;
import java.util.*;
import java.io.File;

public class BitwardenSecretProvider implements SecretProvider, Lifecycle {

    BitwardenSettings bitwardenSettings = new BitwardenSettings();
    BitwardenClient bitwardenClient;

    // Instance fields for this class.
    private final SecretProviderContext context;
    private final BitwardenSecretProviderResource settings;

    /**
     * Constructor for the BitwardenSecretProvider.
     *
     * @param context  the {@link SecretProviderContext} encapsulating the contextual information needed for creating
     *                 new {@link SecretProvider} instances of this type.
     * @param settings the {@link BitwardenSecretProviderResource} containing the configuration settings for this
     *                 provider.
     */
    BitwardenSecretProvider(SecretProviderContext context, BitwardenSecretProviderResource settings) {
        this.context = context;
        this.settings = settings;
    }

    @Override
    public void startup() {

        bitwardenSettings.setApiUrl(settings.apiUrl());
        bitwardenSettings.setIdentityUrl(settings.identityUrl());
        bitwardenClient = new BitwardenClient(bitwardenSettings);

        try (Plaintext plainText = Secret.create(context.getGatewayContext(), settings.accessToken()).getPlaintext()){
            var temp_file = File.createTempFile("ignition","secret");
            bitwardenClient.auth().loginAccessToken(plainText.getAsString(), temp_file.getAbsolutePath());

        } catch (IOException | SecretException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void shutdown() {
        bitwardenClient.close();
    }

    @Override
    public List<String> list() throws SecretProviderException {

        return new ArrayList<>(getSecrets().keySet());

    }

    @Override
    public Plaintext read(String s) throws SecretProviderException {
        Objects.requireNonNull(s, "Secret name cannot be null");

        var secrets = getSecrets();
        if (!secrets.containsKey(s)){
            throw new SecretProviderException("Secret does not exist");
        }

        return Plaintext.fromString(bitwardenClient.secrets().get(secrets.get(s)).getValue());

    }

    // Get secrets from the provider.  We want to list secrets by their human-readable name, but the client usually uses the UUID.cccccbgkbhbcnujjcfvuibngcjnbjlghdcehubehnfvh
    private HashMap<String, UUID> getSecrets() {
        var collection = bitwardenClient.secrets().list(UUID.fromString(settings.organizationId()));

        HashMap<String,UUID> secrets = new HashMap<>();
        for (SecretIdentifierResponse secret: collection.getData()) {
            secrets.put(secret.getKey(),secret.getID());
        }

        return secrets;
    }
}
