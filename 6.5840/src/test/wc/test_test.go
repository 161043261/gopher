package wc_test

import (
	"6.5840/mr"
	"log"
	"strings"
	"testing"
	"unicode"
)

func Map(filename string, contents string) []mr.KeyValue {
	// function to detect word separators.
	// 判断 r 是否为 非字母 字符
	ff := func(r rune) bool { return !unicode.IsLetter(r) }

	// split contents into an array of words.
	// 调用分割函数 ff 将字符串 contents 分割为子串的切片
	words := strings.FieldsFunc(contents, ff)

	kva := []mr.KeyValue{}
	for _, w := range words {
		kv := mr.KeyValue{w, "1"}
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
