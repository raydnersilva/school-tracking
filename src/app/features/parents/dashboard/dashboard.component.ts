import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ScheduleComponent } from '../schedule/schedule.component';
import { NotificationsComponent } from '../notifications/notifications.component';
import { BusMapComponent } from '../bus-map/bus-map.component';

@Component({
  selector: 'app-dashboard',
  imports: [ScheduleComponent, NotificationsComponent, BusMapComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardComponent {}
