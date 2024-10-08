package work_queues

import (
	"bronya.com/go-rabbitmq/utils"
	"context"
	"fmt"
	amqp "github.com/rabbitmq/amqp091-go"
	"log/slog"
	"strconv"
	"sync"
	"testing"
	"time"
)

var url string
var wg sync.WaitGroup
var msg int

func init() {
	url = "amqp://root:0228@127.0.0.1:5672/"
}

// 工作队列（任务队列）
func newTask() {
	conn, err := amqp.Dial(url)
	utils.FailOnErr(err)
	defer conn.Close()

	ch, err := conn.Channel()
	utils.FailOnErr(err)
	defer ch.Close()

	q, err := ch.QueueDeclare(
		"task_queue", // name
		true,         // durable
		false,        // delete when unused
		false,        // exclusive
		false,        // no-wait
		nil,          // arguments
	)
	utils.FailOnErr(err)

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	body := strconv.Itoa(msg)
	msg++

	err = ch.PublishWithContext(ctx,
		"",     // exchange
		q.Name, // routing key
		false,  // mandatory
		false,
		amqp.Publishing{
			DeliveryMode: amqp.Persistent,
			ContentType:  "text/plain",
			Body:         []byte(body),
		},
	)
	utils.FailOnErr(err)
	slog.Info("[producer] Sent: " + body)
}

func worker(name string) {
	defer wg.Done()

	conn, err := amqp.Dial(url)
	utils.FailOnErr(err)
	defer conn.Close()

	ch, err := conn.Channel()
	utils.FailOnErr(err)
	defer ch.Close()

	queue, err := ch.QueueDeclare(
		"task_queue", // name
		true,         // durable
		false,        // delete when unused
		false,        // exclusive
		false,        // no-wait
		nil,          // argument
	)
	utils.FailOnErr(err)

	err = ch.Qos(
		1,     // prefetch count
		0,     // prefetch size
		false, // global
	)
	utils.FailOnErr(err)

	msgChan, err := ch.Consume(
		queue.Name, // queue
		"",         // consumer
		false,      // auto-ack
		false,      // exclusive
		false,      // no-local
		false,      // no-wait
		nil,        // args
	)
	utils.FailOnErr(err)

	for msg := range msgChan {
		slog.Info(fmt.Sprintf("[%v] Received: %s", name, string(msg.Body)))
		time.Sleep(3 * time.Second) // 模拟任务的执行时间 3s
		msg.Ack(false)
	}
}

// go test -run TestWorkQueues -timeout 30s
func TestWorkQueues(t *testing.T) {
	wg.Add(1)
	go func() {
		defer wg.Done()
		for {
			time.Sleep(3 * time.Second)
			newTask()
		}
	}()

	for i := range 3 {
		wg.Add(1)
		go worker("worker-" + strconv.Itoa(i))
	}
	wg.Wait()
}
