import { Component } from '@angular/core';
import { ScheduleComponent } from '../schedule/schedule.component';

@Component({
  selector: 'app-dashboard',
  imports: [ScheduleComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent {}
