https://github.com/lmdbjava/benchmarks

1. [LmdbJava](https://github.com/lmdbjava/lmdbjava) (with fast `ByteBuffer`, safe `ByteBuffer` and
   an [Agrona](https://github.com/real-logic/Agrona) buffer)
2. [LMDBJNI](https://github.com/deephacks/lmdbjni)
3. [Lightweight Java Game Library](https://github.com/LWJGL/lwjgl3/) (LMDB API)
4. [LevelDBJNI](https://github.com/fusesource/leveldbjni)
5. [RocksDB](http://rocksdb.org/)
6. [MVStore](http://h2database.com/html/mvstore.html) (pure Java)
7. [MapDB](http://www.mapdb.org/) (pure Java)
8. [Xodus](https://github.com/JetBrains/xodus) (pure Java)
9. [Chroncile Map](https://github.com/OpenHFT/Chronicle-Map) (pure Java) (**)

```sh
mvn clean package -Dlicense.skipAddThirdParty=true
./results/run.sh
mkdir -p results/date
mv out-* results/date
cd results/date
../results/process.sh
```

#### [lmdb-benchmarks/results/20160710/README.md](lmdb-benchmarks/results/20160710/README.md)