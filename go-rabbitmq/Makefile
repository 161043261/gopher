.PHONY: docker-rm docker-compose docker-volume get

docker-rm:
	docker rm mq1 -f
	docker volume prune

docker-compose: docker-rm
	docker-compose up -d # daemon

get:
	go get github.com/rabbitmq/amqp091-go

rabbitmqctl:
	# 进入 rabbit 容器
	docker exec mq1 /bin/bash
	# Forgotten acknowledgment 队列忘记 ack 时
	# 包含 messages_ready 和  messages_unacknowledged 字段
	sudo rabbitmqctl list_queues name messages_ready messages_unacknowledged
	# 列出交换机 exchange 与（消息）队列 queue 的绑定
	rabbitmqctl list_bindings
