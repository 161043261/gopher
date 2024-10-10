package mr

import (
	"context"
	"fmt"
	"os/exec"
	"testing"
	"time"
)

// ./worker.go
//
// func ihash(key string) int
func TestIhash(t *testing.T) {
	// 对于 32 位机器，math.MaxInt = math.MaxInt32
	// 对于 64 位机器，math.MaxInt = math.MaxInt64
	// math.MaxInt32 = 0x7fff_ffff
	ihash("bronya.com") // true
}

// ./rpc.go
//
// func coordinatorSock() string
func TestCoordinatorSock(t *testing.T) {
	coordinatorSock() // /var/tmp/5840-mr-1000
}

// ./coordinator.go
//
// func MakeCoordinator(files []string, nReduce int) *Coordinator
func TestMakeCoordinator(t *testing.T) {
	MakeCoordinator([]string{}, 0)

	// ctx, cancel := signal.NotifyContext(context.Background(), os.Interrupt)
	// defer cancel()

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	cmd := exec.Command("lsof", "+U")  // 打印 unix 进程
	output, _ := cmd.Output()
	fmt.Println(string(output))

	<-ctx.Done()
}
