import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { TokenResponse } from 'angular-oauth2-oidc';

import { OIDCProvider } from '../store';

/**
 * The authorization service will convert an identity token to an access_token for the resource service
 */
@Injectable({ providedIn: 'root' })
export class OIDCAuthorizationService {
  constructor(
    private http: HttpClient,
  ) {}

  getAccessToken(provider:OIDCProvider, idToken: String):Promise<TokenResponse> {
    return new Promise((resolve, reject) => {
      const httpOptions = {
        headers : new HttpHeaders({
          'Authorization': 'Bearer ' + idToken,
        }),
      }
      this.http
        .post<TokenResponse>(provider.authorizationURI, '', httpOptions)
        .subscribe((tokenResponse) => resolve(tokenResponse));
    });
  }
}
