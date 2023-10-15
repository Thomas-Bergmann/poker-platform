import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { OIDCState, Token } from '../store/oidc-reducers';
import { Store } from '@ngrx/store';
import { OIDCProvider, selectAccessToken, selectCurrentProvider, selectRefreshToken, selectResources } from '../store';
import { OIDCFacade } from './oidc.facade';

@Injectable({ providedIn: 'root' })
export class OIDCInterceptor implements HttpInterceptor {
  access_token : Token | undefined = undefined;
  refresh_token : Token | undefined = undefined;
  get_refesh_trigger : boolean = false;
  oidc_provider : OIDCProvider | undefined = undefined;
  uri_prefixes : Set<string> = new Set<string>();
  constructor(
    private readonly store: Store<OIDCState>,
    private readonly oidcFacade: OIDCFacade
  ) {
    this.store.select(selectAccessToken).subscribe( t => {
      if (t !== undefined)
      {
        this.access_token = t;
        // console.log("access token received", this.access_token);
      }
    });
    this.store.select(selectRefreshToken).subscribe( t => {
      if (t !== undefined)
      {
        this.refresh_token = t;
        // console.log("refresh token received", this.refresh_token);
      }
    });
    this.store.select(selectCurrentProvider).subscribe( p => {
      if (p !== undefined)
      {
        this.oidc_provider = p;
      }
    });
    this.store.select(selectResources).subscribe( r => {
      if (r !== undefined)
      {
        this.uri_prefixes = r;
        // console.log("access scopes received", this.uri_prefixes);
      }
    });
  }

  public intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let url = req.url.toLowerCase();
    // console.log("interceptor started", url);
    if (!this.isAuthorizationHeaderEmpty(req))
    {
      // console.log("interceptor ignored - Authorization header set", url);
      return next.handle(req);
    }
    // access token is invalid, but refresh token exists
    // console.log("oidc.interceptor - isAccessTokenAvailable", this.isAccessTokenAvailable());
    if (this.isRefreshNecessary() && this.oidc_provider != undefined && this.refresh_token?.token != undefined && !this.get_refesh_trigger)
    {
      // avoid handle of this request (endless loop)
      this.get_refesh_trigger = true;
      this.oidcFacade.getAccessTokenWithRefreshToken(this.oidc_provider, this.refresh_token.token).then(r => {
        this.get_refesh_trigger = false;
      }).catch(e => {
        // todo user must login again
        this.get_refesh_trigger = false;
      });
    }
    if (!this.isAccessTokenAvailable())
    {
      // console.log("interceptor ignored - Access token is empty", url);
      return next.handle(req);
    }
    if (! this.isUriMatching(req)) {
      // console.log("interceptor not matching", url);
      return next.handle(req);
    }
    // console.log("interceptor matched", url);
    let headers = req.headers.set('Authorization', 'Bearer ' + this.access_token?.token);
    req = req.clone({ headers });
    return next.handle(req);
  }

  private isUriMatching(req: HttpRequest<any>):boolean {
    var matched = false;
    this.uri_prefixes.forEach(prefix => {
      if (req.url.toLowerCase().startsWith(prefix))
      {
        matched = true;
      }});
    return matched;
  }

  private isRefreshNecessary():boolean {
    if (this.access_token === undefined)
    {
        return false;
    }
    return (this.access_token.expiresIn - new Date().getTime()) / 1000 < 10;
  }

  private isAccessTokenAvailable():boolean {
    // console.log("oicd.interceptor access token expires in %d seconds:", ((this.access_token.expiresIn - new Date().getTime()) / 1000));
    return this.access_token !== undefined && new Date().getTime() < this.access_token.expiresIn;
  }

  private isAuthorizationHeaderEmpty(req: HttpRequest<any>):boolean {
    return req.headers.get('Authorization') === undefined || req.headers.get('Authorization') === null;
  }
}
