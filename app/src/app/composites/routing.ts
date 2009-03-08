import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '../oidc/guard';
import * as composites from './components';

const routes: Routes = [
//  { path: 'players', component: composites.ListPlayersViewComponent },
//  { path: 'players/:player', component: composites.DetailPlayerViewComponent },
//  { path: 'players/:player/seats/:seat', component: composites.GameTableViewComponent },
{
  path: 'players',
  canLoad: [AuthGuard],
  canActivate: [AuthGuard],
  children: [
    { path: '',       pathMatch: 'full', component: composites.ListPlayersViewComponent },
    { path: ':player', pathMatch: 'prefix',
      children: [
        { path: '',     pathMatch: 'full', component: composites.DetailPlayerViewComponent },
      ]
    }
  ]
},
{
  path: 'tables',
  canLoad: [AuthGuard],
  canActivate: [AuthGuard],
  children: [
    { path: '',       pathMatch: 'full', component: composites.ListTablesViewComponent },
    { path: ':table', pathMatch: 'prefix',
      children: [
        { path: '',          pathMatch: 'full', component: composites.DetailTableViewComponent },
        { path: 'seats', pathMatch: 'prefix',
          children: [
            { path: '',      pathMatch: 'full', component: composites.DetailTableViewComponent },
            { path: ':seat', pathMatch: 'full', component: composites.GameTableViewComponent },
          ]
        }
      ]
    }
  ]
},
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [
    RouterModule
  ]
})
export class CompositeRoutingModule {}


/*
Copyright Google LLC. All Rights Reserved.
Use of this source code is governed by an MIT-style license that
can be found in the LICENSE file at https://angular.io/license
*/