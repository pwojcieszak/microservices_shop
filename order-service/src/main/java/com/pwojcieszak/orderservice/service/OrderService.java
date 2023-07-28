package com.pwojcieszak.orderservice.service;

import com.pwojcieszak.orderservice.dto.InventoryResponse;
import com.pwojcieszak.orderservice.dto.OrderLineItemsDto;
import com.pwojcieszak.orderservice.dto.OrderRequest;
import com.pwojcieszak.orderservice.model.Order;
import com.pwojcieszak.orderservice.model.OrderLineItems;
import com.pwojcieszak.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems =  orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItemsList(orderLineItems);


        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        Flux<InventoryResponse> inventoryResponseFlux = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToFlux(InventoryResponse.class);

        Mono<Boolean> allProductsInStockMono = inventoryResponseFlux
                .all(InventoryResponse::isInStock);

        allProductsInStockMono.subscribe(allProductsInStock -> {
            if (allProductsInStock) {
                orderRepository.save(order);
            }
            else {
                throw new IllegalArgumentException("Product not in stock");
            }
        });
        return "Order processing finished";
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
