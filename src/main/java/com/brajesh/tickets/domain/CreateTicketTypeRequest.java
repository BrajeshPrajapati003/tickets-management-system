package com.brajesh.tickets.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketTypeRequest{

    private String name;
    private Double price;
    private String description;
    private Integer totalAvailable;
}
