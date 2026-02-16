package blog;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BlogServiceTest {

    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static BlogService blogService;

    @BeforeAll
    static void setUp() {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("nosql_homework_test");
        blogService = new BlogService(database);
    }

    @AfterAll
    static void tearDown() {
        database.drop();
        mongoClient.close();
    }

    @BeforeEach
    void loadData() {
        blogService.insertSampleData();
    }

    @Test
    @DisplayName("Задание 1: Найти посты с больше чем 3 комментариями")
    void testFindPostsWithMoreThanThreeComments() {
        List<Document> posts = blogService.findPostsWithMoreThanNComments(3);
        
        assertEquals(2, posts.size(), "Должно быть найдено 2 поста с более чем 3 комментариями");
        
        List<String> titles = posts.stream()
            .map(doc -> doc.getString("title"))
            .sorted()
            .toList();
        
        assertTrue(titles.contains("Работа с MongoDB: основы"));
        assertTrue(titles.contains("Node.js и MongoDB: полное руководство"));
    }

    @Test
    @DisplayName("Задание 1: Найти посты с больше чем 0 комментариев")
    void testFindPostsWithMoreThanZeroComments() {
        List<Document> posts = blogService.findPostsWithMoreThanNComments(0);
        
        assertEquals(6, posts.size(), "Должно быть найдено 6 постов с комментариями");
    }

    @Test
    @DisplayName("Задание 2: Получить суммарное количество просмотров (гибкая схема)")
    void testGetTotalViews() {
        long totalViews = blogService.getTotalViews();

        assertEquals(6127, totalViews, 
            "Суммарное количество просмотров должно быть 6127 (1250 + 890 + 3420 + 567)");
    }

    @Test
    @DisplayName("Задание 2: Проверка обработки отсутствующих полей views")
    void testGetTotalViewsWithMissingFields() {
        database.getCollection("posts").insertOne(
            new Document("title", "Тестовый пост без views")
                .append("author", "Тестовый автор")
                .append("tags", List.of("test"))
                .append("comments", List.of())
                .append("published_at", "2026-02-15T10:00:00Z")
        );
        
        long totalViews = blogService.getTotalViews();
        
        assertEquals(6127, totalViews,
            "Посты без поля views должны считаться как 0 просмотров");
    }

    @Test
    void testAddRatingToCommentsWithoutIt() {
        long updated = blogService.addRatingToCommentsWithoutIt("Введение в NoSQL базы данных");
        
        assertEquals(2, updated, "Должно быть обновлено 2 комментария");
        
        Document post = database.getCollection("posts")
            .find(new Document("title", "Введение в NoSQL базы данных"))
            .first();
        
        assertNotNull(post);
        @SuppressWarnings("unchecked")
        List<Document> comments = (List<Document>) post.get("comments");
        
        for (Document comment : comments) {
            assertTrue(comment.containsKey("rating"), 
                "Все комментарии должны иметь поле rating");
            assertEquals(0, comment.getInteger("rating"), 
                "rating должен быть равен 0 для новых полей");
        }
    }

    @Test
    @DisplayName("Задание 3: Добавить rating к комментариям (смешанный случай)")
    void testAddRatingToCommentsWithoutItMixed() {
        long updated = blogService.addRatingToCommentsWithoutIt("Работа с MongoDB: основы");
        
        assertEquals(2, updated, "Должно быть обновлено 2 комментария (у которых не было rating)");
        
        Document post = database.getCollection("posts")
            .find(new Document("title", "Работа с MongoDB: основы"))
            .first();
        
        assertNotNull(post);
        @SuppressWarnings("unchecked")
        List<Document> comments = (List<Document>) post.get("comments");
        
        long commentsWithRating5 = comments.stream()
            .filter(c -> c.getInteger("rating", -1) == 5)
            .count();
        assertEquals(2, commentsWithRating5, "У 2 комментариев должен остаться rating=5");
        
        long commentsWithRating0 = comments.stream()
            .filter(c -> c.getInteger("rating", -1) == 0)
            .count();
        assertEquals(2, commentsWithRating0, "У 2 комментариев должен быть rating=0");
    }
}
