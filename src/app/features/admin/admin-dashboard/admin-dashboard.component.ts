import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { environment } from '../../../../environments/environment';

interface Stats {
  totalStudents: number;
  totalBuses: number;
  activeBuses: number;
  totalRoutes: number;
  activeRoutes: number;
  totalDrivers: number;
  tripsToday: number;
  tripsCompleted: number;
  averageDriverRating: number | null;
}

@Component({
  selector: 'app-admin-dashboard',
  imports: [CommonModule, MatIconModule, FormsModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminDashboardComponent implements OnInit, OnDestroy {
  stats: Stats | null = null;
  users: any[] = [];
  buses: any[] = [];
  routes: any[] = [];
  students: any[] = [];
  activeTab = 'stats';
  isLoading = false;
  private subs = new Subscription();

  newUser = { username: '', name: '', email: '', password: '', role: 'PARENT' };
  newBus = { licensePlate: '', capacity: 40, model: '' };
  newRoute = { name: '', description: '' };
  newStudent = { name: '', grade: '', school: '', parentId: '', busRouteId: '' };
  broadcastTitle = '';
  broadcastMessage = '';
  broadcastType = 'info';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit() { this.loadStats(); }
  ngOnDestroy() { this.subs.unsubscribe(); }

  setTab(tab: string) {
    this.activeTab = tab;
    if (tab === 'users') this.loadUsers();
    if (tab === 'buses') this.loadBuses();
    if (tab === 'routes') this.loadRoutes();
    if (tab === 'students') this.loadStudents();
  }

  private loadStats() {
    this.subs.add(this.http.get<Stats>(`${environment.apiUrl}/admin/stats`).subscribe({
      next: (s) => { this.stats = s; this.cdr.markForCheck(); },
    }));
  }

  private loadUsers() {
    this.subs.add(this.http.get<any[]>(`${environment.apiUrl}/admin/users`).subscribe({
      next: (u) => { this.users = u; this.cdr.markForCheck(); },
    }));
  }

  private loadBuses() {
    this.subs.add(this.http.get<any[]>(`${environment.apiUrl}/admin/buses`).subscribe({
      next: (b) => { this.buses = b; this.cdr.markForCheck(); },
    }));
  }

  private loadRoutes() {
    this.subs.add(this.http.get<any[]>(`${environment.apiUrl}/admin/routes`).subscribe({
      next: (r) => { this.routes = r; this.cdr.markForCheck(); },
    }));
  }

  private loadStudents() {
    this.subs.add(this.http.get<any[]>(`${environment.apiUrl}/admin/students`).subscribe({
      next: (s) => { this.students = s; this.cdr.markForCheck(); },
    }));
  }

  createUser() {
    this.subs.add(this.http.post(`${environment.apiUrl}/admin/users`, this.newUser).subscribe({
      next: () => { this.loadUsers(); this.newUser = { username: '', name: '', email: '', password: '', role: 'PARENT' }; },
    }));
  }

  deleteUser(id: number) {
    this.subs.add(this.http.delete(`${environment.apiUrl}/admin/users/${id}`).subscribe({ next: () => this.loadUsers() }));
  }

  createBus() {
    this.subs.add(this.http.post(`${environment.apiUrl}/admin/buses`, this.newBus).subscribe({
      next: () => { this.loadBuses(); this.newBus = { licensePlate: '', capacity: 40, model: '' }; },
    }));
  }

  deleteBus(id: number) {
    this.subs.add(this.http.delete(`${environment.apiUrl}/admin/buses/${id}`).subscribe({ next: () => this.loadBuses() }));
  }

  createRoute() {
    this.subs.add(this.http.post(`${environment.apiUrl}/admin/routes`, this.newRoute).subscribe({
      next: () => { this.loadRoutes(); this.newRoute = { name: '', description: '' }; },
    }));
  }

  deleteRoute(id: number) {
    this.subs.add(this.http.delete(`${environment.apiUrl}/admin/routes/${id}`).subscribe({ next: () => this.loadRoutes() }));
  }

  createStudent() {
    const body: any = { ...this.newStudent };
    if (body.parentId) body.parentId = +body.parentId;
    if (body.busRouteId) body.busRouteId = +body.busRouteId;
    this.subs.add(this.http.post(`${environment.apiUrl}/admin/students`, body).subscribe({
      next: () => { this.loadStudents(); this.newStudent = { name: '', grade: '', school: '', parentId: '', busRouteId: '' }; },
    }));
  }

  deleteStudent(id: number) {
    this.subs.add(this.http.delete(`${environment.apiUrl}/admin/students/${id}`).subscribe({ next: () => this.loadStudents() }));
  }

  broadcastNotification() {
    this.subs.add(this.http.post(`${environment.apiUrl}/admin/notifications/broadcast`, {
      title: this.broadcastTitle, message: this.broadcastMessage, type: this.broadcastType,
    }).subscribe({
      next: () => { this.broadcastTitle = ''; this.broadcastMessage = ''; this.cdr.markForCheck(); },
    }));
  }
}
