for n in 1 .. 3; do
    for i in caffeine cache2k ohc lmdb; do
        echo "save to $i"
        http --print=b POST "http://localhost:8080/save/$i?k=x&v=$n"
        echo "read from $i"
        http --print=hb "http://localhost:8080/read/$i?k=x"
    done
done
