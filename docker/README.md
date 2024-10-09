# Kafka

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

介绍：Kafka 是一个分布式的事件流平台（消息队列），可以在多个主机上读写（内存）、存储（磁盘）和处理事件，事件也称为记录或消息。事件按主题分组和存储，主题类似于文件夹，事件类似于文件夹下的文件

```shell
cd /
find . -name "kafka-topics.sh"
cd /opt/bitnami/kafka/bin
# 创建一个主题 topic
# bootstrap 引导
kafka-topics.sh --create --topic quickstart-events --bootstrap-server localhost:9092
# 打印主题 topic 的描述信息
kafka-topics.sh --describe --topic quickstart-events --bootstrap-server localhost:9092
# 将一些事件（记录或消息，即字符串）写入主题，ctrl-c 退出
# 默认一行即一个事件
kafka-console-producer.sh --topic quickstart-events --bootstrap-server localhost:9092
>This is my first event
>This is my second event
```