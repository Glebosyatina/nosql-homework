-- TODO: Implementation
//1
create table posts_by_user(
    post_id INT,
    user_id INT,
    username   VARCHAR,		//денормализованное поле, чтобы сразу доставать имя пользователя
    created_at TIMESTAMP,
    content TEXT,
    PRIMARY KEY (user_id, created_at, post_id) //партиционный и кластерный ключи, добавил кластерны ключ post_id чтобы
												//посты сделанные в одно время не затирали один другого
) WITH CLUSTERING ORDER BY (created_at DESC); //для каждого пользователя все посты в одном разделе, отсортированные по времени

//выборка постов заданного пользователя, упорядоченных по времени создания
select post_id, content, created_at from posts_by_user where user_id = 1;


//2
// в предыдущую таблицу добавляем денормализованное поле с именем пользователя
//последний пост заданного пользователя
SELECT post_id, content, created_at, username FROM posts_by_user WHERE user_id = 1 LIMIT 1;


//3
SELECT post_id, content, created_at FROM posts_by_user WHERE created_at > '2020-10-15';


//4
create table post_by_topic(
    user_id     INT,
    username    VARCHAR,
    post_id     INT,
    topic       INT,
    day         DATE,
    created_at  TIMESTAMP,
    PRIMARY KEY ((day, topic), created_at, post_id) //(day, topic) - составной ключ партиционирования для резбиения по дню и теме поста, created_at и post_id - кластерные ключ
);

SELECT user_id, username, post_id, created_at FROM post_by_topic WHERE day = '2020-12-19' AND topic = 1;
