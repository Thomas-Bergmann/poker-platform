import { Injectable } from '@angular/core';
import { filter } from 'rxjs/operators';
import { AuthConfig, OAuthService, OAuthStorage } from 'angular-oauth2-oidc';

import { OIDCProvider } from '../store';

/**
 * https://github.com/manfredsteyer/angular-oauth2-oidc
 * Authentication service will authenticate the user agains the given OIDCProvider.
 * As result the id_token is returned by getTokenForCodeFlow:Promise.
 */
@Injectable({ providedIn: 'root' })
export class OIDCAuthenticationService {
  constructor(
    private oauthService: OAuthService,
    private authStorage: OAuthStorage,
  ) {}

  getConfigForCode(provider:OIDCProvider):AuthConfig {
    return {
      // Your Auth0 app's domain requires a trailing slash (define that at configuration)
      issuer: provider.authenticationURI,
      oidc: true,
      tokenEndpoint: provider.tokenURI,
      userinfoEndpoint: "", // provider.userinfoURI,
      strictDiscoveryDocumentValidation: false,
      // The app's clientId configured in Auth0
      clientId: provider.clientId,

      // Scopes ("rights") the Angular application wants get delegated
      // https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims
      // scope: 'openid email profile offline_access',
      scope: 'openid email profile',

      // Using Authorization Code Flow
      // (PKCE is activated by default for authorization code flow)
      // responseType: (authentication ? 'code' : 'token'),
      responseType: 'code',

      // The app's redirectUri configured in Auth0
      redirectUri: origin + '/login/redirectAfterLogin',

      // logout redirect URL
      postLogoutRedirectUri: origin + '/redirectAfterLogout',

      // Your Auth0 account's logout url
      // Derive it from your application's domain
      logoutUrl: origin +'/logout',

      sessionChecksEnabled: false,
      showDebugInformation: false,
    };
  }
  getConfigForTokenAfterCode(provider:OIDCProvider):AuthConfig {
    let config = this.getConfigForCode(provider);
    config.issuer = provider.tokenIssuer !== undefined ? provider.tokenIssuer : provider.tokenURI;
    return config;
  }
  getCodeForCodeFlow(provider:OIDCProvider) {
    this.oauthService.initCodeFlow();
    this.oauthService.configure(this.getConfigForCode(provider));
    this.oauthService.setupAutomaticSilentRefresh();
    this.oauthService.loadDiscoveryDocument().catch((e: any) => console.error(e));
  }
  getTokenForCodeFlow(provider:OIDCProvider) : Promise<String> {
    return new Promise((resolve, reject) => {
      this.oauthService.initCodeFlow();
      this.oauthService.configure(this.getConfigForTokenAfterCode(provider));
      if (provider.userinfoURI !== undefined)
      {
        this.oauthService.events
          .pipe(filter((e : any) => e.type === 'token_received'))
          .subscribe((a : any) => {
            this.oauthService.loadUserProfile()
      });
      }
      this.oauthService.tryLoginCodeFlow().then((a : any) => resolve(this.authStorage.getItem('id_token') || ''));
    });
  }
  logout() {
    this.oauthService.logOut();
  }
}
