# CCFinderSW
CCFinderSW (CCFSW) はトークンベースのコードクローン検出ツールです．

## コードクローンとは
コードクローンはソースコード中に存在する一致または類似するコード片のことを指します．  
互いに類似したコード片をクローンペアと呼び，本ツールはこれを検出し，出力します．  
現在，文の挿入・削除など含むものは検出対象外としています．

## 最新バージョン
CCFinderSW 0.9

## 実行方法について
CCFinderSW-version.zipを展開してください．  
展開先の/bin/CCFinderSW.batを実行してください．
(現在，全ドキュメントと例にはjarファイルでのものですが，batでも同じコマンドが使用できます)

## コマンド設定方法について
いくつかのドキュメントが存在します．  
実行について  [Run.md](UsageJp/Run.md)  
予約語ファイル・コメントファイルについて [Option.md](UsageJp/Option.md)  
利用できるViewerについて  [OutputAndViewer.md](OutputAndViewer.md)  
考えられるトラブルについて [TroubleShooting.md](UsageJp/TroubleShooting.md)

## ビルドについて
gradleを使用しています．  
```
gradle distZip
```
を行うことで，/build/distributions下にCCinderSW-version.zipが生成されます．

## 必要なもの
- Java Runtime Environment( >=8 )   
  現在，Javaのみで実装しています．
- 出来る限り多くのRAM...  

## テスト環境
Windows 10 Pro

## Copyright and Licensing
See LICENSE.   

ANTLRを使用しています．  
Apache Commons CLIを使用しています  
http://www.apache.org/licenses/LICENSE-2.0

## その他の情報
著者: 瀬村 雄一  
所属: 大阪大学大学院情報科学研究科 井上研究室

## 利用についての注意点
CCFSWは字句解析を行いますが，言語ごとに個別の字句解析は持っていません．  
オプションファイルを使用することで各言語に合わせた字句解析を行います．  
オプションファイルの記法については, Comment.md，ReservedWord.md を参照してください． 
