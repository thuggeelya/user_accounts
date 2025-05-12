# User Accounts

### Контакты:
#### [Telegram](https://t.me/thuggeelya)

---

## Инструкция к запуску проекта

* установить JDK 17 (например, [отсюда](https://adoptium.net/temurin/releases/?version=17&package=jdk))

* установить Docker (Docker Desktop для Windows/MacOS)

* клонировать и собрать проект:

```bash
git clone https://github.com/thuggeelya/user_accounts.git

cd ./user-accounts

mvn -f pom.xml clean install -DskipTests
```

* запустить проект нужно локально с зависимостями в контейнерах:

```bash
# для Windows
docker-compose -f .ci/docker-compose.yml up -d --build
mvn spring-boot:run
```

```bash
# для MacOS
docker compose -f .ci/docker-compose.yml up -d --build
mvn spring-boot:run
```

---

## Используемые технологии

* Java 17
* Spring Boot 3.4.5
* PostgreSQL
* Liquibase
* Elasticsearch
* Maven
* Redis
* Logstash
* Docker
* Testcontainers

---

## Логика

### API

1. Сервер будет доступен по адресу [localhost:8080](http://localhost:8080)
2. Войти под своими доступами можно будет по ссылке [localhost:8080/api/1/login](http://localhost:8080/api/1/login)
3. Описание API будет доступно по [ссылке](http://localhost:8080/swagger-ui.html)

### Решения

#### База данных

* для учета изменения пользовательского баланса была добавлена дополнительная таблица `user_balance_history`
* для ее связи с аккаунтом пользователя были добавлены триггеры в миграцию, потому что пользователи создаются
не через API

#### Elasticsearch

* для поиска по фильтрам из ТЗ используется **Elasticsearch**, но он расширен использованием **Logstash**
* это сделано для синхронизации хранилища, потому что новые пользователи не создаются через приложение
(а, например, через миграции или вручную)
* для наглядности, синхронизация происходит раз в минуту, cron можно поменять в
[конфиге Logstash](.ci/logstash/logstash.conf)
* данные Elasticsearch можно отследить через [Kibana](http://localhost:5601)
* по-хорошему, нужно пропускать события, которые сервер публикует для записи в Elasticsearch, через очередь сообщений
(например, rabbitmq), но этого не было сделано в рамках данного проекта

#### Авторизация

* добавлена простая авторизация через JWT
* работает в основном на основе [этого](src/main/java/ru/thuggeelya/useraccounts/aspect/AuthJwtAspect.java) аспекта 
* на тестах отключена

#### Тестирование

* для запуска тестов необходим запущенный Elasticsearch:

```bash
docker-compose -f .ci/docker-compose.yml elasticsearch
```

```bash
mvn test
```
