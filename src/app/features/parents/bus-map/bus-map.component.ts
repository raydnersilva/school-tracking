import { CommonModule } from '@angular/common';
import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  HostListener,
  OnDestroy,
  ViewChild,
} from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { LeafletModule } from '@bluehalo/ngx-leaflet';
import { LeafletDrawModule } from '@bluehalo/ngx-leaflet-draw';
import * as L from 'leaflet';
import 'leaflet-routing-machine';
import { Subscription, timer } from 'rxjs';
import { BusTrackingService, BusRouteData } from '../../../core/services/bus-tracking.service';

@Component({
  selector: 'app-bus-map',
  imports: [CommonModule, MatIconModule, LeafletModule, LeafletDrawModule],
  templateUrl: './bus-map.component.html',
  styleUrl: './bus-map.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BusMapComponent implements AfterViewInit, OnDestroy {
  @ViewChild('mapContainer', { static: true }) mapContainer!: ElementRef<HTMLDivElement>;
  private map: L.Map | null = null;
  private routeControl: L.Routing.Control | null = null;
  private subs = new Subscription();
  mapLoaded = false;
  private busMarker!: L.Marker;
  private routeLine!: L.Polyline;
  private progressLine!: L.Polyline;
  private routeCoordinates: L.LatLng[] = [];
  private animationSpeed = 200;
  private activeBusId: number | null = null;

  private routeData = {
    start: L.latLng(-23.5505, -46.6333),
    school: L.latLng(-23.5631, -46.6523),
    stops: [
      L.latLng(-23.552, -46.635),
      L.latLng(-23.5555, -46.64),
      L.latLng(-23.558, -46.645),
    ],
  };

  constructor(private busTrackingService: BusTrackingService) {}

  mapOptions = {
    layers: [
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 20,
        attribution: '© OpenStreetMap',
      }),
    ],
    zoom: 20,
    center: L.latLng(-23.5505, -46.6333),
  };

  ngAfterViewInit(): void {
    this.initMap();
    this.loadRouteFromBackend();
    this.connectWebSocket();
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
    this.destroyMap();
  }

  private initMap(): void {
    const container = this.mapContainer?.nativeElement;
    if (!container || (container as any)._leaflet_map) return;

    this.destroyMap();
    this.map = L.map(container, this.mapOptions);

    this.subs.add(
      timer(100).subscribe(() => {
        if (this.map) {
          this.map.invalidateSize(true);
          this.setupRoute();
          this.addMarkers();
          this.mapLoaded = true;
        }
      })
    );
  }

  private setupRoute(): void {
    if (!this.map) return;

    const waypoints = [
      this.routeData.start,
      ...this.routeData.stops,
      this.routeData.school,
    ];

    this.routeControl = L.Routing.control({
      waypoints,
      routeWhileDragging: false,
      show: false,
      addWaypoints: false,
      collapsible: false,
      lineOptions: {
        styles: [{ color: '#FFD700', weight: 6, opacity: 0.9 }],
        extendToWaypoints: true,
        missingRouteTolerance: 10,
      },
    }).addTo(this.map);

    const routesFoundHandler = (e: any) => {
      const routes = e.routes;
      if (routes?.length > 0) {
        this.routeCoordinates = routes[0].coordinates;
        this.createRouteElements();
        this.simulateBusMovement();

        if ('removeRoute' in this.routeControl!) {
          (this.routeControl as any).removeRoute();
        } else if (this.map) {
          this.map.removeControl(this.routeControl!);
        }
      }
    };
    this.routeControl.on('routesfound', routesFoundHandler);

    this.map.fitBounds(L.latLngBounds(waypoints), {
      padding: [50, 50],
      maxZoom: 20,
    });
  }

  private createRouteElements(): void {
    if (!this.map) return;

    this.routeLine = L.polyline(this.routeCoordinates, {
      color: '#FFD700',
      weight: 6,
      opacity: 0.9,
    }).addTo(this.map);

    this.progressLine = L.polyline([], {
      color: '#FF0000',
      weight: 6,
      opacity: 0.9,
    }).addTo(this.map);
  }

  private addMarkers(): void {
    if (!this.map) return;

    L.marker(this.routeData.start, {
      icon: L.icon({
        iconUrl: 'assets/images/bus.png',
        iconSize: [41, 41],
        iconAnchor: [20, 43],
      }),
    })
      .bindPopup('Ponto de Partida')
      .addTo(this.map);

    L.marker(this.routeData.school, {
      icon: L.icon({
        iconUrl: 'assets/images/school.png',
        iconSize: [41, 41],
        iconAnchor: [20, 43],
      }),
    })
      .bindPopup('Escola')
      .addTo(this.map);

    this.routeData.stops.forEach((stop, i) => {
      L.marker(stop, {
        icon: L.icon({
          iconUrl: 'assets/images/bus-stop.png',
          iconSize: [42, 42],
          iconAnchor: [20, 41],
        }),
      })
        .bindPopup(`Parada ${i + 1}`)
        .addTo(this.map!);
    });
  }

  private simulateBusMovement(): void {
    if (!this.map || this.routeCoordinates.length === 0) return;

    let staticBusMarker: L.Marker | null = null;
    this.map.eachLayer((layer) => {
      if (
        layer instanceof L.Marker &&
        layer.getLatLng().equals(this.routeData.start) &&
        !(layer as any)._icon?.src?.includes('bus.png')
      ) {
        staticBusMarker = layer;
      }
    });

    if (staticBusMarker) {
      this.map.removeLayer(staticBusMarker);
    }

    this.busMarker = L.marker(this.routeCoordinates[0], {
      icon: L.icon({
        iconUrl: 'assets/images/bus-moving.png',
        iconSize: [42, 42],
        iconAnchor: [20, 40],
      }),
      zIndexOffset: 1000,
    }).addTo(this.map);

    this.startAnimation();
  }

  private startAnimation() {
    let currentIndex = 0;
    const animation$ = timer(0, this.animationSpeed).subscribe(() => {
      if (currentIndex >= this.routeCoordinates.length) {
        animation$.unsubscribe();
        return;
      }

      const currentPos = this.routeCoordinates[currentIndex];
      this.busMarker.setLatLng(currentPos);
      this.progressLine.setLatLngs(
        this.routeCoordinates.slice(0, currentIndex + 1)
      );
      currentIndex++;
    });

    this.subs.add(animation$);
  }

  changeRouteColor(color: string): void {
    if (!this.routeControl || !this.map) return;

    const waypoints = this.routeControl.getWaypoints();
    this.map.removeControl(this.routeControl);

    this.routeControl = L.Routing.control({
      waypoints,
      routeWhileDragging: false,
      show: false,
      addWaypoints: false,
      lineOptions: {
        styles: [{ color, weight: 6, opacity: 0.9 }],
        extendToWaypoints: true,
        missingRouteTolerance: 10,
      },
    }).addTo(this.map);
  }

  @HostListener('window:resize')
  onWindowResize(): void {
    if (this.map) {
      this.subs.add(timer(200).subscribe(() => this.map?.invalidateSize(true)));
    }
  }

  private destroyMap(): void {
    if (this.routeControl && this.map) {
      this.map.removeControl(this.routeControl);
    }
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }

  private loadRouteFromBackend(): void {
    this.subs.add(
      this.busTrackingService.getRoutes().subscribe({
        next: (routes: BusRouteData[]) => {
          if (routes.length > 0) {
            const route = routes[0];
            if (route.stops.length >= 2) {
              this.routeData.start = L.latLng(route.stops[0].latitude, route.stops[0].longitude);
              this.routeData.school = L.latLng(
                route.stops[route.stops.length - 1].latitude,
                route.stops[route.stops.length - 1].longitude
              );
              this.routeData.stops = route.stops.slice(1, -1).map(
                s => L.latLng(s.latitude, s.longitude)
              );
            }
            if (route.busId) {
              this.activeBusId = route.busId;
              this.subscribeToLiveBus(route.busId);
            }
          }
        },
        error: () => {},
      })
    );
  }

  private connectWebSocket(): void {
    this.busTrackingService.connect();
  }

  private subscribeToLiveBus(busId: number): void {
    this.subs.add(
      this.busTrackingService.subscribeToBus(busId).subscribe({
        next: (bus) => {
          if (bus.currentLatitude && bus.currentLongitude && this.map) {
            const pos = L.latLng(bus.currentLatitude, bus.currentLongitude);
            if (this.busMarker) {
              this.busMarker.setLatLng(pos);
            } else {
              this.busMarker = L.marker(pos, {
                icon: L.icon({
                  iconUrl: 'assets/images/bus-moving.png',
                  iconSize: [42, 42],
                  iconAnchor: [20, 40],
                }),
                zIndexOffset: 1000,
              }).addTo(this.map);
            }
          }
        },
      })
    );
  }
}
