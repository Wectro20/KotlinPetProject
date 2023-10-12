package com.ajax.cryptocurrency.nats

import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import io.nats.client.Connection
import reactor.core.publisher.Mono

interface NatsController<ReqT : GeneratedMessageV3, RespT : GeneratedMessageV3> {

    val connection: Connection

    val subject: String

    val parser: Parser<ReqT>

    fun handler(request: ReqT): Mono<RespT>
}
