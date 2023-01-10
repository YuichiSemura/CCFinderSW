# CCFinderSW

CCFinderSW (CCFSW) はトークンベースのコードクローン検出ツールです．

## コードクローンとは

コードクローンはソースコード中に存在する，一致または類似するコード片のことを指します．  
互いに類似したコード片をクローンペアと呼び，本ツールはこれを検出し，出力します．  
また現在クローンペアは，文の挿入・削除など含むものは対象外としています．

## 最新バージョン

以下のリンクからダウンロードしてください．利用方法は [Run.md](UsageJp/Run.md) を参照してください．

[CCFinderSW-1.0.zip](build/distributions/CCFinderSW-1.0.zip)

## 使い方

いくつかのドキュメントが存在します．

- 実行について
  - [Run.md](UsageJp/Run.md)
- 予約語ファイル・コメントファイルについて
  - [OptionFile.md](UsageJp/OptionFile.md)
- 利用できる Viewer について
  - [OutputAndViewer.md](UsageJp/OutputAndViewer.md)
- 考えられるトラブルについて
  - [TroubleShooting.md](UsageJp/TroubleShooting.md)

## 必要なもの

- Java Runtime Environment( >=8 )
  - 現在，Java のみで実装しています．
- 出来る限り多くの RAM...
  - 対象となるプロジェクトの規模が大きければ大きいほど，字句情報を保持するためのメモリが必要となります．

## Former Versions

- [CCFinder](http://sel.ist.osaka-u.ac.jp/cdtools/ccfinder.html)
- [CCFinderX](http://www.ccfinder.net/ccfinderxos-j.html)

## テスト環境

some quick tests

- CCFinderSW
  - Windows 10 Pro
  - Max OS
  - (probably) Linux
- Icca
  - Windows 10 Pro

## Copyright and Licensing

See LICENSE.  
このソフトウェアには Apache Licence2.0 で配布されているライブラリが含まれています．

## その他の情報

著者: 瀬村 雄一
所属: 大阪大学大学院情報科学研究科 井上研究室

## 論文情報

詳細な情報は論文にて記載されています．以下に論文情報を示します．

```tex
@INPROCEEDINGS{8305997,
  author={Semura, Yuichi and Yoshida, Norihiro and Choi, Eunjong and Inoue, Katsuro},
  booktitle={2017 24th Asia-Pacific Software Engineering Conference (APSEC)},
  title={CCFinderSW: Clone Detection Tool with Flexible Multilingual Tokenization},
  year={2017},
  pages={654-659},
  doi={10.1109/APSEC.2017.80}
}
```

## 利用についての注意点

CCFSW は字句解析を行いますが，言語ごとに個別の字句解析機構は持っていません．  
オプションファイルを使用することで各言語に合わせた字句解析を行います．  
（また同時に，各言語に最適化されたレクサーアルゴリズムではない，ということも意味します．）  
このオプションファイルはユーザ自身が変更できるものです．  
オプションファイルの記法については, [OptionFile.md](UsageJp/OptionFile.md) を参照してください．
