package daggerok.rest

import daggerok.data.Message
import daggerok.data.MessagesMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Configuration
class WebfluxRoutesConfig {

  @Bean
  fun routes(messagesMapper: MessagesMapper) = router {
    ("/").nest {

      contentType(MediaType.APPLICATION_JSON_UTF8)

      GET("/**") {

        val map = mapOf(
            "hello" to "world",
            "messages" to messagesMapper.findAll()
        )

        ok().body(
            Mono.just(map), map.javaClass
        )
      }

      POST("/**") {

        it.bodyToMono(Map::class.java)
            .filter { it.containsKey("message") }
            .map { it["message"] as String }
            .map { Message(body = it) }
            .map { messagesMapper.save(it) }
            .map { "messages" to messagesMapper.findAll() }
            .subscribeOn(Schedulers.elastic())
            .flatMap {
              ok().body(
                Mono.just(it), it.javaClass
              )
            }
      }
    }
  }
}
