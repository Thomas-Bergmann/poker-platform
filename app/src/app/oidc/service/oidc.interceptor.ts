import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { OIDCState } from '../store/oidc-reducers';
import { Store } from '@ngrx/store';
import { selectAccessToken, selectResources } from '../store/oidc-selectors';

@Injectable({ providedIn: 'root' })
export class OIDCInterceptor implements HttpInterceptor {
  access_token : string='';
  uri_prefixes : Set<string> = new Set<string>();
  constructor(
    private readonly store: Store<OIDCState>
  ) {
    this.store.select(selectAccessToken).subscribe( t => {
      if (t !== undefined)
      {
        this.access_token = t;
        // console.log("access token received", this.access_token);
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
    let headers = req.headers.set('Authorization', 'Bearer ' + this.access_token);
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

  private isAccessTokenAvailable():boolean {
    return this.access_token.length > 0;
  }
  private isAuthorizationHeaderEmpty(req: HttpRequest<any>):boolean {
    return req.headers.get('Authorization') === undefined || req.headers.get('Authorization') === null;
  }
}
