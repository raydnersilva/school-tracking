import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, Subject, BehaviorSubject, filter, take } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export interface BusLocation {
  id: number;
  licensePlate: string;
  capacity: number;
  model: string;
  driverName: string;
  currentLatitude: number;
  currentLongitude: number;
  active: boolean;
}

export interface BusRouteData {
  id: number;
  name: string;
  description: string;
  busId: number;
  busLicensePlate: string;
  active: boolean;
  stops: RouteStopData[];
}

export interface RouteStopData {
  id: number;
  name: string;
  latitude: number;
  longitude: number;
  stopOrder: number;
  estimatedTime: string;
}

@Injectable({
  providedIn: 'root',
})
export class BusTrackingService {
  private apiUrl = environment.apiUrl;
  private wsUrl = environment.apiUrl.replace('/api', '/ws');
  private stompClient: Client | null = null;
  private connected$ = new BehaviorSubject<boolean>(false);
  private busLocationSubjects = new Map<number, Subject<BusLocation>>();

  constructor(private http: HttpClient) {}

  connect(): void {
    if (this.stompClient?.active) return;

    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(this.wsUrl),
      reconnectDelay: 3000,
      debug: () => {},
      onConnect: () => this.connected$.next(true),
      onDisconnect: () => this.connected$.next(false),
    });

    this.stompClient.activate();
  }

  disconnect(): void {
    this.stompClient?.deactivate();
    this.connected$.next(false);
    this.busLocationSubjects.forEach(s => s.complete());
    this.busLocationSubjects.clear();
  }

  subscribeToBus(busId: number): Observable<BusLocation> {
    if (!this.busLocationSubjects.has(busId)) {
      const subject = new Subject<BusLocation>();
      this.busLocationSubjects.set(busId, subject);

      const doSubscribe = () => {
        this.stompClient?.subscribe(
          `/topic/bus/${busId}/location`,
          (message: IMessage) => {
            const data = JSON.parse(message.body) as BusLocation;
            subject.next(data);
          }
        );
      };

      if (this.connected$.value) {
        doSubscribe();
      } else {
        this.connected$.pipe(filter(c => c), take(1)).subscribe(() => doSubscribe());
      }
    }
    return this.busLocationSubjects.get(busId)!.asObservable();
  }

  sendBusLocation(busId: number, latitude: number, longitude: number): void {
    if (this.stompClient?.active) {
      this.stompClient.publish({
        destination: '/app/bus/location',
        body: JSON.stringify({ busId, latitude, longitude, timestamp: Date.now() }),
      });
    }
  }

  getActiveBuses(): Observable<BusLocation[]> {
    return this.http.get<BusLocation[]>(`${this.apiUrl}/buses`);
  }

  getBusLocation(busId: number): Observable<BusLocation> {
    return this.http.get<BusLocation>(`${this.apiUrl}/buses/${busId}`);
  }

  getRoutes(): Observable<BusRouteData[]> {
    return this.http.get<BusRouteData[]>(`${this.apiUrl}/routes`);
  }

  getRoute(routeId: number): Observable<BusRouteData> {
    return this.http.get<BusRouteData>(`${this.apiUrl}/routes/${routeId}`);
  }
}
