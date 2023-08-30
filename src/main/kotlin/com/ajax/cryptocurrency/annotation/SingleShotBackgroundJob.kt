package com.ajax.cryptocurrency.annotation

import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Component
annotation class SingleShotBackgroundJob (
    val startDelay: Long = 0,
    val maxParallelThreads: Int = 1
)