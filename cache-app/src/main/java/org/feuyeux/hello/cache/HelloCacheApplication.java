package org.feuyeux.hello.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class HelloCacheApplication {

  public static void main(String[] args) {
    args = new String[]{
        "--add-opens=java.base/java.nio=ALL-UNNAMED",
        "--add-opens=java.base/java.lang=ALL-UNNAMED",
        "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
        "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED"
    };

    log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    SpringApplication.run(HelloCacheApplication.class, args);
    log.info("---------------------------");
  }
}
