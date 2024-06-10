package org.hse.software.construction.tickets.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    @JsonProperty
    int userId;
    @JsonProperty
    int departureId;
    @JsonProperty
    int destinationId;
    @JsonProperty
    int orderStatus;
    @JsonProperty
    Date created;
}
