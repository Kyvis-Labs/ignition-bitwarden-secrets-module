package com.kyvislabs.secretprovider.bitwarden;

import com.inductiveautomation.ignition.gateway.config.AbstractExtensionPoint;
import com.inductiveautomation.ignition.gateway.config.ExtensionPointConfig;
import com.inductiveautomation.ignition.gateway.config.ValidationErrors;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.SchemaUtil;
import com.inductiveautomation.ignition.gateway.secrets.*;
import com.inductiveautomation.ignition.gateway.web.nav.ExtensionPointResourceForm;
import com.inductiveautomation.ignition.gateway.web.nav.WebUiComponent;

import java.util.Optional;

public class BitwardenSecretProviderExtensionPoint
        extends AbstractExtensionPoint<BitwardenSecretProviderResource>
        implements SecretProviderType<BitwardenSecretProviderResource> {

    public static final String EXTENSION_POINT_TYPE = "BITWARDEN";

    public BitwardenSecretProviderExtensionPoint() {
        super(EXTENSION_POINT_TYPE,
                "BitwardenSecretProvider.SecretProviderType.Name",
                "BitwardenSecretProvider.SecretProviderType.Desc");
    }

    public SecretProvider createProvider(SecretProviderContext context) throws SecretProviderTypeException {
        ExtensionPointConfig<SecretProviderConfig, ?> config = context.getResource().config();
        BitwardenSecretProviderResource settings = getSettings(config)
                .orElseThrow(() -> new IllegalStateException("Secret provider configuration missing for: "
                        + context.getResource().name()));
        return new BitwardenSecretProvider(context, settings);
    }

    @Override
    public Optional<BitwardenSecretProviderResource> defaultSettings() {
        return Optional.of(BitwardenSecretProviderResource.DEFAULT);
    }

    @Override
    public Optional<WebUiComponent> getWebUiComponent(ComponentType type) {
        return Optional.of(
                new ExtensionPointResourceForm(
                        SecretProviderConfig.RESOURCE_TYPE,
                        "Secret Provider",
                        EXTENSION_POINT_TYPE,
                        SchemaUtil.fromType(SecretProviderConfig.class),
                        SchemaUtil.fromType(BitwardenSecretProviderResource.class)
                )
        );
    }

    @Override
    protected void validate(BitwardenSecretProviderResource settings, ValidationErrors.Builder errors) {
        /*
         Optionally, add validation to an incoming configuration object
         These error messages will be conveyed back to the standard web UI automatically
        */
        // errors.requireNotNull("someField", settings.auditProfileName());
        super.validate(settings, errors);
    }
}
