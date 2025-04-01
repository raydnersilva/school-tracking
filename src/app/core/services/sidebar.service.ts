import { Injectable } from '@angular/core';
import { MenuItem } from '../models/menu-item';
import { BehaviorSubject } from 'rxjs/internal/BehaviorSubject';

@Injectable({
  providedIn: 'root',
})
export class SidebarService {
  private sidebarOpen$ = new BehaviorSubject<boolean>(true);

  private menuItems: MenuItem[] = [
    {
      icon: 'directions_bus',
      label: 'Ônibus',
      route: '/bus',
      roles: ['parent', 'driver', 'admin'],
    },
    {
      icon: 'child_care',
      label: 'Alunos',
      route: '/students',
      roles: ['parent', 'admin'],
    },
    {
      icon: 'schedule',
      label: 'Horários',
      route: '/schedule',
      roles: ['parent', 'driver', 'admin'],
    },
    {
      icon: 'notifications',
      label: 'Notificações',
      route: '/notifications',
      roles: ['parent', 'driver', 'admin'],
    },
    {
      icon: 'settings',
      label: 'Configurações',
      route: '/settings',
      roles: ['parent', 'driver', 'admin'],
    },
    {
      icon: 'admin_panel_settings',
      label: 'Administração',
      route: '/admin',
      roles: ['admin'],
    },
    {
      icon: 'route',
      label: 'Rotas',
      route: '/routes',
      roles: ['driver', 'admin'],
    },
  ];

  getMenuItems(role: string): MenuItem[] {
    return this.menuItems.filter((item) => item.roles.includes(role));
  }

  get sidebarState() {
    return this.sidebarOpen$.asObservable();
  }

  toggleSidebar() {
    this.sidebarOpen$.next(!this.sidebarOpen$.value);
  }
}
