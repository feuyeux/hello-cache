package org.feuyeux.hello.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CacheController {

  @Autowired
  private Cache2kCoon<String, String> cache2kCoon;
  @Autowired
  private CaffeineCoon<String, String> caffeineCoon;
  @Autowired
  private OhcCoon<String> ohcCoon;
  @Autowired
  private LmdbCoon<String> lmdbCoon;

  @PostMapping("/save/{engine}")
  public String save(
      @PathVariable String engine,
      @RequestParam(value = "k") String key,
      @RequestParam(value = "v") String value
  ) {
    switch (engine) {
      case "caffeine":
        caffeineCoon.put(key, value);
        return "OK";
      case "cache2k":
        cache2kCoon.put(key, value);
        return "OK";
      case "ohc":
        ohcCoon.put(key, value);
        return "OK";
      case "lmdb":
        lmdbCoon.put(key, value);
        return "OK";
      default:
        return "Unsupported";
    }
  }

  @GetMapping("/read/{engine}")
  public String read(
      @PathVariable String engine,
      @RequestParam(value = "k") String key
  ) {
    return switch (engine) {
      case "caffeine" -> caffeineCoon.get(key);
      case "cache2k" -> cache2kCoon.get(key);
      case "ohc" -> ohcCoon.get(key);
      case "lmdb" -> lmdbCoon.get(key);
      default -> "Unsupported";
    };
  }
}
