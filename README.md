## Hello Cache

| Level                  | Candidates                                                                                                                |
|:-----------------------|:--------------------------------------------------------------------------------------------------------------------------|
| Local Memory(On Heap)  | [Caffeine](https://github.com/ben-manes/caffeine)/[Cache2k](https://cache2k.org)/[Guava](https://github.com/google/guava) |
| Local Memory(Off Heap) | [OHC](https://github.com/snazy/ohc)                                                                                       |
| Local Disk             | [LmdbJava](https://github.com/lmdbjava/lmdbjava)/[Chroncile Map](https://github.com/OpenHFT/Chronicle-Map)                |
| Remote Client          | lettuce redisson jedis                                                                                                    |

Unable to make field long java.nio.Buffer.address accessible: module java.base does not "opens
java.nio" - LMDB using
Java API

```sh
--add-opens=java.base/java.nio=ALL-UNNAMED
```