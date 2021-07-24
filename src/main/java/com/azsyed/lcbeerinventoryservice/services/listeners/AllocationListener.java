package com.azsyed.lcbeerinventoryservice.services.listeners;

import com.azsyed.brewery.model.events.AllocateOrderRequest;
import com.azsyed.brewery.model.events.AllocateOrderResult;
import com.azsyed.lcbeerinventoryservice.config.JmsConfig;
import com.azsyed.lcbeerinventoryservice.services.AllocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AllocationListener {

    private final AllocationService allocationService;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(AllocateOrderRequest request){
        AllocateOrderResult.AllocateOrderResultBuilder builder = AllocateOrderResult.builder();

        builder.beerOrder(request.getBeerOrder());

        try{
            Boolean allocationResult = allocationService.allocateOrder(request.getBeerOrder());

            if(allocationResult){
                builder.pendingInventory(false);
            }else{
                builder.pendingInventory(true);
            }
            builder.allocationError(false);
        }catch (Exception e){
            log.error("Allocation failed for OrderId :"+request.getBeerOrder().getId());
            builder.allocationError(true);
        }

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE,builder.build());

    }



}
