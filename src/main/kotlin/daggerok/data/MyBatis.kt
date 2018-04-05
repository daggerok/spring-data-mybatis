package daggerok.data

import org.apache.ibatis.annotations.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Repository

private val log = LoggerFactory.getLogger("app")

data class Message(var id: Long? = null,
                   var body: String? = null)
@Mapper
@Repository
interface MessagesMapper {

  @Select("select * from messages")
  fun findAll(): Collection<Message>

  @Options(useGeneratedKeys = true)
  @Insert("""
    INSERT INTO
      messages (id   , body   )
      VALUES   (#{id}, #{body})
  """)
  fun save(message: Message)

  @Select("SELECT * FROM messages WHERE id = #{id}")
  fun findOne(id: Long): Message

  @Delete("DELETE FROM messages WHERE id = #{id}")
  fun delete(id: Long)

/*
  @Delete("""
    DELETE
      FROM  messages
      WHERE id in ( SELECT id FROM messages )
  """)
*/
  //@Delete("DELETE FROM messages")
  @Delete("TRUNCATE TABLE messages")
  fun deleteAll()
}

@Configuration
class MyBatisConfig {

  @Bean
  fun beforeRun() = InitializingBean {
    log.info("beforeRun")
  }

  @Bean
  fun runner(messagesMapper: MessagesMapper, usersMapper: UsersMapper) = ApplicationRunner {
    log.info("messagesMapper data\n:{}", messagesMapper.findAll())
    messagesMapper.save(Message(body = "hey"))
    messagesMapper.save(Message(body = "ho"))
    messagesMapper.save(Message())
    log.info("before delete\n:{}", messagesMapper.findAll())
    messagesMapper.delete(3)
    log.info("after delete\n:{}", messagesMapper.findAll())
    messagesMapper.save(Message(body = "fixed"))
    log.info("messagesMapper data\n:{}", messagesMapper.findAll())
    messagesMapper.deleteAll()

    log.info("user smart search\n:{}", usersMapper.smartSearch(null, null))
  }
}

// see XML mapper: src/main/resources/daggerok/data/UsersMapper.xml
data class User(var id: Long? = 0L,
                var name: String? = null)
@Mapper
@Repository
interface UsersMapper {
  fun smartSearch(@Param("id") id: Long?,
                  @Param("name") name: String?): Collection<User>
}
