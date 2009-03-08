package de.hatoka.oidc.internal.remote;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains content of meta data request https://login.microsoftonline.com/tenantid/v2.0/.well-known/openid-configuration
 * <pre>{
    "token_endpoint":"https://login.microsoftonline.com/tenantid/oauth2/v2.0/token",
    "token_endpoint_auth_methods_supported":["client_secret_post","private_key_jwt","client_secret_basic"],
    "jwks_uri":"https://login.microsoftonline.com/tenantid/discovery/v2.0/keys",
    "response_modes_supported":["query","fragment","form_post"],
    "subject_types_supported":["pairwise"],
    "id_token_signing_alg_values_supported":["RS256"],
    "response_types_supported":["code","id_token","code id_token","id_token token"],
    "scopes_supported":["openid","profile","email","offline_access"],
    "issuer":"https://login.microsoftonline.com/tenantid/v2.0",
    "request_uri_parameter_supported":false,
    "userinfo_endpoint":"https://graph.microsoft.com/oidc/userinfo",
    "authorization_endpoint":"https://login.microsoftonline.com/tenantid/oauth2/v2.0/authorize",
    "device_authorization_endpoint":"https://login.microsoftonline.com/tenantid/oauth2/v2.0/devicecode",
    "http_logout_supported":true,
    "frontchannel_logout_supported":true,
    "end_session_endpoint":"https://login.microsoftonline.com/tenantid/oauth2/v2.0/logout",
    "claims_supported":["sub","iss","cloud_instance_name","cloud_instance_host_name","cloud_graph_host_name","msgraph_host","aud","exp","iat","auth_time","acr","nonce","preferred_username","name","tid","ver","at_hash","c_hash","email"],
    "kerberos_endpoint":"https://login.microsoftonline.com/tenantid/kerberos",
    "tenant_region_scope":"EU",
    "cloud_instance_name":"microsoftonline.com",
    "cloud_graph_host_name":"graph.windows.net",
    "msgraph_host":"graph.microsoft.com",
    "rbac_url":"https://pas.windows.net"
} </pre>
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdentityProviderMetaDataResponse
{
    @JsonProperty("token_endpoint")
    private String tokenEndpoint;
    @JsonProperty("jwks_uri")
    private String jwkSetUri;
    @JsonProperty("scopes_supported")
    private String[] supportedScopes;
    @JsonProperty("issuer")
    private String issuer;
    @JsonProperty("userinfo_endpoint")
    private String userInfoEndpoint;
    @JsonProperty("authorization_endpoint")
    private String authorizationEndpoint;
    @JsonProperty("end_session_endpoint")
    private String endSessionEndpoint;
    public String getTokenEndpoint()
    {
        return tokenEndpoint;
    }
    public void setTokenEndpoint(String tokenEndpoint)
    {
        this.tokenEndpoint = tokenEndpoint;
    }
    public String getJwkSetUri()
    {
        return jwkSetUri;
    }
    public void setJwkSetUri(String jwkSetUri)
    {
        this.jwkSetUri = jwkSetUri;
    }
    public String[] getSupportedScopes()
    {
        return supportedScopes;
    }
    public void setSupportedScopes(String[] supportedScopes)
    {
        this.supportedScopes = supportedScopes;
    }
    public String getIssuer()
    {
        return issuer;
    }
    public void setIssuer(String issuer)
    {
        this.issuer = issuer;
    }
    public String getUserInfoEndpoint()
    {
        return userInfoEndpoint;
    }
    public void setUserInfoEndpoint(String userInfoEndpoint)
    {
        this.userInfoEndpoint = userInfoEndpoint;
    }
    public String getAuthorizationEndpoint()
    {
        return authorizationEndpoint;
    }
    public void setAuthorizationEndpoint(String authorizationEndpoint)
    {
        this.authorizationEndpoint = authorizationEndpoint;
    }
    public String getEndSessionEndpoint()
    {
        return endSessionEndpoint;
    }
    public void setEndSessionEndpoint(String endSessionEndpoint)
    {
        this.endSessionEndpoint = endSessionEndpoint;
    }
}
