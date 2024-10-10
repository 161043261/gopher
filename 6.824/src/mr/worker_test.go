package mr

import "testing"

func Test_ihash(t *testing.T) {
	// 对于 32 位机器，math.MaxInt = math.MaxInt32
	// 对于 64 位机器，math.MaxInt = math.MaxInt64
	// math.MaxInt32 = 0x7fff_ffff
	ihash("bronya.com") // true
}
