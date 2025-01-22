package com.alibou.book.routes;

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
//    @Value("${product.service.url}")
//    private String productServiceUrl;
//    @Value("${order.service.url}")
//    private String orderServiceUrl;
//    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoute() {
        return GatewayRouterFunctions.route("inventory")
                .route(RequestPredicates.path("/api/inventory"), HandlerFunctions.http("http://localhost:8082"))
//                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceCircuitBreaker",
//                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventorySecondServiceRoute() {
        return GatewayRouterFunctions.route("inventorySecond")
                .route(RequestPredicates.path("/api/inventory/*"), HandlerFunctions.http("http://localhost:8082"))
//                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceCircuitBreaker",
//                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryThirdServiceRoute() {
        return GatewayRouterFunctions.route("inventoryThird")
                .route(RequestPredicates.path("/api/inventory/*/add"), HandlerFunctions.http("http://localhost:8082"))
//                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceCircuitBreaker",
//                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> menuServiceRoute() {
        return GatewayRouterFunctions.route("menu")
                .route(RequestPredicates.path("/api/menuitems"), HandlerFunctions.http("http://localhost:8081"))
//                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceCircuitBreaker",
//                        URI.create("forward:/fallbackRoute")))
                .build();
    }
    @Bean
    public RouterFunction<ServerResponse> menuSecondServiceRoute() {
        return GatewayRouterFunctions.route("menuSecond")
                .route(RequestPredicates.path("/api/menuitems/*"), HandlerFunctions.http("http://localhost:8081"))
//                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceCircuitBreaker",
//                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> menuThirdServiceRoute() {
        return GatewayRouterFunctions.route("menuThird")
                .route(RequestPredicates.path("/api/menuitems/*/*"), HandlerFunctions.http("http://localhost:8081"))
//                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceCircuitBreaker",
//                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> categoriesServiceRoute() {
        return GatewayRouterFunctions.route("categories")
                .route(RequestPredicates.path("/api/categories"), HandlerFunctions.http("http://localhost:8081"))
//                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceCircuitBreaker",
//                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> categoriesSecondServiceRoute() {
        return GatewayRouterFunctions.route("categoriesSecond")
                .route(RequestPredicates.path("/api/categories/*"), HandlerFunctions.http("http://localhost:8081"))
//                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceCircuitBreaker",
//                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderGetByCustomerIdServiceRoute() {
        return GatewayRouterFunctions.route("orderGetByCustomerid")
                .route(RequestPredicates.path("/orders/customer/*"), HandlerFunctions.http("http://localhost:8083"))
//                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceCircuitBreaker",
//                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderGet2ServiceRoute() {
        return GatewayRouterFunctions.route("orderGet2")
                .route(RequestPredicates.path("/orders"), HandlerFunctions.http("http://localhost:8083"))
//                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceCircuitBreaker",
//                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderGetServiceRoute() {
        return GatewayRouterFunctions.route("orderGet")
                .route(RequestPredicates.path("/orders*"), HandlerFunctions.http("http://localhost:8083"))
//                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceCircuitBreaker",
//                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute() {
        return GatewayRouterFunctions.route("order")
                .route(RequestPredicates.path("/orders/placeOrder*"), HandlerFunctions.http("http://localhost:8083"))
//                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceCircuitBreaker",
//                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderSecondServiceRoute() {
        return GatewayRouterFunctions.route("orderSec")
                .route(RequestPredicates.path("/orders/placeOrder/*"), HandlerFunctions.http("http://localhost:8083"))
//                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceCircuitBreaker",
//                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> getOrderMenuItemIdKeyByOrderIdRoute() {
        return GatewayRouterFunctions.route("orderMenuItemIdKeyByOrderId")
                .route(RequestPredicates.path("/orderMenuItemIdKeys/order/*"), HandlerFunctions.http("http://localhost:8083"))
//                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceCircuitBreaker",
//                        URI.create("forward:/fallbackRoute")))
                .build();
    }
}
