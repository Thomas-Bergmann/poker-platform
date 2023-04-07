package de.hatoka.oidc.internal.remote;

import org.springframework.stereotype.Component;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

@Component
public class IdentityProviderClient
{
    public IdentityProviderMetaDataResponse getMetaData(String metaDataURI)
    {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(metaDataURI);

        return target.request(MediaType.APPLICATION_JSON_TYPE).get(IdentityProviderMetaDataResponse.class);
    }
}
