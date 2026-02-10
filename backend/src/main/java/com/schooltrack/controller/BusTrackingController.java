package com.schooltrack.controller;

import com.schooltrack.dto.BusLocationUpdate;
import com.schooltrack.dto.BusResponse;
import com.schooltrack.model.Bus;
import com.schooltrack.repository.BusRepository;
import com.schooltrack.service.DtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class BusTrackingController {

    private final BusRepository busRepository;
    private final DtoMapper dtoMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/bus/location")
    public void updateBusLocation(BusLocationUpdate update) {
        Bus bus = busRepository.findById(update.busId())
                .orElse(null);

        if (bus != null) {
            bus.setCurrentLatitude(update.latitude());
            bus.setCurrentLongitude(update.longitude());
            busRepository.save(bus);

            BusResponse response = dtoMapper.toBusResponse(bus);
            messagingTemplate.convertAndSend(
                    "/topic/bus/" + update.busId() + "/location",
                    response
            );
        }
    }
}
