import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { HttpClient } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface Notification {
  id: number;
  title: string;
  message: string;
  type: string;
  read: boolean;
  createdAt: string | null;
}

@Component({
  selector: 'app-notifications',
  imports: [MatIconModule],
  templateUrl: './notifications.component.html',
  styleUrl: './notifications.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotificationsComponent implements OnInit, OnDestroy {
  notifications: Notification[] = [];
  private subs = new Subscription();

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadNotifications();
  }

  ngOnDestroy() {
    this.subs.unsubscribe();
  }

  private loadNotifications() {
    this.subs.add(
      this.http.get<Notification[]>(`${environment.apiUrl}/notifications`).subscribe({
        next: (data) => {
          this.notifications = data;
          this.cdr.markForCheck();
        },
        error: () => {},
      })
    );
  }
}
