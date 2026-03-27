package ratelimiter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.args.ExpiryOption;

public class RateLimiter {

  private final Jedis redis;
  private final String label;
  private final long maxRequestCount;
  private final long timeWindowSeconds;

  public RateLimiter(Jedis redis, String label, long maxRequestCount, long timeWindowSeconds) {
    this.redis = redis;
    this.label = label;
    this.maxRequestCount = maxRequestCount;
    this.timeWindowSeconds = timeWindowSeconds;
  }

  public boolean pass() {
    // TODO: Implementation
    //rate limiter на основе fixed window, через счетчик контролируем запросы в рамках одного окна
    // по прошествию окна сбрасываем счетчик, решение со slidingWindow для последнего теста пока не додумал
    /*
    String strCount = redis.get(label);   //достаем сколько запросов сделано
    int count = strCount != null ? Integer.parseInt(strCount) : 0;

    if (count < maxRequestCount) {
      redis.incr(label);
      redis.expire(label, timeWindowSeconds, ExpiryOption.NX); //если expire time еще не было установлено, то устанавливаем
      return true;
    }
    return false;
     */

    //подход такой, на стороне редис будем хранить сет со временем запросов
    //и так же выполнять логику по "сдвигу" окна, то есть уменьшению колва запросов в текущем окне
    //как погуглил лучше это делать через lua скрипт
    //через KEY и ARGV передаем в редис, запрос(название сета), окно, макс.колво запросов, текущее время
    //ZREMRANGEBYSCORE удаляет все значения в сете не принадлещацие диапазону(время которых уже прошло и осталось за окном)
    //ZCARD количество запросов в текущем окне
    //ZADD для добавления запроса с его временем
    //EXPIRE удаляем набор запросов по если в течении всего окна не было запросов
    String luaScript = """
        local key = KEYS[1]
        local window = tonumber(ARGV[1])
        local limit = tonumber(ARGV[2])
        local now = tonumber(ARGV[3])
        
        redis.call('ZREMRANGEBYSCORE', key, 0, now - window)
        local count = redis.call('ZCARD', key)
        
        if count < limit then
            local micro = redis.call('TIME')[2]
            local member = now .. ':' .. micro
            redis.call('ZADD', key, now, member)
            redis.call('EXPIRE', key, window)
            return 1
        else
            return 0
        end
        """;

    long now = System.currentTimeMillis() / 1000;

    List<String> key = Arrays.asList(label); //наш запрос
    List<String> args = Arrays.asList(        //в виде args передаем параметры
        String.valueOf(timeWindowSeconds),
        String.valueOf(maxRequestCount),
        String.valueOf(now)
    );

    Long result = (Long) redis.eval(luaScript, key, args); //оказывается протокол redis не поддерживает boolean
    return result == 1;
    //падает на requestWithInsufficientIntervalsTest тесте так как там не работает логика проверки со сдвигающимся окном
  }

  public static void main(String[] args) {
    JedisPool pool = new JedisPool("localhost", 6379);

    try (Jedis redis = pool.getResource()) {
      RateLimiter rateLimiter = new RateLimiter(redis, "pr_rate", 1, 1);

      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      long prev = Instant.now().toEpochMilli();
      long now;

      while (true) {
        try {
          String s = br.readLine();
          if (s == null || s.equals("q")) {
            return;
          }
          boolean passed = rateLimiter.pass();

          now = Instant.now().toEpochMilli();
          if (passed) {
            System.out.printf("%d ms: %s", now - prev, "passed");
            prev = now;
          } else {
            System.out.printf("%d ms: %s", now - prev, "limited");
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

    }
  }
}
