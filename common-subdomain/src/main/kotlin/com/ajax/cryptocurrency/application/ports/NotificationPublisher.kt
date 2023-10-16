package com.ajax.cryptocurrency.application.ports

interface NotificationPublisher<T> {
    val topic: String

    fun publishNotification(domainObject: T)
}
