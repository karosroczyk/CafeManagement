package com.cafe.auth.routes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;

@Configuration
public class Routes {
    @Value("${services.menu.url}")
    private String menuServiceUrl;

    @Value("${services.inventory.url}")
    private String inventoryServiceUrl;

    @Value("${services.order.url}")
    private String orderServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> menuRoutes() {
        return route()
                .route(RequestPredicates.path("/api/menuitems"), HandlerFunctions.http(menuServiceUrl))
                .route(RequestPredicates.path("/api/menuitems/*"), HandlerFunctions.http(menuServiceUrl))
                .route(RequestPredicates.path("/api/menuitems/*/*"), HandlerFunctions.http(menuServiceUrl))
                .route(RequestPredicates.path("/api/categories"), HandlerFunctions.http(menuServiceUrl))
                .route(RequestPredicates.path("/api/categories/*"), HandlerFunctions.http(menuServiceUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryRoutes() {
        return GatewayRouterFunctions.route("inventory-routes")
                .route(RequestPredicates.path("/api/inventory"), HandlerFunctions.http(inventoryServiceUrl))
                .route(RequestPredicates.path("/api/inventory/*"), HandlerFunctions.http(inventoryServiceUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderRoutes() {
        return GatewayRouterFunctions.route("order-routes")
                .route(RequestPredicates.path("/orders/customer/*"), HandlerFunctions.http(orderServiceUrl))
                .route(RequestPredicates.path("/orders"), HandlerFunctions.http(orderServiceUrl))
                .route(RequestPredicates.path("/orders*"), HandlerFunctions.http(orderServiceUrl))
                .route(RequestPredicates.path("/orders/items*"), HandlerFunctions.http(orderServiceUrl))
                .route(RequestPredicates.path("/orders/items/*"), HandlerFunctions.http(orderServiceUrl))
                .route(RequestPredicates.path("/orderMenuItemIdKeys/order/*"), HandlerFunctions.http(orderServiceUrl))
                .build();
    }
}
