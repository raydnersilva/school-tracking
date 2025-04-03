import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-notifications',
  imports: [MatIconModule],
  templateUrl: './notifications.component.html',
  styleUrl: './notifications.component.scss',
})
export class NotificationsComponent {
  notifications = [
    {
      title: 'Ônibus a caminho',
      message: 'O ônibus de João está a 10 minutos da escola',
      time: '10:30 AM',
      type: 'info',
    },
    {
      title: 'Atraso previsto',
      message: 'O ônibus da rota 12 está com 15 minutos de atraso',
      time: '07:45 AM',
      type: 'warning',
    },
  ];
}
