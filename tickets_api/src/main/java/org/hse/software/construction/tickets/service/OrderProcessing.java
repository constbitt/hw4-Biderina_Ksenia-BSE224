package org.hse.software.construction.tickets.service;

import org.hse.software.construction.tickets.model.order.Order;
import org.hse.software.construction.tickets.model.order.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;

@Service
public class OrderProcessing {

    private final OrderRepository orderRepository;
    private final Random random = new Random();

    public OrderProcessing(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Scheduled(fixedDelay = 5000)
    public void processOrders() {
        List<Order> ordersToProcess = orderRepository.findByStatus(1);
        for (Order order : ordersToProcess) {
            try {
                Thread.sleep(random.nextInt(3000));
                int newStatus = random.nextBoolean() ? 2 : 3;
                order.setStatus(newStatus);
                orderRepository.save(order);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}