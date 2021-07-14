package com.azsyed.lcbeerinventoryservice.services;

import com.azsyed.brewery.model.BeerOrderDto;
import com.azsyed.brewery.model.BeerOrderLineDto;
import com.azsyed.lcbeerinventoryservice.domain.BeerInventory;
import com.azsyed.lcbeerinventoryservice.repositories.BeerInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Service
public class AllocationServiceImpl implements AllocationService {

    private BeerInventoryRepository beerInventoryRepository;

    @Override
    public Boolean allocateOrder(BeerOrderDto beerOrderDto) {
       log.debug("Allocating orderId : "+beerOrderDto.getId());

        AtomicInteger totalOrdered = new AtomicInteger();
        AtomicInteger totalAllocated = new AtomicInteger();

        beerOrderDto.getBeerOrderLines().forEach(orderLine -> {
            if ((orderLine.getOrderQuantity() != null ? orderLine.getOrderQuantity() : 0) - (orderLine.getQuantityAllocated() != null ? orderLine.getQuantityAllocated() : 0) > 0){
                allocateBeerOrderLine(orderLine);
            }
            totalOrdered.set(totalOrdered.get()+orderLine.getOrderQuantity());
            totalAllocated.set(totalAllocated.get()+(orderLine.getQuantityAllocated()!=null?orderLine.getQuantityAllocated():0));
        });
        log.debug("Total Ordered: " + totalOrdered.get() + " Total Allocated: " + totalAllocated.get());
        return totalOrdered.get() == totalAllocated.get();
    }

    private void allocateBeerOrderLine(BeerOrderLineDto orderLine) {
        List<BeerInventory> beerInventoryList = beerInventoryRepository.findAllByUpc(orderLine.getUpc());

        beerInventoryList.forEach(beerInventory -> {
            int inventory = (beerInventory.getQuantityOnHand() == null) ? 0 : beerInventory.getQuantityOnHand();
            int orderQty = (orderLine.getOrderQuantity() == null) ? 0 : orderLine.getOrderQuantity();
            int allocatedQty = (orderLine.getQuantityAllocated() == null) ? 0 : orderLine.getQuantityAllocated();
            int qtyToAllocate = orderQty - allocatedQty;

            if (inventory >= qtyToAllocate) { // full allocation
                inventory = inventory - qtyToAllocate;
                orderLine.setQuantityAllocated(orderQty);
                beerInventory.setQuantityOnHand(inventory);

                beerInventoryRepository.save(beerInventory);
            } else if (inventory > 0) { //partial allocation
                orderLine.setQuantityAllocated(allocatedQty + inventory);
                beerInventory.setQuantityOnHand(0);

            }

            if (beerInventory.getQuantityOnHand() == 0) {
                beerInventoryRepository.delete(beerInventory);
            }
        });

    }

}
