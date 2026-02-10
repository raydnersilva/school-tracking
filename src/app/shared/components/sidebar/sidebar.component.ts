import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnDestroy } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { SidebarService } from '../../../core/services/sidebar.service';
import { MatIconModule } from '@angular/material/icon';
import { MenuItem } from '../../../core/models/menu-item';
import { COLORS } from '../../utils/colors';
import { Subscription } from 'rxjs';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-sidebar',
  imports: [CommonModule, RouterModule, MatIconModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SidebarComponent implements OnDestroy {
  @Input() isMobile: boolean = false;
  menuItens: MenuItem[] = [];
  isOpen: boolean = true;
  colors = COLORS;
  userName: string = 'Usuário';
  userRole: string = 'parent';
  private subs = new Subscription();

  constructor(
    private sidebarService: SidebarService,
    private router: Router,
    private authService: AuthService
  ) {
    this.userRole = this.authService.getUserRole() || 'parent';
    this.userName = this.authService.getUserName() || 'Usuário';
    this.menuItens = this.sidebarService.getMenuItems(this.userRole);

    this.subs.add(
      this.sidebarService.sidebarState.subscribe((state) => {
        this.isOpen = state;
      })
    );
  }

  ngOnDestroy() {
    this.subs.unsubscribe();
  }

  toggleSidebar() {
    this.sidebarService.toggleSidebar();
  }

  logout() {
    this.authService.logout();
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
