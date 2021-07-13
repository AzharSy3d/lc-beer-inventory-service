package com.azsyed.lcbeerinventoryservice.services;

import com.azsyed.model.events.NewInventoryEvent;
import com.azsyed.lcbeerinventoryservice.config.JmsConfig;
import com.azsyed.lcbeerinventoryservice.domain.BeerInventory;
import com.azsyed.lcbeerinventoryservice.repositories.BeerInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewInventoryListener {

    private final BeerInventoryRepository beerInventoryRepository;

    @JmsListener(destination = JmsConfig.NEW_INVENTORY_QUEUE)
    public void listen(NewInventoryEvent event){
        log.info("listen GOt Inventory " +event.toString());

        beerInventoryRepository.save(BeerInventory.builder()
                                    .beerId(event.getBeerDto().getId())
                                    .upc(event.getBeerDto().getUpc())
                                    .quantityOnHand(event.getBeerDto().getQuantityOnHand())
                                    .build());
    }
}
