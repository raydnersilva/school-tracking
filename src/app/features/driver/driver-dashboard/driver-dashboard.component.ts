import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { HttpClient } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { BusTrackingService } from '../../../core/services/bus-tracking.service';

interface Trip {
  id: number;
  busRouteId: number;
  busRouteName: string;
  driverId: number;
  driverName: string;
  startedAt: string;
  endedAt: string | null;
  status: string;
  studentsBoarded: number;
  studentsDropped: number;
}

interface Student {
  id: number;
  name: string;
  grade: string;
  school: string;
}

@Component({
  selector: 'app-driver-dashboard',
  imports: [CommonModule, MatIconModule],
  templateUrl: './driver-dashboard.component.html',
  styleUrl: './driver-dashboard.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DriverDashboardComponent implements OnInit, OnDestroy {
  currentTrip: Trip | null = null;
  students: Student[] = [];
  tripHistory: Trip[] = [];
  routes: { id: number; name: string }[] = [];
  selectedRouteId: number | null = null;
  isLoading = false;
  errorMessage = '';
  private subs = new Subscription();

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private busTrackingService: BusTrackingService
  ) {}

  ngOnInit() {
    this.loadCurrentTrip();
    this.loadRoutes();
    this.loadTripHistory();
    this.busTrackingService.connect();
  }

  ngOnDestroy() {
    this.subs.unsubscribe();
  }

  private loadCurrentTrip() {
    this.subs.add(
      this.http.get<Trip>(`${environment.apiUrl}/driver/trip/current`).subscribe({
        next: (trip) => {
          this.currentTrip = trip;
          this.loadStudents();
          this.cdr.markForCheck();
        },
        error: () => {
          this.currentTrip = null;
          this.cdr.markForCheck();
        },
      })
    );
  }

  private loadRoutes() {
    this.subs.add(
      this.http.get<any[]>(`${environment.apiUrl}/routes`).subscribe({
        next: (routes) => {
          this.routes = routes;
          if (routes.length > 0) this.selectedRouteId = routes[0].id;
          this.cdr.markForCheck();
        },
      })
    );
  }

  private loadStudents() {
    this.subs.add(
      this.http.get<Student[]>(`${environment.apiUrl}/students`).subscribe({
        next: (students) => {
          this.students = students;
          this.cdr.markForCheck();
        },
      })
    );
  }

  private loadTripHistory() {
    this.subs.add(
      this.http.get<Trip[]>(`${environment.apiUrl}/driver/trips`).subscribe({
        next: (trips) => {
          this.tripHistory = trips.slice(0, 10);
          this.cdr.markForCheck();
        },
      })
    );
  }

  startTrip() {
    if (!this.selectedRouteId) return;
    this.isLoading = true;
    this.subs.add(
      this.http.post<Trip>(`${environment.apiUrl}/driver/trip/start?busRouteId=${this.selectedRouteId}`, {}).subscribe({
        next: (trip) => {
          this.currentTrip = trip;
          this.isLoading = false;
          this.loadStudents();
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Erro ao iniciar viagem';
          this.isLoading = false;
          this.cdr.markForCheck();
        },
      })
    );
  }

  endTrip() {
    if (!this.currentTrip) return;
    this.isLoading = true;
    this.subs.add(
      this.http.put<Trip>(`${environment.apiUrl}/driver/trip/${this.currentTrip.id}/end`, {}).subscribe({
        next: () => {
          this.currentTrip = null;
          this.isLoading = false;
          this.loadTripHistory();
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Erro ao encerrar viagem';
          this.isLoading = false;
          this.cdr.markForCheck();
        },
      })
    );
  }

  boardStudent(studentId: number) {
    if (!this.currentTrip) return;
    this.subs.add(
      this.http.post(`${environment.apiUrl}/driver/trip/${this.currentTrip.id}/board?studentId=${studentId}`, {}).subscribe({
        next: () => {
          this.loadCurrentTrip();
        },
      })
    );
  }

  dropStudent(studentId: number) {
    if (!this.currentTrip) return;
    this.subs.add(
      this.http.post(`${environment.apiUrl}/driver/trip/${this.currentTrip.id}/drop?studentId=${studentId}`, {}).subscribe({
        next: () => {
          this.loadCurrentTrip();
        },
      })
    );
  }

  sendLocation(lat: number, lng: number) {
    if (this.currentTrip) {
      this.busTrackingService.sendBusLocation(this.currentTrip.busRouteId, lat, lng);
    }
  }
}
