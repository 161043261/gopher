.PHONY: up down

up:
	touch ./my.cnf
	docker up -d

down:
	docker down -v

redis:
	docker exec -it redis redis-cli # interactive tty
