package com.ajax.cryptocurrency.threads

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class BackgroundJobRunner(
    @Autowired private val priceSaver: PriceSaver
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        priceSaver.run()
    }
}
