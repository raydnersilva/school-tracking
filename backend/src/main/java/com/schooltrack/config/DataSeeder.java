package com.schooltrack.config;

import com.schooltrack.model.*;
import com.schooltrack.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BusRepository busRepository;
    private final BusRouteRepository busRouteRepository;
    private final StudentRepository studentRepository;
    private final NotificationRepository notificationRepository;
    private final ScheduleRepository scheduleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        // Users
        User admin = userRepository.save(User.builder()
                .username("admin")
                .name("Administrador")
                .email("admin@schooltrack.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .build());

        User parent = userRepository.save(User.builder()
                .username("maria")
                .name("Maria Silva")
                .email("maria@email.com")
                .password(passwordEncoder.encode("123456"))
                .role(Role.PARENT)
                .build());

        User driver = userRepository.save(User.builder()
                .username("jose")
                .name("José Santos")
                .email("jose@email.com")
                .password(passwordEncoder.encode("123456"))
                .role(Role.DRIVER)
                .build());

        // Bus
        Bus bus = busRepository.save(Bus.builder()
                .licensePlate("ABC-1234")
                .capacity(40)
                .model("Mercedes-Benz OF 1519")
                .driver(driver)
                .currentLatitude(-23.5505)
                .currentLongitude(-46.6333)
                .build());

        // Route
        BusRoute route = busRouteRepository.save(BusRoute.builder()
                .name("Rota 12 - Centro")
                .description("Rota do centro até a escola Municipal São Paulo")
                .bus(bus)
                .build());

        // Stops
        RouteStop stop1 = RouteStop.builder()
                .name("Ponto de Partida - Praça da Sé")
                .latitude(-23.5505)
                .longitude(-46.6333)
                .stopOrder(1)
                .estimatedTime("06:30")
                .busRoute(route)
                .build();

        RouteStop stop2 = RouteStop.builder()
                .name("Parada 1 - Av. Paulista")
                .latitude(-23.552)
                .longitude(-46.635)
                .stopOrder(2)
                .estimatedTime("06:45")
                .busRoute(route)
                .build();

        RouteStop stop3 = RouteStop.builder()
                .name("Parada 2 - Rua Augusta")
                .latitude(-23.5555)
                .longitude(-46.64)
                .stopOrder(3)
                .estimatedTime("06:55")
                .busRoute(route)
                .build();

        RouteStop stop4 = RouteStop.builder()
                .name("Escola Municipal São Paulo")
                .latitude(-23.5631)
                .longitude(-46.6523)
                .stopOrder(4)
                .estimatedTime("07:10")
                .busRoute(route)
                .build();

        route.getStops().add(stop1);
        route.getStops().add(stop2);
        route.getStops().add(stop3);
        route.getStops().add(stop4);
        busRouteRepository.save(route);

        // Student
        Student student = studentRepository.save(Student.builder()
                .name("João Silva")
                .grade("5º Ano")
                .school("Escola Municipal São Paulo")
                .parent(parent)
                .busRoute(route)
                .build());

        // Schedule
        scheduleRepository.save(Schedule.builder()
                .period("Manhã")
                .startTime("07:00")
                .endTime("12:30")
                .busPickupTime("06:30")
                .busDropoffTime("12:45")
                .busRoute(route)
                .student(student)
                .build());

        // Notifications
        notificationRepository.save(Notification.builder()
                .title("Ônibus a caminho")
                .message("O ônibus de João está a 10 minutos da escola")
                .type("info")
                .user(parent)
                .build());

        notificationRepository.save(Notification.builder()
                .title("Atraso previsto")
                .message("O ônibus da rota 12 está com 15 minutos de atraso")
                .type("warning")
                .user(parent)
                .build());
    }
}
