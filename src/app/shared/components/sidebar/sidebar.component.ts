import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { SidebarService } from '../../../core/services/sidebar.service';
import { MatIconModule } from '@angular/material/icon';
import { MenuItem } from '../../../core/models/menu-item';
import { Subject } from 'rxjs/internal/Subject';

@Component({
  selector: 'app-sidebar',
  imports: [CommonModule, RouterModule, MatIconModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss',
})
export class SidebarComponent {
  @Input() isMobile: boolean = false;
  menuItens: MenuItem[] = [];
  isOpen: boolean = true;

  constructor(private sidebarService: SidebarService, private router: Router) {
    const userRole: string = 'parent';
    this.menuItens = this.sidebarService.getMenuItems(userRole);

    this.sidebarService.sidebarState.subscribe((state) => {
      this.isOpen = state;
    });
  }

  toggleSidebar() {
    this.sidebarService.toggleSidebar();
  }

  logout() {
    this.router.navigate(['login']);
  }

  getRoleName(role: string | undefined): string {
    switch (role) {
      case 'parent':
        return 'Responsável';
      case 'driver':
        return 'Motorista';
      case 'admin':
        return 'Administrador';
      default:
        return 'Usuário';
    }
  }
}
