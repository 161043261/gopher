# redis

redis 数据类型

### [string](./assets/string.md) 字符串

- 键 === 字符串（值）
- 字符串是 Redis **最**基本的数据类型

### [list](./assets/list.md) 链表

- 键 === 字符串链表
- 链表是按插入顺序排序的字符串链表，命令以 l 开头
- 将 list 链表用作栈 stack 和队列 queue
  - 左表头 === 右表尾
  - 左栈顶 === 右栈底

### [hash](./assets/hash.md) 哈希

- 键 === 哈希表（两两一组的，字段、字段值的集合 k1 v1 k2 v2...）
- 哈希用于 map、对象存储

### [set](./assets/set.md) 集合

- 键 === 唯一字符串的无序集合

### [sorted set](./assets/zset.md) 有序集合

> sorted set 有序集合是唯一字符串的有序集合

### [stream](./assets/stream.md) 流

> 流是什么？

### bitmap

### bitfield

### geospatial

就像蝴蝶飞不过沧海，没有谁忍心责怪
