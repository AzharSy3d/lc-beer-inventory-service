package com.azsyed.lcbeerinventoryservice.services;

import com.azsyed.brewery.model.BeerOrderDto;

public interface AllocationService {
    Boolean allocateOrder(BeerOrderDto beerOrderDto);

    void deallocateOrder(BeerOrderDto beerOrderDto);
}
