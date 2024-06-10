package org.hse.software.construction.tickets.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hse.software.construction.tickets.config.JwtUtil;
import org.hse.software.construction.tickets.controller.OrderRequest;
import org.hse.software.construction.tickets.model.order.Order;
import org.hse.software.construction.tickets.model.station.Station;
import org.hse.software.construction.tickets.model.user.User;
import org.hse.software.construction.tickets.model.order.OrderRepository;
import org.hse.software.construction.tickets.model.station.StationRepository;
import org.hse.software.construction.tickets.model.user.UserRepository;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final StationRepository stationRepository;
    private final JwtUtil jwtUtil;

    public Order createOrder(OrderRequest request) {
        try {
            String mail = jwtUtil.extractUsername(request.getToken());
            if ((jwtUtil.isTokenExpired(request.getToken())) || (userRepository.findByEmail(mail).isEmpty())) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Sorry, your token is not valid");
        }

        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new EntityNotFoundException("User with id = " + request.getUserId() + " not found in repository"));

        Station fromStation = stationRepository.findById(request.getDepartureId()).orElseThrow(() -> new EntityNotFoundException("Departure station with id = " + request.getDepartureId() + " not found in repository"));

        Station toStation = stationRepository.findById(request.getDestinationId()).orElseThrow(() -> new EntityNotFoundException("Destination station with id = " + request.getDestinationId() + " not found in repository"));

        Order order = Order.builder()
                .user(user)
                .fromStation(fromStation)
                .toStation(toStation)
                .status(1)
                .created(new Timestamp(System.currentTimeMillis()))
                .build();

        return orderRepository.save(order);
    }
}