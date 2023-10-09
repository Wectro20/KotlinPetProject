package com.ajax.cryptocurrency.config

import com.ajax.cryptocurrency.nats.NatsController
import com.google.protobuf.GeneratedMessageV3
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
class NatsListenerBeanPostProcessor : BeanPostProcessor {
    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        if (bean is NatsController<*, *>) {
            bean.initialize()
        }
        return bean
    }

    private fun <ReqT : GeneratedMessageV3, RepT : GeneratedMessageV3> NatsController<ReqT, RepT>
            .initialize() {
        connection.createDispatcher { message ->
            val parsedData = parser.parseFrom(message.data)

            handler(parsedData)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext { response ->
                    connection.publish(message.replyTo, response.toByteArray())
                }
                .subscribe()
        }.subscribe(subject)
    }
}
