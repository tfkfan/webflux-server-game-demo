package com.tfkfan.webgame.config

import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono


@Component
class CustomWebFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> =
        if (exchange.request.uri.path == "/") chain.filter(
            exchange.mutate().request(exchange.request.mutate().path("/index.html").build()).build()
        )
        else chain.filter(exchange)
}