package com.kyvislabs.secretprovider.bitwarden;

import com.inductiveautomation.ignition.common.resourcecollection.ResourceType;
import com.inductiveautomation.ignition.gateway.config.ResourceTypeMeta;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.*;
import com.inductiveautomation.ignition.gateway.secrets.SecretConfig;
import com.inductiveautomation.ignition.gateway.web.nav.FormFieldType;
import org.apache.commons.lang3.StringUtils;

/**
 * Configuration for a MongoDB secret provider. This resource will be persisted to disk as part of the secret
 * provider config.
 */
public record BitwardenSecretProviderResource(
        @FormCategory("CUSTOM SETTINGS")
        @Label("API URL")
        @FormField(FormFieldType.TEXT)
        @DefaultValue("https://api.bitwarden.com")
        @Required
//        @DescriptionKey("MongoDbSecretProviderResource.connectionString.Desc")
        @Description("The Bitwarden API URL.")
        String apiUrl,

        @FormCategory("CUSTOM SETTINGS")
        @Label("Identity URL")
        @FormField(FormFieldType.TEXT)
        @DefaultValue("https://identity.bitwarden.com")
        @Required
//        @DescriptionKey("MongoDbSecretProviderResource.databaseName.Desc")
        @Description("The client ID of your Machine Identity.")
        String identityUrl,

        @FormCategory("CUSTOM SETTINGS")
        @Label("Access Token")
        @FormField(FormFieldType.SECRET)
//        @DescriptionKey("MongoDbSecretProviderResource.username.Desc")
        @Description("The access token to authenticate with")
        SecretConfig accessToken,

        @FormCategory("CUSTOM SETTINGS")
        @Label("Organization ID")
        @FormField(FormFieldType.TEXT)
//        @DescriptionKey("MongoDbSecretProviderResource.password.Desc")
        @Description("Your organization ID")
        String organizationId,

        @FormCategory("CUSTOM SETTINGS")
        @Label("Project ID")
        @FormField(FormFieldType.TEXT)
//        @DescriptionKey("MongoDbSecretProviderResource.authenticationDb.Desc")
        @Description("The path to the secrets.")
        String projectId
) {
    public static final ResourceType RESOURCE_TYPE = new ResourceType(GatewayHook.MODULE_ID, "bitwarden-secret-provider");

    public static final BitwardenSecretProviderResource DEFAULT = new BitwardenSecretProviderResource(
            "https://api.bitwarden.com",
            "https://identity.bitwarden.com",
            null,
            "",
            ""
    );

    public static final ResourceTypeMeta<BitwardenSecretProviderResource> META = ResourceTypeMeta.newBuilder(BitwardenSecretProviderResource.class)
            .resourceType(RESOURCE_TYPE)
            .categoryName("Bitwarden Secrets Manager")
            .defaultConfig(DEFAULT)
            .buildValidator((resource, validator) -> {
                // Custom validation logic for the resource. This gets called anytime the resource system creates
                // an instance of this resource, such as when a secret provider is created, updated, or loaded.
            })
            .build();

    /**
     * Canonical constructor that fills in default values for any null or blank parameters.
     *
     * @param apiUrl           The Bitwarden API URL.
     * @param identityUrl       The name of the database containing secret provider documents.
     * @param accessToken         The ID of your project.
     * @param organizationId   The ID of your project.
     * @param projectId        The path to the secrets.
     */
    public BitwardenSecretProviderResource {
        if (StringUtils.isBlank(apiUrl)) {
            apiUrl = DEFAULT.apiUrl();
        }

        if (StringUtils.isBlank(identityUrl)) {
            identityUrl = DEFAULT.identityUrl();
        }

    }
}
