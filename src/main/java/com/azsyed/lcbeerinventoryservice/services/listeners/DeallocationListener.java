package com.azsyed.lcbeerinventoryservice.services.listeners;

import com.azsyed.brewery.model.events.DeallocateOrderRequest;
import com.azsyed.lcbeerinventoryservice.config.JmsConfig;
import com.azsyed.lcbeerinventoryservice.services.AllocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DeallocationListener {

    private final AllocationService allocationService;

    @JmsListener(destination = JmsConfig.DEALLOCATE_ORDER_QUEUE)
    public void listen(DeallocateOrderRequest request){
        allocationService.deallocateOrder(request.getBeerOrderDto());
    }
}
