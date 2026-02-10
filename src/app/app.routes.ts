import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

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
    canActivate: [authGuard],
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
  {
    path: 'driver',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/driver/driver-dashboard/driver-dashboard.component').then(
        (m) => m.DriverDashboardComponent
      ),
  },
  {
    path: 'admin',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/admin/admin-dashboard/admin-dashboard.component').then(
        (m) => m.AdminDashboardComponent
      ),
  },
  {
    path: 'chat',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/chat/chat.component').then(
        (m) => m.ChatComponent
      ),
  },
  { path: '**', redirectTo: 'login' },
];
