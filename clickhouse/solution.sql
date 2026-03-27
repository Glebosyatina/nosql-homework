-- Решение заданий по ClickHouse

-- 1. Создание таблицы
-- TODO: скопируйте и доработайте CREATE TABLE из schema.sql

CREATE TABLE IF NOT EXISTS server_logs
(
    timestamp  DateTime,
    user_id     UInt64,
    endpoint    String,
    response_time_ms    UInt64,
    status_code         UInt32
) ENGINE = MergeTree()
ORDER BY (timestamp, response_time_ms); -- TODO: выберите подходящий порядок сортировки

-- 2. Загрузка данных из CSV
-- Подсказка: можно использовать clickhouse-client с параметром --query
-- Пример команды (выполняется в терминале):
-- cat server_logs.csv | clickhouse-client --query="INSERT INTO server_logs FORMAT CSVWithNames"
cat server_logs.csv | docker exec -i clickhouse clickhouse-client --query="INSERT INTO server_logs FORMAT CSVWithNames"

-- 3. Запрос: Топ-5 самых медленных endpoint'ов (по среднему времени ответа)
-- TODO: напишите SELECT запрос

--группировка по endpoint и сортировка по среднему времени отклика
SELECT endpoint, AVG(response_time_ms) as avg_response_time FROM server_logs
GROUP BY endpoint
ORDER BY avg_response_time DESC
LIMIT 5;

-- 4. Запрос: Количество запросов по часам за весь период в логах
-- TODO: напишите SELECT запрос с использованием функции toHour() или formatDateTime()

--колво запросов по часам
--вытаскиваем час из даты создания и группируем по нему
SELECT toHour(timestamp) as hour, count() as num_requests FROM server_logs
GROUP BY hour;


-- 5. Запрос: Процент ошибок (status_code >= 400) для каждого endpoint'а
-- TODO: напишите SELECT запрос с вычислением процента ошибок

--через countIf считаем ошибки, и находим их процент от общего числа count()
SELECT endpoint, countIf(status_code >= 400) as bad_code, count() total, round(bad_code / total * 100, 2) as persent_bad_codes  FROM server_logs
GROUP BY endpoint;

