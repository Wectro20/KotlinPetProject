package com.ajax.cryptocurrency.application.lib

import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Component
annotation class ScheduledBackgroundJobStarter (
    val startDelay: Long = 0,
    val period: Long = 0
)
