package blog;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;

public class BlogService {

    private final MongoCollection<Document> posts;

    public BlogService(MongoDatabase database) {
        this.posts = database.getCollection("posts");
    }

    public List<Document> findPostsWithMoreThanNComments(int minComments) {
        // TODO: Реализуйте поиск постов с количеством комментариев > minComments
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public long getTotalViews() {
        // TODO: Реализуйте подсчет суммарного количества просмотров
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public long addRatingToCommentsWithoutIt(String postTitle) {
        // TODO: Реализуйте добавление поля rating к комментариям без этого поля
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Вспомогательный метод для загрузки тестовых данных
     * Обратите внимание: документы имеют разную структуру (гибкая схема MongoDB)
     */
    public void insertSampleData() {
        posts.drop();
        
        List<Document> samplePosts = List.of(
            new Document("title", "Введение в NoSQL базы данных")
                .append("author", "Иван Петров")
                .append("tags", List.of("nosql", "database", "tutorial"))
                .append("views", 1250) // Поле views есть
                .append("comments", List.of(
                    new Document("user", "Мария").append("text", "Отличная статья!").append("date", "2026-02-10T10:30:00Z"),
                    new Document("user", "Алексей").append("text", "Очень полезно, спасибо").append("date", "2026-02-10T14:20:00Z")
                ))
                .append("published_at", "2026-02-09T09:00:00Z"),
            
            new Document("title", "Работа с MongoDB: основы")
                .append("author", "Елена Смирнова")
                .append("tags", List.of("mongodb", "nosql", "javascript"))
                .append("is_featured", true) // Дополнительное поле
                .append("comments", List.of(
                    new Document("user", "Дмитрий").append("text", "Можно больше примеров кода?").append("date", "2026-02-11T11:15:00Z"),
                    new Document("user", "Ольга").append("text", "Всё понятно объяснено").append("date", "2026-02-11T16:45:00Z").append("rating", 5),
                    new Document("user", "Сергей").append("text", "Ждём продолжения").append("date", "2026-02-12T09:30:00Z"),
                    new Document("user", "Анна").append("text", "Буду использовать в проекте").append("date", "2026-02-12T13:20:00Z").append("rating", 5)
                ))
                .append("published_at", "2026-02-11T08:00:00Z"),
            
            new Document("title", "ClickHouse для аналитики")
                .append("author", "Иван Петров")
                .append("tags", List.of("clickhouse", "analytics", "bigdata"))
                .append("views", 890)
                .append("comments", List.of(
                    new Document("user", "Павел").append("text", "Интересный подход").append("date", "2026-02-12T10:00:00Z")
                ))
                .append("published_at", "2026-02-12T07:30:00Z"),
            
            new Document("title", "Redis: кэширование и не только")
                .append("author", "Дмитрий Козлов")
                .append("tags", List.of("redis", "cache", "performance"))
                .append("comments", List.of())
                .append("published_at", "2026-02-13T10:00:00Z"),
            
            new Document("title", "Cassandra vs ScyllaDB")
                .append("author", "Елена Смирнова")
                .append("tags", List.of("cassandra", "scylla", "comparison"))
                .append("comments", List.of(
                    new Document("user", "Виктор").append("text", "А что быстрее?").append("date", "2026-02-13T12:30:00Z"),
                    new Document("user", "Татьяна").append("text", "ScyllaDB впечатляет").append("date", "2026-02-13T15:10:00Z"),
                    new Document("user", "Михаил").append("text", "Хочу попробовать оба варианта").append("date", "2026-02-13T17:00:00Z")
                ))
                .append("published_at", "2026-02-13T11:00:00Z"),
            
            new Document("title", "Node.js и MongoDB: полное руководство")
                .append("author", "Алексей Николаев")
                .append("tags", List.of("nodejs", "mongodb", "backend"))
                .append("views", 3420)
                .append("draft_version", 3)
                .append("comments", List.of(
                    new Document("user", "Игорь").append("text", "Очень подробно").append("date", "2026-02-14T09:00:00Z").append("rating", 5),
                    new Document("user", "Наталья").append("text", "Примеры рабочие, проверила").append("date", "2026-02-14T11:30:00Z").append("rating", 5),
                    new Document("user", "Роман").append("text", "Лучший туториал по теме").append("date", "2026-02-14T14:15:00Z").append("rating", 5),
                    new Document("user", "Юлия").append("text", "Ждём часть 2").append("date", "2026-02-14T16:45:00Z").append("rating", 4)
                ))
                .append("published_at", "2026-02-14T08:00:00Z"),
            
            new Document("title", "Проектирование схем в NoSQL")
                .append("author", "Иван Петров")
                .append("tags", List.of("nosql", "design", "architecture"))
                .append("views", 567)
                .append("comments", List.of(
                    new Document("user", "Константин").append("text", "Полезные паттерны").append("date", "2026-02-14T10:30:00Z")
                ))
                .append("published_at", "2026-02-14T09:30:00Z")
        );
        
        posts.insertMany(samplePosts);
    }
}
