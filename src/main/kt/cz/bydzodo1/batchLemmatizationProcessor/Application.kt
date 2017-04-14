package main.kt.cz.bydzodo1.batchLemmatizationProcessor

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.SpringApplication
import java.util.Arrays
import org.springframework.boot.CommandLineRunner
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean

@SpringBootApplication
open class Application{

    @Autowired
    lateinit var ctx: ApplicationContext

    val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java, *args)
        }
    }

    @Bean
    open fun init() = CommandLineRunner {
        logger.debug("initializing application")
        val beanNames = ctx.beanDefinitionNames
        Arrays.sort(beanNames)
        for (beanName in beanNames) {
            logger.debug("loading bean with name $beanName")
        }
    }
}