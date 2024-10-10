package wc

import (
	"log"
	"testing"
)

func TestMap(t *testing.T) {
	filename := "test_file"
	contents := "Step,,,Into......a /* */ Vast  Magical&&&World******of```Adventure!!!!!!"
	kva := Map(filename, contents)
	for _, kv := range kva {
		log.Println(kv.Key, "==", kv.Value)
	}
}
