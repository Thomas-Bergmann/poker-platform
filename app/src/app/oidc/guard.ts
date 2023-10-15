import { Injectable } from '@angular/core';
import {
  CanActivate, Router,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  CanActivateChild,
  CanLoad, Route
} from '@angular/router';
import { Store } from '@ngrx/store';

import { OIDCState, selectRefreshToken, rememberRouteBeforeLogin } from 'src/app/oidc/store';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate, CanActivateChild, CanLoad {
  isLoggedIn : boolean = false;
  constructor(
    private router: Router,
    private readonly oidcStore: Store<OIDCState>,
  ) {
    this.oidcStore.select(selectRefreshToken).subscribe((token) => {
      this.isLoggedIn = token !== undefined;
    });
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (!this.isLoggedIn) {
      this.oidcStore.dispatch(rememberRouteBeforeLogin({url : state.url.trim()}))
      this.router.navigate(['/login']);
    }
    return this.isLoggedIn;
  }

  canActivateChild(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    return this.isLoggedIn;
  }

  canLoad(route: Route): boolean {
    return this.isLoggedIn;
  }
}
