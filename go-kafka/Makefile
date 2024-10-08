.PHONY: docker-compose get

docker-compose: docker-rm
	docker-compose up -d
	docker ps -a

docker-rm:
	docker rm -f zoo1 kafka1
	rm -rf ./volume/zoo1 ./volume/kafka1
	mkdir  ./volume/zoo1 ./volume/kafka1

get:
	go get -u github.com/segmentio/kafka-go