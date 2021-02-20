.PHONY: help

help:
	@cat Makefile  | grep  "^[a-z-].*" | grep -v help

# ============= DB ===================
db:
	cat Makefile | grep -E "^db-"

db-start:
	docker-compose up -d

db-stop:
	docker-compose stop

db-shell:
	mysql -u root -h 127.0.0.1 -ptest test

db-shell-test:
	mysql -u root -h 127.0.0.1 -ptest test_test

db-import-live:
	mysqldump -u root -h 127.0.0.1 -ptest test --column-statistics=0 | gzip > /tmp/test-local-before.sql.gz
	# ssh root@do2 "rm -f /var/www/test-backup.sql.gz"
	# ssh root@do2 "ls -lah /var/www/test-backup.sql.gz"
	ssh root@do2 "mysqldump -u root test --column-statistics=0 | gzip > /var/www/test-backup.sql.gz"
	scp root@do2:/var/www/test-backup.sql.gz /tmp/test-backup.sql.gz
	gzcat /tmp/test-backup.sql.gz | mysql -u root -h 127.0.0.1 -ptest test

db-recreate-test: db-start
	@echo "Dropping and recreating test db..."
	mysql -u root -h 127.0.0.1 -ptest  -e "drop schema if exists test_test; create schema test_test; GRANT ALL ON test_test.* TO 'test';"

db-add-user-elvis:
	curl --header "Content-Type: application/json" \
      --request POST \
      --data '{"email": "elvisciotti@gmail.com","passwordRaw": "123qwe"}' \
      http://localhost:3003/users/signup

db-user:
	mysql -u root -h 127.0.0.1 -ptest test -e "select * from user"

db-rule:
	mysql -u root -h 127.0.0.1 -ptest test -e "select * from rule\G"

db-widget:
	mysql -u root -h 127.0.0.1 -ptest test -e "select * from widget\G"

db-recreate:
	@echo "Dropping and recreating db..."
	mysql -u root -h 127.0.0.1 -ptest -e "drop schema test; create schema test;"

# ============= SPRING ===================
start: db-start
	# https://stackoverflow.com/questions/40060989/how-to-use-spring-boot-profiles
	mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

start-prod: db-start
	mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"

stop: db-stop
	killall java

# ============= TEST ===================
test: db-recreate-test
	mvn test

# ============= RELEASE ===================
deploy:
	date > src/main/resources/version.dat
	mvn test
	git add . || ( echo "already added")
	git commit -m "auto commit" | ( echo "already committed")
	git push -f || ( echo "already pushed. Press to recreate JAR anyway"; read)
	ansible-playbook ansible/release.yml

provision:
	ansible-playbook ansible/server.yml

# ============= PROD ===================
prod-health:
	# curl http://api.flowautomator.com/health-check
	curl https://spring-boot-sample.featurecompare.com/health-check

prod-ssh:
	ssh root@do2

prod-restart:
	ssh root@do2 "systemctl restart test-backend.service"

prod-logs:
	ssh root@do2 "journalctl -u test-backend.service"

# ============= OTHERS ===================

kill:
	@ps aux | grep java | wc -l
	@killall java

push-force:
	git push origin -f
