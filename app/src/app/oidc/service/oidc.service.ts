import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { OIDCProvider } from '../store';
import { ApiService } from 'src/app/core/service';

interface OIDCRO {
  localRef : string;
  globalRef : string;
  data : {
    // display name of identity provider
    name : string;
    // client identifier
    clientId: string;
    // URI to get code (also issuer for code)
    authenticationURI : string;
    // URI for token resource (of identity provider)
    tokenURI : string;
    // issuer of token - could be different to token URI
    tokenIssuer : string | undefined;
    // URI for user info - could be null - than no user info is retrieved
    userinfoURI : string | undefined;
  };
  info : {
    // authorization URI - issuer of access token
    authorizationURI : string;
    // resource server URI - requires access token
    resourceURI: string;
  };
}

@Injectable({ providedIn: 'root' })
export class OIDCService {
  constructor(private apiService: ApiService) {}

  loadIdentityProviders(): Observable<OIDCProvider[]> {
    return this.apiService
      .get<OIDCRO[]>(`/auth/idps`)
      .pipe(map(this.convertListOIDCRO));
  }

  convertListOIDCRO(ros : OIDCRO[]): OIDCProvider[]
  {
    return ros.map(a => convertOIDCRO(a));
  }
}

function convertOIDCRO(ro : OIDCRO): OIDCProvider
{
  return {
    localRef : ro.localRef,
    clientId: ro.data.clientId,
    name : ro.data.name,
    authenticationURI : ro.data.authenticationURI,
    tokenURI : ro.data.tokenURI,
    tokenIssuer : ro.data.tokenIssuer,
    userinfoURI: ro.data.userinfoURI,
    authorizationURI : ro.info.authorizationURI,
  }
}
