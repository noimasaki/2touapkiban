# 2統括AP基盤お勉強用

## 環境構築（codespace）

リポジトリ内で`.devcontainer/devcontainer.json`を作成し、以下を記述する。

```json
{
  "name": "Java 17 + Maven",
  "image": "mcr.microsoft.com/devcontainers/java:17-bookworm",

  "features": {
    "ghcr.io/devcontainers/features/java:1": {
      "version": "none",
      "installMaven": true,
      "installGradle": false
    }
  },

  "customizations": {
    "vscode": {
      "extensions": [
        "vscjava.vscode-java-pack"
      ]
    }
  },

  "postCreateCommand": "java -version && mvn -version"
}
```

あとは、codespaceを新しくcreateするだけ。

動作確認は以下の通り。

```bash
java -version
mvn -version
```

## Hello Worldしてみる
1. `helloworld/Hello.java`を作成
2. `helloworld`ディレクトリで、コンパイルする

```bash
javac Hello.java
```

3. 実行
```bash
java Hello
```

## SpringBootでHello Worldしてみる
1. VScodeの拡張機能の`SpringInitializer`をCodespaceに追加し、SpringBootプロジェクトを作成（`./hellospringbootweb`）
2. Controllerとhtmlテンプレートを作成して、起動クラスを実行する。
3. 無事に起動すると、ポート転送されるので、ボタン押下すれば、ブラウザからCodespace上で実行されたAPサーバにアクセスできる。

![alt text](./image/1.png)
