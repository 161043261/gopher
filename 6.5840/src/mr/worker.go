package mr

import (
	"fmt"
	"hash/fnv"
	"log"
	"math"
	"net/rpc"
)

// KeyValue Map functions return a slice of KeyValue.
type KeyValue struct {
	Key   string
	Value string
}

// ByKey for sorting by key.
type ByKey []KeyValue

// Len for sorting by key.
func (a ByKey) Len() int           { return len(a) }
func (a ByKey) Swap(i, j int)      { a[i], a[j] = a[j], a[i] }
func (a ByKey) Less(i, j int) bool { return a[i].Key < a[j].Key }

// use ihash(key) % NReduce to choose the reduce
// task number for each KeyValue emitted by Map.
//
// ihash 计算字符串 key 的哈希值
func ihash(key string) int {
	h := fnv.New32a()
	h.Write([]byte(key))
	log.Println("math.MaxInt == 0x7fff_ffff: ", math.MaxInt == 0x7fff_ffff)     // false
	log.Println("math.MaxInt32 == 0x7fff_ffff: ", math.MaxInt32 == 0x7fff_ffff) // true
	log.Println("1 << 31 - 1 == 0x7fff_ffff: ", 1<<31-1 == 0x7fff_ffff)         // true
	return int(h.Sum32() & 0x7fffffff)
}

// Worker main/mrworker.go calls this function.
func Worker(mapf func(string, string) []KeyValue,
	reducef func(string, []string) string) {

	// Your worker implementation here.

	// uncomment to send the Example RPC to the coordinator.
	CallExample()
}

// CallExample example function to show how to make an RPC call to the coordinator.
// 向协调器发送 RPC 请求
// the RPC argument and reply types are defined in rpc.go.
func CallExample() {

	// declare an argument structure.
	args := ExampleArgs{}

	// fill in the argument(s).
	args.X = 99

	// declare a reply structure.
	reply := ExampleReply{}

	// send the RPC request, wait for the reply.
	// the "Coordinator.Example" tells the
	// receiving server that we'd like to call
	// the Example() method of struct Coordinator.
	ok := call("Coordinator.Example", &args, &reply)
	if ok {
		// reply.Y should be 100.
		fmt.Printf("reply.Y %v\n", reply.Y)
	} else {
		fmt.Printf("call failed!\n")
	}
}

// send an RPC request to the coordinator, wait for the response.
// usually returns true.
// returns false if something goes wrong.
func call(rpcname string, args interface{}, reply interface{}) bool {
	// c, err := rpc.DialHTTP("tcp", "127.0.0.1"+":1234")
	sockname := coordinatorSock()
	log.Printf("sockname: %s", sockname) // /var/tmp/5840-mr-1000

	// rpc.DialHTTP("unix", "/var/tmp/5840-mr-1000")
	rpcClient, err := rpc.DialHTTP("unix", sockname)
	if err != nil {
		log.Fatal("dialing:", err)
	}
	defer rpcClient.Close()

	// rpcname: "Coordinator.Example"
	// args: ExampleArgs{X: 99}
	// reply: ExampleReply{}

	err = rpcClient.Call(rpcname, args, reply)
	if err == nil {
		return true
	}

	fmt.Println(err)
	return false
}
