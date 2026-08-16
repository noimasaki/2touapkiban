# GitHub CodespacesでWildFlyにデプロイしてPostgreSQLへ接続する手順

この手順は、このリポジトリの現在の構成（Java 17、Spring Boot 4.1.0、MyBatis、PostgreSQL 16）を対象にしている。

> 現在のアプリは実行可能JARとして作られるため、そのままではWildFlyに配置できない。最初にWAR化が必要。
> 以下ではWildFly 40.0.0.Final（Jakarta EE 11、JDK 17）を使用する。

## 1. アプリをWAR化する

`pom.xml`の`<version>0.0.1-SNAPSHOT</version>`の直後に、次を追加する。

```xml
<packaging>war</packaging>
```

`pom.xml`の`<dependencies>`内に、次の依存関係を追加する。

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>
```

生成されるファイル名を固定するため、`pom.xml`の`<build>`直下に`<finalName>`を追加する。

```xml
<build>
    <finalName>demo</finalName>
    <plugins>
        <!-- 既存内容のまま -->
    </plugins>
</build>
```

`src/main/java/com/example/demo/DemoApplication.java`を次の内容にする。

```java
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class DemoApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(DemoApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

これはSpring Boot公式のTraditional Deployment（従来型WARデプロイ）の構成に沿った変更である。

## 2. Docker ComposeにWildFlyを追加する

`docker-compose.yaml`を次の内容にする。

```yaml
services:
  postgres:
    image: postgres:16
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: appdb
      POSTGRES_USER: appuser
      POSTGRES_PASSWORD: apppass
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U appuser -d appdb"]
      interval: 5s
      timeout: 5s
      retries: 10

  wildfly:
    image: quay.io/wildfly/wildfly:40.0.0.Final-jdk17
    command: >
      /bin/bash -c "if ! grep -q '^admin='
      /opt/jboss/wildfly/standalone/configuration/mgmt-users.properties;
      then /opt/jboss/wildfly/bin/add-user.sh
      -u admin -p 'CodespaceAdmin_2026!' --silent; fi;
      exec /opt/jboss/wildfly/bin/standalone.sh
      -b 0.0.0.0 -bmanagement 0.0.0.0"
    ports:
      - "8080:8080"
      - "9990:9990"
    environment:
      SPRING_DATASOURCE_JNDI_NAME: java:jboss/datasources/AppDS
    volumes:
      - ./target/jdbc-driver/postgresql.jar:/opt/jboss/wildfly/standalone/deployments/postgresql.jar:ro
    depends_on:
      postgres:
        condition: service_healthy
```

ポイントは次のとおり。

- 管理画面用のポート`9990`を公開し、管理ユーザーを起動時に作成する。
- PostgreSQL JDBCドライバをWildFlyへデプロイする。
- Spring BootはURL・ユーザー名・パスワードを直接使わず、WildFly管理のJNDIデータソース`java:jboss/datasources/AppDS`を参照する。

## 3. PostgreSQLを起動してDDLを流す

Codespacesのターミナルで、このディレクトリへ移動して実行する。

```bash
cd HelloApi/demo
docker compose up -d postgres
docker compose exec -T postgres \
  psql -U appuser -d appdb \
  < db/ddl/001_create_todo_table.sql
```

DDLを実行済みのDBにもう一度流すと`todo`テーブル作成時にエラーになる。その場合は、DDLの再実行はせず次へ進む。

DBを確認する。

```bash
docker compose exec postgres \
  psql -U appuser -d appdb \
  -c "SELECT id, title, done FROM todo ORDER BY id;"
```

## 4. JDBCドライバを用意してWildFlyを起動する

```bash
./mvnw dependency:copy-dependencies \
  -DincludeArtifactIds=postgresql \
  -DoutputDirectory=target/jdbc-driver \
  -Dmdep.stripVersion=true
test -f target/jdbc-driver/postgresql.jar
docker compose up -d wildfly
docker compose logs -f wildfly
```

ログにPostgreSQL JDBCドライバのデプロイ完了が表示されたら、`Ctrl+C`でログ表示だけを終了する。コンテナは停止しない。

ドライバのデプロイ状況も確認できる。

```bash
docker compose exec wildfly \
  ls -l /opt/jboss/wildfly/standalone/deployments/
```

`postgresql.jar.deployed`があればデプロイ成功。`postgresql.jar.failed`がある場合は、次で原因を確認する。

```bash
docker compose logs wildfly
```

## 5. WildFly管理画面でデータソースを作成する

1. Codespacesの「ポート」タブを開き、ポート`9990`の転送URLを開く。
2. WildFly管理画面へ次のユーザーでログインする。
   - ユーザー名: `admin`
   - パスワード: `CodespaceAdmin_2026!`
3. `Configuration` → `Subsystems` → `Datasources & Drivers` → `Datasources`を開く。
4. `Add`からデータソースを追加し、次の値を設定する。

| 項目 | 値 |
|---|---|
| Name | `AppDS` |
| JNDI Name | `java:jboss/datasources/AppDS` |
| Driver | デプロイ済みのPostgreSQLドライバ |
| Connection URL | `jdbc:postgresql://postgres:5432/appdb` |
| User Name | `appuser` |
| Password | `apppass` |

WildFlyコンテナから見たDBホスト名は`localhost`ではなく、Composeのサービス名`postgres`になる。

設定画面の`Test Connection`を実行し、接続成功になることを確認してから保存・有効化する。

## 6. WARを作成してWildFlyへデプロイする

`clean`を付けると`target/jdbc-driver/postgresql.jar`も削除されるため、ここでは`package`だけを実行する。

```bash
./mvnw package
test -f target/demo.war
docker compose cp target/demo.war \
  wildfly:/opt/jboss/wildfly/standalone/deployments/demo.war
docker compose exec wildfly \
  touch /opt/jboss/wildfly/standalone/deployments/demo.war.dodeploy
docker compose logs -f wildfly
```

`demo.war`のデプロイ完了が表示されたら、`Ctrl+C`でログ表示だけを終了する。`demo.war.deployed`が作成されていれば成功。

```bash
docker compose exec wildfly \
  ls -l /opt/jboss/wildfly/standalone/deployments/
```

## 7. Codespacesから動作確認する

ターミナル内では次を実行する。

```bash
curl http://localhost:8080/demo/hello
curl http://localhost:8080/demo/todos
```

期待する結果は、1つ目が`Hello, AP基盤`、2つ目がDDLで登録されたTodoを含むJSON。

ブラウザから見る場合は、Codespacesの「ポート」タブでポート`8080`の転送URLを開き、そのURLの末尾に次を付ける。

```text
/demo/hello
/demo/todos
```

ポートの公開範囲は、動作確認だけなら`Private`のままでよい。

## 8. コード変更後の再デプロイ

```bash
./mvnw package
docker compose cp target/demo.war \
  wildfly:/opt/jboss/wildfly/standalone/deployments/demo.war
docker compose exec wildfly \
  touch /opt/jboss/wildfly/standalone/deployments/demo.war.dodeploy
docker compose logs -f wildfly
```

## 9. 停止する

```bash
docker compose down
```

DBデータも完全に消して作り直す場合だけ、次を使う。

```bash
docker compose down -v
```

## 補足

- この構成では、PostgreSQLへの接続情報と接続プールをWildFlyが管理し、アプリはJNDI経由で利用する。
- `docker compose down`でWildFlyコンテナを削除すると、管理画面で作成したデータソース設定も消える。再起動だけなら`docker compose restart wildfly`を使う。
- 学習・開発環境向けに分かりやすさを優先し、パスワードをComposeへ直接記載している。本番環境ではSecretsなどへ移す。
- 管理ポート`9990`はCodespacesの「ポート」タブで`Private`のまま使用し、Publicにはしない。

## 確認に使用した公式資料

- [Spring Boot: Traditional Deployment](https://docs.spring.io/spring-boot/how-to/deployment/traditional-deployment.html)
- [WildFly 40 Getting Started Guide](https://docs.wildfly.org/40/Getting_Started_Guide.html)
- [WildFly公式コンテナ](https://github.com/wildfly/wildfly-container)
- [WildFly 40リリース情報（EE 11、Java 17対応）](https://www.wildfly.org/news/2026/05/21/WildFly-40-is-released/)
- [GitHub Codespaces: ポートフォワーディングのトラブルシューティング](https://docs.github.com/en/codespaces/troubleshooting/troubleshooting-port-forwarding-for-github-codespaces)
