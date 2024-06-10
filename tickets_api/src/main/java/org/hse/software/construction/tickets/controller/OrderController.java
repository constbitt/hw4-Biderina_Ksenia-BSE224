package org.hse.software.construction.tickets.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hse.software.construction.tickets.model.order.Order;
import org.hse.software.construction.tickets.model.order.OrderRepository;
import org.hse.software.construction.tickets.model.station.StationRepository;
import org.hse.software.construction.tickets.model.user.UserRepository;
import org.hse.software.construction.tickets.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ticket-api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final StationRepository stationRepository;

    @PostMapping("/order")
    public ResponseEntity<?> createOrder(
            @RequestBody OrderRequest request
    ) {
        Order order;
        try {
            try {
                order = orderService.createOrder(request);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        }
        OrderResponse response = OrderResponse.builder()
                .userId(order.getUser().getId())
                .departureId(order.getFromStation().getId())
                .destinationId(order.getToStation().getId())
                .orderStatus(order.getStatus())
                .created(order.getCreated())
                .build();


        return ResponseEntity.ok("Success, order is being checked\n\nDetails\nuser: " + userRepository.getReferenceById(response.userId).getNickname() + "(id = " + response.userId + ")\nfrom: " + stationRepository.getReferenceById(response.departureId).getStation() + "(id = " + response.departureId + ")\nto: " + stationRepository.getReferenceById(response.destinationId).getStation()  + "(id = " + response.destinationId +  ")\ncreated: " + response.created);
    }


    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable String orderId) {
        Order order;
        try {
            Integer id = Integer.parseInt(orderId);
            order = orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Order with id " + orderId + " not found"));
        } catch (NumberFormatException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid order ID format. It must be a number.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        OrderResponse response = OrderResponse.builder()
                .userId(order.getUser().getId())
                .departureId(order.getFromStation().getId())
                .destinationId(order.getToStation().getId())
                .orderStatus(order.getStatus())
                .created(order.getCreated())
                .build();
        int status = response.orderStatus;
        String textStatus = "";
        if (status == 1) {
            textStatus = "check";
        }
        if (status == 2) {
            textStatus = "success";
        }
        if (status == 3) {
            textStatus = "rejection";
        }
        return ResponseEntity.ok("Order id = " + orderId + " details\n\nuser: " + userRepository.getReferenceById(response.userId).getNickname() + "(id = " + response.userId + ")\nfrom: " + stationRepository.getReferenceById(response.departureId).getStation() + "(id = " + response.departureId + ")\nto: " + stationRepository.getReferenceById(response.destinationId).getStation()  + "(id = " + response.destinationId +  ")\nstatus: " + textStatus + "\ncreated: " + response.created);
    }
}

