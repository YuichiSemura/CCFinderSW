# TroubleShooting

### 実行時のコンソールの出力に「LOC = 0」がある
  拡張子の設定ミスであることが多いです．  
  Option.mdのコメントファイルの説明を参照してください．
  
### Commentファイルの内容が反映されていない  
  Commentファイルのファイル名が正しいか確認してください．  
  [Option.md](UsageJp/Option.md)を参考に，文法が正しいか確認してください．  
  Commentファイルの文字コードをBOMなしUTF-8に変更してください．

### ファイルが存在しないなどのエラー（例外）が発生する
  -d オプションの引数が正しいディレクトリを選択していることを確認してください．   
  -d オプションの引数のファイル区切り文字が，環境で使われたものか確認してください．  
  -d オプションの引数の末尾に余分なファイル区切り文字があるなら削除してください． 