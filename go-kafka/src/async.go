package src

import (
	"context"
	"log"
	"strconv"
	"sync"
	"time"

	"github.com/IBM/sarama"
)

// 异步生产者
func asyncProducer(topic string, total int) {
	config := sarama.NewConfig()
	config.Producer.Return.Errors = true    // 是否返回错误信息
	config.Producer.Return.Successes = true // 是否返回成功信息
	producer, err := sarama.NewAsyncProducer([]string{"127.0.0.1:9092"}, config)
	if err != nil {
		panic(err)
	}

	defer producer.AsyncClose()

	var (
		wg                                    sync.WaitGroup
		enqueued, timedOut, succeeded, failed int
	)

	wg.Add(1)
	go func() {
		defer wg.Done()
		for s := range producer.Successes() {
			log.Printf("[Producer] Success: key:%v msg:%+v \n", s.Key, s.Value)
			succeeded++
		}
	}()

	wg.Add(1)
	go func() {
		defer wg.Done()
		for e := range producer.Errors() {
			log.Printf("[Producer] Errors：msg:%v err:%v\n", e.Msg, e.Err)
			failed++
		}
	}()

	// 异步生产
	for i := 0; i < total; i++ {
		str := "value-" + strconv.Itoa(i)
		// 创建消息（也称为记录、事件）
		msg := &sarama.ProducerMessage{
			Topic: topic,
			// 指定键的序列化（对象 -> 字节流）方法
			Key: sarama.StringEncoder("key-" + strconv.Itoa(i)), // 类型转换
			// 指定值的序列化（对象 -> 字节流）方法
			Value: sarama.StringEncoder(str), // 类型转换
		}

		ctx, cancel := context.WithTimeout(context.Background(), 10*time.Millisecond)
		defer cancel()

		select {
		case producer.Input() <- msg:
			enqueued++
		case <-ctx.Done():
			timedOut++
		}
		if i%5 == 0 && i != 0 {
			log.Printf("Sent :%d, Enqueued: %d, Timed out: %d\n",
				i, enqueued, timedOut)
		}
	}

	wg.Wait()
	log.Printf("Done! Sent: %d, Enqueued: %d, Timed out: %d, Succeed: %d, Failed: %d",
		total, enqueued, timedOut, succeeded, failed)
}

func TestAsync() {

}
