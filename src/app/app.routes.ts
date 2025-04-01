import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then(
        (m) => m.LoginComponent
      ),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register/register.component').then(
        (m) => m.RegisterComponent
      ),
  },
  {
    path: 'forgot-password',
    loadComponent: () =>
      import('./features/auth/forgot-password/forgot-password.component').then(
        (m) => m.ForgotPasswordComponent
      ),
  },
  {
    path: 'parent',
    loadComponent: () =>
      import('./features/parents/home/home.component').then(
        (m) => m.HomeComponent
      ),
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./features/parents/dashboard/dashboard.component').then(
            (m) => m.DashboardComponent
          ),
        outlet: 'content',
      },
    ],
  },
];
