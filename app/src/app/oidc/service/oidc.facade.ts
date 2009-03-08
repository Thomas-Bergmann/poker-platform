import { Store } from '@ngrx/store';
import { Injectable } from '@angular/core';

import { OIDCInterceptor } from './oidc.interceptor';
import { OIDCAuthenticationService } from './oidc-authentication';
import { OIDCAuthorizationService } from './oidc-authorization';
import { OIDCService } from './oidc.service';
import { OIDCState, OIDCProvider, addProviders, defineUser, setAccessToken, addResources } from 'src/app/oidc/store';
import { environment } from 'src/environments/environment';
import { ServiceEndpoint, ServiceState, updateServiceLocation } from 'src/app/core/service';

@Injectable({ providedIn: 'root' })
export class OIDCFacade {
  constructor(
    private readonly store: Store<OIDCState>,
    private readonly service : OIDCService,
    private readonly serviceStore: Store<ServiceState>,
    private readonly authorizationService : OIDCAuthorizationService,
    private readonly authenticationService : OIDCAuthenticationService,
    ) {
  }
  loadIdentityProviders() {
    this.serviceStore.dispatch(updateServiceLocation(
      { serviceEndpoint : new ServiceEndpoint().init(environment.serviceEndPoint)}));
    this.service.loadIdentityProviders()
      .subscribe(providers => this.store.dispatch(addProviders({ providers : providers})));
  }
  loginViaCodeFlow(p : OIDCProvider | undefined) {
    if (p !== undefined) {
      this.authenticationService.getTokenForCodeFlow(p).then((idToken) =>
      {
        this.authorizationService.getAccessToken(p, idToken).then((tokenResponse) => {
          var claims = this.getClaims(tokenResponse.access_token);
          this.store.dispatch(setAccessToken({ token : tokenResponse.access_token}));
          this.store.dispatch(addResources({ resources : this.getScopes(claims)}));
          this.store.dispatch(defineUser({ user : this.getUser(claims)}));
        });
      });
    }
  }

  private getClaims(access_token:string):any {
    const tokenParts = access_token.split('.');
    const claimsBase64 = this.padBase64(tokenParts[1]);
    const claimsJson = this.b64DecodeUnicode(claimsBase64);
    const claims = JSON.parse(claimsJson);
    // console.log("claims from access_token", claims);
    return claims;
  }
  private getScopes(claims:any):string[] {
    var scopes : string = claims.scope;
    return scopes.split(" ");
  }
  private getUser(claims:any):string {
    return claims.sub;
  }

  private padBase64(base64data:any): string {
    while (base64data.length % 4 !== 0) {
      base64data += '=';
    }
    return base64data;
  }

  // see: https://developer.mozilla.org/en-US/docs/Web/API/WindowBase64/Base64_encoding_and_decoding#The_.22Unicode_Problem.22
  private b64DecodeUnicode(str:string) {
    const base64 = str.replace(/\-/g, '+').replace(/\_/g, '/');

    return decodeURIComponent(
      atob(base64)
        .split('')
        .map(function (c) {
          return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        })
        .join('')
    );
}

private base64UrlEncode(str:string): string {
  const base64 = btoa(str);
  return base64.replace(/\+/g, '-').replace(/\//g, '_').replace(/=/g, '');
}

}
