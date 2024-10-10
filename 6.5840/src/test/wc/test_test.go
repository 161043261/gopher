package wc_test

import (
	"fmt"
	"log"
	"os"
	"strings"
	"testing"
	"unicode"

	"6.5840/mr"
)

func Map(filename string, contents string) []mr.KeyValue {
	// function to detect word separators.
	// 判断 r 是否为 非字母 字符
	ff := func(r rune) bool { return !unicode.IsLetter(r) }

	// split contents into an array of words.
	// 调用分割函数 ff 将字符串 contents 分割为子串的切片
	words := strings.FieldsFunc(contents, ff)

	var kva []mr.KeyValue
	for _, w := range words {
		kv := mr.KeyValue{Key: w, Value: "1"}
		kva = append(kva, kv)
	}
	return kva // 键值对的切片，值都是 "1"
}

func TestMap(t *testing.T) {
	filename := "test_file"
	contents := "Step,,,Into......a /* a */ Vast  Magical&&&World******of```Adventure!!!!!!"
	kva := Map(filename, contents)
	for _, kv := range kva {
		log.Println(kv.Key, "==", kv.Value)
	}
}

func Lab1Map(contents string, tmpfile string) {
	// function to detect word separators.
	// 判断 r 是否为 非字母 字符
	ff := func(r rune) bool { return !unicode.IsLetter(r) }

	// split contents into an array of words.
	// 调用分割函数 ff 将字符串 contents 分割为子串的切片
	words := strings.FieldsFunc(contents, ff)

	// 创建 tmp 文件
	stream, err := os.OpenFile(tmpfile, os.O_APPEND|os.O_CREATE|os.O_WRONLY|os.O_TRUNC, 0755)
	defer stream.Close()
	for _, w := range words {
		kv := mr.KeyValue{Key: w, Value: "1"}
		if err != nil {
			panic(err.Error())
		}
		bytes := []byte(fmt.Sprintln(kv))

		stream.Write(bytes[1 : len(bytes)-2])
		stream.Write([]byte{'\n'})
	}
}

// go test -run TestLab1Map
func TestLab1Map(t *testing.T) {
	contents := "Step,,,Into......a /* a */ Vast  Magical&&&World******of```Adventure!!!!!!"
	tmpfile := "./map_test"
	Lab1Map(contents, tmpfile)
}
