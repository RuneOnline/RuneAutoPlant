package org.rookieand.replantCropCount.database

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import org.bson.Document
import org.rookieand.replantCropCount.configuration.PluginConfiguration
import java.util.UUID

// 모든 호출이 블로킹이므로 반드시 비동기 스레드에서 사용한다.
class PlayerCountRepository(private val configuration: PluginConfiguration) {

    private lateinit var client: MongoClient
    private lateinit var collection: MongoCollection<Document>

    fun connect() {
        client = MongoClients.create(configuration.mongoUri)
        collection = client.getDatabase(configuration.mongoDatabase).getCollection(configuration.mongoCollection)
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
