import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface Schedule {
  id: number;
  period: string;
  startTime: string;
  endTime: string;
  busPickupTime: string;
  busDropoffTime: string;
  busRouteId: number | null;
  busRouteName: string | null;
  studentId: number | null;
  studentName: string | null;
}

@Component({
  selector: 'app-schedule',
  imports: [],
  templateUrl: './schedule.component.html',
  styleUrl: './schedule.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ScheduleComponent implements OnInit, OnDestroy {
  schedules: Schedule[] = [];
  private subs = new Subscription();

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadSchedules();
  }

  ngOnDestroy() {
    this.subs.unsubscribe();
  }

  private loadSchedules() {
    this.subs.add(
      this.http.get<Schedule[]>(`${environment.apiUrl}/schedules`).subscribe({
        next: (data) => {
          this.schedules = data;
          this.cdr.markForCheck();
        },
        error: () => {},
      })
    );
  }
}
