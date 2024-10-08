# string 字符串

### set

```shell
set bike:1 Deimos
get bike:1
```

### setnx, setxx

- `setnx`: 如果 key 已存在，则 set 失败
- `setxx`: 如果 key 不存在，则 set 失败

```shell
set bike:1 bike nx
set bike:1 bike xx
```

### mset, mget

`mset`, `mget`: 在一条命令中 set 或 get 多个键

```shell
mset bike:1 "Deimos" bike:2 "Ares" bike:3 "Vanth"
mget bike:1 bike:2 bike:3
```

### 字符串作为计数器

- incr 原子的、将字符串解析为整数，并 +1
- incrby n 原子的、将字符串解析为整数，并 +n
- decr 原子的、将字符串解析为整数，并 -1
- decrby n 原子的、将字符串解析为整数，并 -n

incr 等操作（读出 -- 加/减 -- 写入）是原子的

```shell
set total_crashes 0
incr total_crashes
incrby total_crashes 10
```

### 总结

- set, get
- setnx, setxx
- mset, mget
- incr, decr
- incrby, decrby
- incrbyfloat, decrbyfloat
