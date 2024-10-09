# Kafka

> 荣花与炎日之途

预备

```shell
# 删除容器和数据卷
docker-compose down -v # volume
# 以 detach 分离模式运行容器
docker-compose up -d # detach
# 进入容器
# -it 表示打开一个 interactive tty 交互式终端
docker exec -it kafka_container /bin/bash
```

### 快速开始

Kafka 是一个分布式的事件流平台（消息队列）

1. 发布（写入）和订阅（读出）事件 -> 内存
2. 持久化存储事件 -> 磁盘
3. 处理事件

Keywords

```text
Topic 主题（目录）
└── Partition 分区（子目录）
    ├── Event1 事件1（文件）
    └── Event2 事件2
```

| Keywords       |          |
| -------------- | -------- |
| 主题 topic     |          |
| 分区 partition | 消息队列 |
| 事件 event     | 消息     |

- 事件：记录或消息
- 发布者（生产者）、订阅者（消费者）
- 主题、分区：一个主题包含多个分区
  分区 (partition) 分布在（不同）Kafka 代理 (broker) 上的多个桶 (bucket) 中，允许客户端同时从多个代理读出数据或向多个代理写入数据
- 相同键 (e.g. ID) 的事件会被写入相同分区，即相同键的消息会被写入相同的消息队列
- 分区（消息队列）是 FIFO 的

```shell
cd /
find . -name "kafka-topics.sh"
cd /opt/bitnami/kafka/bin
# 创建一个主题 topic
# bootstrap 引导
kafka-topics.sh --create --topic quickstart-events --bootstrap-server localhost:9092
# 打印主题 topic 的描述信息
kafka-topics.sh --describe --topic quickstart-events --bootstrap-server localhost:9092
# 生产者：将一些事件（记录或消息，即字符串）写入主题
# 默认一行一个事件
kafka-console-producer.sh --topic quickstart-events --bootstrap-server localhost:9092
>This is my first event
>This is my second event
# 消费者：读出这些事件
kafka-console-consumer.sh --topic quickstart-events --from-beginning --bootstrap-server localhost:9092
```

查看连接的客户端

```shell
kafka-consumer-groups.sh --bootstrap-server localhost:9092
```