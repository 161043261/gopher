package hello_world

import (
	"bronya.com/go-rabbitmq/utils"
	"context"
	amqp "github.com/rabbitmq/amqp091-go"
	"log/slog"
	"testing"
	"time"
)

var url string

func init() {
	url = "amqp://root:0228@127.0.0.1:5672/"
}

func send() {
	conn, err := amqp.Dial(url)
	utils.FailOnErr(err)
	defer conn.Close()

	ch, err := conn.Channel()
	utils.FailOnErr(err)
	defer ch.Close()

	q, err := ch.QueueDeclare(
		"hello_world", // name
		false,         // durable
		false,         // delete when unused
		false,         // exclusive
		false,         // no-wait
		nil,           // arguments
	)
	utils.FailOnErr(err)

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	body := "Hello World"
	err = ch.PublishWithContext(ctx,
		"",     // exchange
		q.Name, // routing key 路由到指定队列
		false,  // mandatory
		false,  // immediate
		amqp.Publishing{
			ContentType: "text/plain",
			Body:        []byte(body),
		},
	)
	utils.FailOnErr(err)
	slog.Info("[producer] Sent: " + body)
}

func receive() {
	conn, err := amqp.Dial(url)
	utils.FailOnErr(err)
	defer conn.Close()

	ch, err := conn.Channel()
	utils.FailOnErr(err)
	defer ch.Close()

	queue, err := ch.QueueDeclare(
		"hello_world", // name
		false,         // durable
		false,         // delete when unused
		false,         // exclusive
		false,         // no-wait
		nil,           // arguments
	)
	utils.FailOnErr(err)

	msgChan, err := ch.Consume(queue.Name, // queue
		"",    // consumer
		true,  // auto-ack
		false, // exclusive
		false, // no-local
		false, // no-wait
		nil,   // args
	)
	utils.FailOnErr(err)

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	go func() {
		for msg := range msgChan {
			slog.Info("[consumer] Received: " + string(msg.Body))
		}
	}()

	<-ctx.Done()
}

// go test -run TestHelloWorld
func TestHelloWorld(t *testing.T) {
	send()
	receive()
}
