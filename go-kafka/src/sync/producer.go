package sync

import "github.com/IBM/sarama"

func Produce(topic string, limit int) {
	config := sarama.NewConfig()
	config.Producer.Return.Successes = true
	config.Producer.Return.Errors = true
}
