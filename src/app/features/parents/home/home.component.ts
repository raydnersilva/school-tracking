import { Component, HostListener } from '@angular/core';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { RouterOutlet } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { SidebarService } from '../../../core/services/sidebar.service';

@Component({
  selector: 'app-home-dashboard',
  imports: [SidebarComponent, RouterOutlet, MatIconModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent {
  isMobile = window.innerWidth < 768;

  constructor(private sidebarService: SidebarService) {}

  @HostListener('window:resize', ['$event'])
  onResize() {
    this.isMobile = window.innerWidth < 768;
  }

  toggleSidebar() {
    this.sidebarService.toggleSidebar();
  }
}
