import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { IssueList } from './pages/issue-list/issue-list';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'issue-list', component: IssueList },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
];
