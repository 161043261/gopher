
docker-compose down -v # volume

docker-compose up -d # daemon

cd /

find . -name "kafka-topics.sh"

cd /opt/bitnami/kafka/bin

# 创建一个主题 topic
# bootstrap 引导
kafka-topics.sh --create --topic quickstart-events --bootstrap-server localhost:9092

# 打印主题 topic 的描述信息
kafka-topics.sh --describe --topic quickstart-events --bootstrap-server localhost:9092

# 将一些事件写入主题
kafka-console-producer.sh --topic quickstart-events --bootstrap-server localhost:9092