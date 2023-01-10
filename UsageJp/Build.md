# Build with Gradle

既にビルド済みの zip を解凍すれば CCFSW は実行できます．ビルドを必要としている場合は gradle を利用したビルドが可能です．ファイル構成とビルド方法について記述します．

## gradle build

- ビルドを行うには，Java (version>=8)の実行環境が必要です．
- gradle が導入された環境ならば問題ありません．また `gradlew` と `gradle/gradle-wrapper.jar` が用意されているので，そちらでの実行も可能です．
- `build.gradle` ファイルがある階層で `gradle build` を実行してください．
  - 実行可能 jar ファイルが生成されます．この jar のみを必要としている人は，`build/libs/CCFinderSW-<version>.jar`を利用してください．
  - さらに，関連ライブラリと設定ファイル(`./src/main/dist`)も梱包した zip ファイルが生成されます．zip ファイルは `build/distributions` に生成されます．

## Directory structure

version 1.0.1

```
.
├── Usage
├── UsageJp
├── build
│   └── distributions
├── gradle/wrapper
└── src
    └── main
        ├── dist
        │   ├── comment
        │   ├── grammarsv4
        │   ├── icca/bin
        │   └── reserved
        └── java
            ├── META-INF
            ├── aleesa
            ├── ccfindersw
            ├── clonedetector
            └── common
```

- `./build`
  - gradle の成果物の生成先です．
- `./src`
  - `./src/main/dist`
    - コメント設定ファイル，予約語設定ファイル，ANTLRv4 文法ファイル， icca が入っています．
  - `./src/main/java`
    - `aleesa` package
      - ANTLRv4 文法ファイルの分析に関する機能が含まれています．-antlr で利用されます．
      - ALEESA (A Lexical Element Extractor from Syntax definitions of ANTLR) という名前は好きなキャラクターの名前から取りました．named after 市ヶ谷有咲(BanG Dream!)
    - `ccfindersw` package
      - CLI のための機能が含まれています．
    - `clonedetector` package
      - 字句解析，クローン検出の機能が含まれています．
    - `common` package
      - 共通的な，そして取るに足らないような機能が含まれています．
