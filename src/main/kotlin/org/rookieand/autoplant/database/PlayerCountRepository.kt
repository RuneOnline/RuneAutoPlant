package org.rookieand.autoplant.database

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoException
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import org.bson.Document
import org.rookieand.autoplant.configuration.PluginConfiguration
import java.util.UUID
import java.util.concurrent.TimeUnit

// 모든 호출이 블로킹이므로 반드시 비동기 스레드에서 사용한다.
class PlayerCountRepository(private val configuration: PluginConfiguration) {

    private lateinit var client: MongoClient
    private lateinit var collection: MongoCollection<Document>

    fun connect() {
        val settings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(configuration.mongoUri))
            // ping 실패를 30초 넘게 기다리지 않도록 서버 선택 타임아웃을 5초로 낮춘다.
            .applyToClusterSettings { it.serverSelectionTimeout(5, TimeUnit.SECONDS) }
            .build()
        client = MongoClients.create(settings)
        val database = client.getDatabase(configuration.mongoDatabase)
        // create()는 lazy라 연결 실패를 던지지 않는다. ping으로 즉시 확인해 원인을 드러낸다.
        try {
            database.runCommand(Document("ping", 1))
        } catch (e: MongoException) {
            client.close()
            throw MongoConnectionException(configuration.mongoUri, e)
        }
        collection = database.getCollection(configuration.mongoCollection)
    }

    fun close() {
        if (::client.isInitialized) client.close()
    }

    fun load(uuid: UUID): Int? =
        collection.find(Filters.eq("_id", uuid.toString())).first()?.getInteger("count")

    fun save(uuid: UUID, count: Int) {
        val document = Document("_id", uuid.toString()).append("count", count)
        collection.replaceOne(Filters.eq("_id", uuid.toString()), document, ReplaceOptions().upsert(true))
    }

    fun delete(uuid: UUID) {
        collection.deleteOne(Filters.eq("_id", uuid.toString()))
    }
}

class MongoConnectionException(uri: String, cause: Throwable) : RuntimeException(
    "MongoDB($uri) 연결에 실패했습니다. 플러그인 문제가 아니라 DB 서버가 실행 중인지, config.yml의 mongodb.uri가 올바른지 확인하세요.",
    cause
)
