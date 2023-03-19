package de.hatoka.oidc.internal.remote;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import de.hatoka.common.capi.rest.RestControllerErrorSupport;
import de.hatoka.oidc.capi.business.IdentityProviderBO;
import de.hatoka.oidc.capi.business.IdentityProviderBORepository;
import de.hatoka.oidc.capi.business.IdentityProviderRef;
import de.hatoka.oidc.capi.remote.IdentityProviderDataRO;
import de.hatoka.oidc.capi.remote.IdentityProviderInfoRO;
import de.hatoka.oidc.capi.remote.IdentityProviderRO;
import de.hatoka.oidc.capi.remote.OIDCUserInfo;

@RestController
@RequestMapping(value = IdentityProviderController.PATH_ROOT, produces = { MediaType.APPLICATION_JSON_VALUE})
public class IdentityProviderController
{
    public static final String BEARER_PREFIX = "bearer ";
    public static final String PATH_TOKEN    = "/auth/users/token";
    public static final String PATH_ROOT = "/auth/idps";
    public static final String PATH_VAR_IDP = "idpRef";
    public static final String SUB_PATH_IDP = "/{" + PATH_VAR_IDP + "}";
    public static final String PATH_IDP = PATH_ROOT + SUB_PATH_IDP;
    public static final String SUB_PATH_AUTH     = SUB_PATH_IDP + "/auth";
    public static final String SUB_PATH_REDIRECT = SUB_PATH_IDP + "/redirect";
    public static final String PARAMETER_PUBLIC = "public";
    public static final String PARAMETER_CODE = "code";
    public static final String QUERY_PATH_IDPS = PATH_ROOT;
    private static final String METHOD_USERINFO    = "/userinfo";
    private static final String SUB_PATH_USERINFO = SUB_PATH_IDP + METHOD_USERINFO;

    @Autowired
    private IdentityProviderBORepository idpRepository;
    @Autowired
    private RestControllerErrorSupport errorSupport;
    @Autowired
    private IdentityProviderBO2RO idpBO2RO;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<IdentityProviderRO> getAll(UriComponentsBuilder uriBuilder)
    {
        List<IdentityProviderBO> providers = idpRepository.getAllIdentityProviders().stream().collect(Collectors.toList());
        return convert(providers, uriBuilder);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping(SUB_PATH_IDP)
    public IdentityProviderRO create(@PathVariable(PATH_VAR_IDP) String idpLocalRefString, @RequestBody IdentityProviderDataRO data, UriComponentsBuilder uriBuilder)
    {
        IdentityProviderRef idpRef = IdentityProviderRef.valueOfLocal(idpLocalRefString);
        Optional<IdentityProviderBO> opt = idpRepository.findIdentityProvider(idpRef);
        if (opt.isPresent())
        {
            opt.get().remove();
        }
        IdentityProviderBO idp = idpRepository.createIdentityProvider(idpRef, data);
        return convert(idp, uriBuilder);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(SUB_PATH_IDP)
    public IdentityProviderRO get(@PathVariable(PATH_VAR_IDP) String idpLocalRefString, UriComponentsBuilder uriBuilder)
    {
        Optional<IdentityProviderBO> opt = getIdentityProvider(idpLocalRefString);
        if (!opt.isPresent())
        {
            errorSupport.throwNotFoundException("idp.notfound", idpLocalRefString);
        }
        IdentityProviderBO idp = opt.get();
        return convert(idp, uriBuilder);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping(SUB_PATH_IDP)
    public void delete(@PathVariable(PATH_VAR_IDP) String idpLocalRefString)
    {
        Optional<IdentityProviderBO> opt = getIdentityProvider(idpLocalRefString);
        opt.ifPresent(IdentityProviderBO::remove);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = SUB_PATH_USERINFO)
    public OIDCUserInfo getUserInfo(@PathVariable(PATH_VAR_IDP) String idpLocalRefString, @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken, UriComponentsBuilder uriBuilder)
    {
        Optional<IdentityProviderBO> opt = getIdentityProvider(idpLocalRefString);
        if (!opt.isPresent())
        {
            errorSupport.throwNotFoundException("idp.notfound", idpLocalRefString);
        }
        IdentityProviderBO idp = opt.get();
        if (!bearerToken.toLowerCase().startsWith(BEARER_PREFIX))
        {
            errorSupport.throwNotFoundException("idp.bearer.idtoken.notfound", bearerToken);
        }
        String idToken = bearerToken.substring(BEARER_PREFIX.length()).trim();
        return idp.getUserInfo(idToken);
    }

    private Optional<IdentityProviderBO> getIdentityProvider(String idpLocalRefString)
    {
        IdentityProviderRef idpRef = IdentityProviderRef.valueOfLocal(idpLocalRefString);
        return idpRepository.findIdentityProvider(idpRef);
    }

    private IdentityProviderRO convert(IdentityProviderBO group, UriComponentsBuilder uriBuilder)
    {
        return idpBO2RO.apply(group, getServerUris(uriBuilder, group));
    }

    private IdentityProviderInfoRO getServerUris(UriComponentsBuilder uriBuilder, IdentityProviderBO provider)
    {
        IdentityProviderInfoRO result = new IdentityProviderInfoRO();
        result.setAuthorizationUri(this.getTokenURI(uriBuilder, provider).toString());
        result.setUserInfoUri(this.getUserInfoURI(uriBuilder, provider).toString());
        return result;
    }

    private List<IdentityProviderRO> convert(List<IdentityProviderBO> groups, UriComponentsBuilder uriBuilder)
    {
        return groups.stream().map(g -> convert(g, uriBuilder)).collect(Collectors.toList());
    }

    private URI getUserInfoURI(UriComponentsBuilder uriBuilder, IdentityProviderBO provider)
    {
        return uriBuilder.replaceQuery(null).replacePath(PATH_ROOT + "/" + provider.getRef().getLocalRef() + METHOD_USERINFO).build().toUri();
    }
    private URI getTokenURI(UriComponentsBuilder uriBuilder, IdentityProviderBO provider)
    {
        return uriBuilder.replaceQuery(null).replacePath(PATH_TOKEN).build().toUri();
    }
}
