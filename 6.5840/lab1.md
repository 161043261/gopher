# MapReduce

```shell
cd src/main

go build -buildmode=plugin ../mrapps/wc.go

ls | grep .so # wc.so

rm mr-out*

go run mrsequential.go wc.so pg*.txt
```
