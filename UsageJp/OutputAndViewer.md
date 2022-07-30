# 出力形式とViewerの使用法

## -ccfsw の出力形式について

- ccfswの内部で保持している情報と，ファイル・クローン情報をtxtファイルで出力します．
- -ccfsw の引数として「pair」と「set」の2種類を選択できます．
  - クローン部の出力が変わります．引数指定なしでpairになります．

出力ファイルは，バージョン・オプション・ルールファイル情報・ソースファイル情報・クローン情報で構成されます．

### バージョン
本ツールのバージョン名
```
#version\t<バージョン名>\n 
```
### オプション　
「-d」「-l」「-o」「-t」「-charset」の内容

```
#option
<オプション名>\t<引数>\n
…
```

### ルールファイル情報
言語と文法ファイルのパス
```
#rule_constructor\n
<言語名>{\n
\tcomment_file\t<コメントファイルパス>\n
\treserved_file\t<予約語ファイルパス>\n
}\n
…
```

### ソースファイル情報
対象ファイルの情報です（ファイル番号は0からスタート）
```
#source_files\n
<ファイル番号>\t<行数>\t<トークン数>\t<ファイルパス>\n
…
```

### クローン情報
クローン情報は#clone_pairsと#clone_setsのどちらかで出力されます．

#### pair
- 一つのクローンペアは 3 行で出力されます
- クローンIDが同じクローンペアはクローンセットになります
(pairの出力を選んでもクローンセットの情報は失われません)
- あるコード片A,Bがクローンペアであるとき，  
1行目がクローンID，  
2行目がコード片Aの位置（開始地点と終了地点），  
3行目がコード片Bの位置（開始地点と終了地点）を表しています  

```
#clone_pairs
cloneID:<クローンID>\n
\t<ファイル番号>:<開始行>,<開始列>Space-Space<終了行>,<終了列>\n
\t<ファイル番号>:<開始行>,<開始列>Space-Space<終了行>,<終了列>\n
cloneID:<クローンID>\n
…
```

#### set
あるコード片A,B,Cがクローンセットであるとき，  
1行目がクローンID，  
2行目がコード片Aの位置（開始地点と終了地点），  
3行目がコード片Bの位置（開始地点と終了地点），  
4行目がコード片Cの位置（開始地点と終了地点），……を表しています

```
#clone_sets
cloneID:<クローンID>\n
\t<ファイル番号>:<開始行>,<開始列>Space-Space<終了行>,<終了列>\n
\t<ファイル番号>:<開始行>,<開始列>Space-Space<終了行>,<終了列>\n
\t<ファイル番号>:<開始行>,<開始列>Space-Space<終了行>,<終了列>\n
cloneID:<クローンID>\n
…
```

## Viewerの使用法
### Gemini(CCFinder)
出力されたファイルをGeminiで開くことができます．  
①  CCFSWに同梱されたIccaという名前のフォルダを開きます．  
②  Iccagui.jarを起動します．Geminiの(2)analyze token-based...を選択し，  
③  Analysis Result Fileに出力されたファイルを選択し，起動します．  

### GemX(CCFinderX)
オプションの「-ccfx」を追加することで，CCFinderXで使用されている形式でファイルの出力がされます．  
CCFinderX形式のファイルはGemXで開くことができます．  
CCFinderXは，CCFinderのHP( http://www.ccfinder.net/ccfinderxos-j.html )でダウンロード可能です．  
GemXを起動するには，Java JDKの“32bit版"が必要です．  
① gemx.batでGemXを起動し，  
② ツールバーのFile→Open Clone Data...を選択し，  
③ CCFSWで出力されたccfxdファイルを選択します．  
