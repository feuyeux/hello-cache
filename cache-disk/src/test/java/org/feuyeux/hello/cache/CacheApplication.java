package org.feuyeux.hello.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class CacheApplication {

  public static void main(String[] args) {
    String[] args2 = new String[]{
        "--add-opens=java.base/java.nio=ALL-UNNAMED",
        "--add-opens=java.base/java.lang=ALL-UNNAMED",
        "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
        "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED"
    };
    String[] args0 = new String[args.length + args2.length];
    System.arraycopy(args, 0, args0, 0, args.length);
    System.arraycopy(args2, 0, args0, args.length, args2.length);

    SpringApplication.run(CacheApplication.class, args0);
  }
}
