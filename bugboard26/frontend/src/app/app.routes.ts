import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { IssueList } from './pages/issue-list/issue-list';
import { IssueCreate } from './pages/issue-create/issue-create';
import { IssueDetail } from './pages/issue-detail/issue-detail';
import { AdminUtenti } from './pages/admin-utenti/admin-utenti';
import { authGuard } from './guards/auth.guard';
import { adminGuard } from './guards/admin.guard';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'issue-list', component: IssueList, canActivate: [authGuard] },
  { path: 'issue/nuova', component: IssueCreate, canActivate: [authGuard] },
  { path: 'issue/:id', component: IssueDetail, canActivate: [authGuard] },
  { path: 'admin/utenti', component: AdminUtenti, canActivate: [adminGuard] },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
];
