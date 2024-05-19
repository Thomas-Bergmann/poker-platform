import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { OIDCProvider, ServiceTokenResponse } from '../store';

/**
 * The authorization service will convert an identity token to an access_token for the resource service
 */
@Injectable({ providedIn: 'root' })
export class OIDCAuthorizationService {
  constructor(
    private http: HttpClient,
  ) {}

  getAccessToken(provider:OIDCProvider, idToken: String):Promise<ServiceTokenResponse> {
    return new Promise((resolve, reject) => {
      const httpOptions = {
        headers : new HttpHeaders({
          'Authorization': 'Bearer ' + idToken,
        }),
      }
      var data : OAuthUserAuthenticationRO  = new OAuthUserAuthenticationRO();
      data.idp = provider.localRef;
      this.http
        .post<ServiceTokenResponse>(provider.authorizationURI, data, httpOptions)
        .subscribe((tokenResponse) => resolve(tokenResponse));
    });
  }
  getAccessTokenWithRefreshToken(provider:OIDCProvider, refreshToken: String):Promise<ServiceTokenResponse> {
    return new Promise((resolve, reject) => {
      const httpOptions = {
        headers : new HttpHeaders({
          'Authorization': 'Bearer ' + refreshToken,
        }),
      }
      var data : OAuthUserAuthenticationRO  = new OAuthUserAuthenticationRO();
      data.idp = provider.localRef;
      this.http
        .post<ServiceTokenResponse>(provider.authorizationURI, data, httpOptions)
        .subscribe((tokenResponse) => resolve(tokenResponse));
    });
  }
}

 class OAuthUserAuthenticationRO {
  "idp" : string
 }
