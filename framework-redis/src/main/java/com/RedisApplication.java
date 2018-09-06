package  com;
import com.framework.redis.RedisConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
@SpringBootApplication
@EnableCaching
public class RedisApplication {
    public static void main(String[] args){
        SpringApplication.run(RedisApplication.class,args);
    }
}
