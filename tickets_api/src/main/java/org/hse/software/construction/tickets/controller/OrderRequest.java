package org.hse.software.construction.tickets.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    int userId;
    int departureId;
    int destinationId;
    String token;
}
