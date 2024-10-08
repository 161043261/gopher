# hash 哈希

键: 哈希表（键值对的集合）

- hset 设置哈希表中一个或多个字段的值
- hget 查询一个字段
- hgetall 查询所有字段
- hmget 查询一个或多个字段
- hincrby n 原子的、将字段（字符串）解析为整数，并 +n

```shell
hset bike:1 \ # 键
model Deimos brand Ergonom type 'Enduro bikes' price 4972 # 哈希表（键值对的集合）

hget bike:1 model
hget bike:1 price
hgetall bike:1
hmget bike:1 model price no-such-field
```

### 例

骑车 ride 次数、撞车 crash 次数或更换车主 owner 次数的计数器

```shell
hincrby bike:1:stats rides 1      # 骑车次数 +1
hincrby bike:1:stats rides 1      # 骑车次数 +1
hincrby bike:1:stats rides 1      # 骑车次数 +1
hincrby bike:1:stats crashes 1    # 撞车次数 +1
hincrby bike:1:stats owners 1     # 更换车主次数 +1
hget bike:1:stats rides           # 3
hmget bike:1:stats owners crashes # 1 1
```

### 字段过期