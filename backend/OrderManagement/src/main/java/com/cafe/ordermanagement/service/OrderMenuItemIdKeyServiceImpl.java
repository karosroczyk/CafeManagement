package com.cafe.ordermanagement.service;

import com.cafe.ordermanagement.dao.OrderDAOJPA;
import com.cafe.ordermanagement.dao.OrderMenuItemIdKeyDAOJPA;
import com.cafe.ordermanagement.entity.OrderMenuItemId;
import com.cafe.ordermanagement.entity.OrderMenuItemIdKey;
import com.cafe.ordermanagement.exception.ResourceNotFoundException;
import com.netflix.discovery.EurekaClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class OrderMenuItemIdKeyServiceImpl implements OrderMenuItemIdKeyService{
    @Autowired
    private final OrderMenuItemIdKeyDAOJPA orderMenuItemIdKeyDAOJPA;
    @Autowired
    private WebClient.Builder webClientBuilder;
    private String menuServiceUrl;
    @Autowired
    private EurekaClient discoveryClient;

    public OrderMenuItemIdKeyServiceImpl(OrderMenuItemIdKeyDAOJPA orderMenuItemIdKeyDAOJPA, WebClient.Builder webClientBuilder, EurekaClient discoveryClient){
        this.orderMenuItemIdKeyDAOJPA = orderMenuItemIdKeyDAOJPA;
        this.webClientBuilder = webClientBuilder;
        this.discoveryClient = discoveryClient;
    }
    @PostConstruct
    private void init() {
        menuServiceUrl = discoveryClient.getNextServerFromEureka("menu", false).getHomePageUrl();
    }
    @Override
    public List<OrderMenuItemId> getOrderMenuItemIdKeyByOrderId(Integer order_id){
        return this.orderMenuItemIdKeyDAOJPA.findByOrderMenuItemIdKey_OrderId(order_id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id: " + order_id + " not found."));
    }
}
