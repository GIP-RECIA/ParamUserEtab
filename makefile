.PHONY: help db db-init db-down db-clean wait-db run build test clean init-project ldap ldap-down
SHELL := /bin/bash
DB_CONTAINER := paramuseretab-db
DB_IMAGE := mariadb:11
DB_PORT := 3307
LDAP_DIR := ../docker-openldap-test

help:
	@echo "make db	- lance MariaDB en local (conteneur Docker, port $(DB_PORT))"
	@echo "make db-init	- cree la base sarapis_test + la table astructure"
	@echo "make db-down	- arrete le conteneur (les donnees sont gardees)"
	@echo "make db-clean	- arrete et supprime le conteneur + ses donnees"
	@echo "make build	- compile le projet (./mvnw clean package)"
	@echo "make run	- lance l'application (profil local, logs console actives)"
	@echo "make test	- lance les tests unitaires"
	@echo "make clean	- nettoie les build artifacts"
	@echo "make ldap	- lance le ldap local"
	@echo "make ldap-down	- arrête le conteneur du ldap local"
	@echo ""
	@echo "Ordre pour repartir de zero : make db -> make wait-db -> make db-init -> make run"
	@echo "make init-project  - tout en un : lance la BDD, attend qu'elle soit prete, cree le schema"

db:
	@if [ "$$(docker ps -aq -f name=^$(DB_CONTAINER)$$)" ]; then \
		    echo "Conteneur $(DB_CONTAINER) existe deja, demarrage..."; \
		    docker start $(DB_CONTAINER); \
	else \
		    docker run -d --name $(DB_CONTAINER) \
				  -p $(DB_PORT):3306 \
				  -e MARIADB_ROOT_PASSWORD=root \
				  -e MARIADB_DATABASE=sarapis_test \
				  $(DB_IMAGE); \
	fi
	@echo "Attends ~10s que MariaDB soit prete, puis lance 'make db-init'"

db-init:
	docker exec -i $(DB_CONTAINER) mariadb -uroot -proot sarapis_test -e \
		    "CREATE TABLE IF NOT EXISTS astructure (siren VARCHAR(50) PRIMARY KEY, nomCourt VARCHAR(255), siteWeb VARCHAR(255), logo VARCHAR(255));"
	@echo "Table astructure prete."

db-down:
	docker stop $(DB_CONTAINER)

db-clean:
	docker rm -f $(DB_CONTAINER)

wait-db:
	@echo "Attente du demarrage de MariaDB..."
	@until docker exec $(DB_CONTAINER) mariadb -uroot -proot -e "SELECT 1" > /dev/null 2>&1; do sleep 1; done
	@echo "MariaDB est prete."

build:
	./mvnw clean package

run:
	./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dlogging.config=classpath:logback-dev.xml -Dspring.profiles.active=local"

test:
	./mvnw test

clean:
	./mvnw clean

init-project: db wait-db db-init ldap
	@echo "Projet initialise : base de donnees prete. Lance 'make run' pour demarrer l'app."
ldap:
	cd $(LDAP_DIR) && docker compose up -d

ldap-down:
	cd $(LDAP_DIR) && docker compose down
