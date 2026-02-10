package com.schooltrack.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "geofence_zones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeofenceZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double radiusMeters;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ZoneType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_route_id")
    private BusRoute busRoute;

    private boolean active;

    public enum ZoneType {
        SCHOOL, BUS_STOP, CUSTOM
    }
}
