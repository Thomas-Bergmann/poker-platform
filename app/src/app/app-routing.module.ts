import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './oidc/guard';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

/**
 * https://angular.io/guide/routing-overview
 * https://angular.io/generated/live-examples/router/stackblitz.html
 */
const appRoutes: Routes = [
  {
    path: 'players',
    pathMatch: 'prefix',
    loadChildren: () => import('./composites').then(m => m.CompositeModule),
    canLoad: [AuthGuard],
    canActivate: [AuthGuard],
  },
  {
    path: 'tables',
    pathMatch: 'prefix',
    loadChildren: () => import('./composites').then(m => m.CompositeModule),
    canLoad: [AuthGuard],
    canActivate: [AuthGuard],
  },
  {
    path: 'login',
    pathMatch: 'prefix',
    loadChildren: () => import('./oidc').then(m => m.OIDCModule),
    data: { preload: true }
  },
  { path: '',   redirectTo: '/login', pathMatch: 'full' },
];

@NgModule({
  imports: [
    BrowserAnimationsModule,
    RouterModule.forRoot(
      appRoutes, {
        enableTracing: false // <-- debugging purposes only
      }
    )
  ],
  exports: [
    RouterModule
  ]
})
export class AppRoutingModule { }
