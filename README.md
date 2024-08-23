## Hello Cache

| Level                  | Candidates                                                                                                                |
|:-----------------------|:--------------------------------------------------------------------------------------------------------------------------|
| Local Memory(On Heap)  | [Caffeine](https://github.com/ben-manes/caffeine)/[Cache2k](https://cache2k.org)/[Guava](https://github.com/google/guava) |
| Local Memory(Off Heap) | [OHC](https://github.com/snazy/ohc)                                                                                       |
| Local Disk             | [LmdbJava](https://github.com/lmdbjava/lmdbjava)/[Chroncile Map](https://github.com/OpenHFT/Chronicle-Map)                |
| Remote Client          | lettuce redisson jedis                                                                                                    |
## Test

```sh
sh test.sh
```

## Benchmark

### Run Benchmark

```sh
$ sh sh/jvm-benchmark.sh
$ head data/Jvm_benchmark_ZGC.out
$ head data/Jvm_benchmark_ZGC.json
```

```sh
sh sh/remote-benchmark.sh 

```


### JMH Chars

- https://jmh.morethan.io/