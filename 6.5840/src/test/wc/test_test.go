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
	ff := func(r rune) bool { return !unicode.IsLetter(r) }

	// split contents into an array of words.
	words := strings.FieldsFunc(contents, ff)

	kva := []mr.KeyValue{}
	for _, w := range words {
		kv := mr.KeyValue{w, "1"}
		kva = append(kva, kv)
	}
	return kva
}

func TestMap(t *testing.T) {
	filename := "test_file"
	contents := "Step,,,Into......a /* a */ Vast  Magical&&&World******of```Adventure!!!!!!"
	kva := Map(filename, contents)
	for _, kv := range kva {
		log.Println(kv.Key, "==", kv.Value)
	}
}
