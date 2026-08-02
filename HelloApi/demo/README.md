# 起動メモ

## postgresコンテナ起動・確認コマンド

```bash
# 起動
docker compose up -d
# コンテナ確認
docker ps -a    # postgres:16が Up になること

# postgresコンテナ内に入る
docker compose exec postgres bash
```

もし、`docker compose up -d`をやり直したい場合は、以下を実行。
```bash
docker compose down -v
```

## postgres接続
postgresコンテナ内に入れば、psqlがすでに入っているので、そのまま実行できる。

しかし、codespaceでは標準ではないっていないため、.devcontainer.jsonに記述しない場合は別途インストールする必要がある。

```bash
sudo apt-get update
sudo apt-get install -y postgresql-client
```

postgres接続して、DDLを流すコマンド
```bash
# DDLの実行
psql -h localhost -p 5432 -U appuser -d appdb -f ./db/ddl/001_create_todo_table.sql

# postgresのappdbに接続
psql -h localhost -p 5432 -U appuser -d appdb

# DB内の情報確認（001_create_todo_table.sql のレコードが見れること）
SELECT id , title , done FROM todo ORDER BY id;
```


## springboot起動コマンド

```bash
# コンパイル
./mvnw clean compile

# 実行
./mvnw spring-boot:run
```
