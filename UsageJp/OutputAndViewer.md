# 出力形式と Viewer の使用法

## -ccfsw の出力形式について

- ccfsw の内部で保持している情報と，ファイル・クローン情報を txt ファイルで出力します．
- -ccfsw の引数として「pair」と「set」の 2 種類を選択できます．
  - クローン部の出力が変わります．引数指定なしで pair になります．

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

対象ファイルの情報です（ファイル番号は 0 からスタート）

```
#source_files\n
<ファイル番号>\t<行数>\t<トークン数>\t<ファイルパス>\n
…
```

### クローン情報

クローン情報は#clone_pairs と#clone_sets のどちらかで出力されます．

#### pair

- 一つのクローンペアは 3 行で出力されます
- クローン ID が同じクローンペアはクローンセットになります
  (pair の出力を選んでもクローンセットの情報は失われません)
- あるコード片 A,B がクローンペアであるとき，  
  1 行目がクローン ID，  
  2 行目がコード片 A の位置（開始地点と終了地点），  
  3 行目がコード片 B の位置（開始地点と終了地点）を表しています

```
#clone_pairs
cloneID:<クローンID>\n
\t<ファイル番号>:<開始行>,<開始列>Space-Space<終了行>,<終了列>\n
\t<ファイル番号>:<開始行>,<開始列>Space-Space<終了行>,<終了列>\n
cloneID:<クローンID>\n
…
```

#### set

あるコード片 A,B,C がクローンセットであるとき，  
1 行目がクローン ID，  
2 行目がコード片 A の位置（開始地点と終了地点），  
3 行目がコード片 B の位置（開始地点と終了地点），  
4 行目がコード片 C の位置（開始地点と終了地点），……を表しています

```
#clone_sets
cloneID:<クローンID>\n
\t<ファイル番号>:<開始行>,<開始列>Space-Space<終了行>,<終了列>\n
\t<ファイル番号>:<開始行>,<開始列>Space-Space<終了行>,<終了列>\n
\t<ファイル番号>:<開始行>,<開始列>Space-Space<終了行>,<終了列>\n
cloneID:<クローンID>\n
…
```

## Viewer の使用法

### Gemini(CCFinder)

出力されたファイルを Gemini で開くことができます．※　 Windows でのみ動作確認をしています。

1. CCFSW に同梱された Icca という名前のフォルダを開きます．
1. Iccagui.jar を起動します．Gemini の(2)analyze token-based...を選択し，
1. Analysis Result File に出力されたファイルを選択し，起動します．

### GemX(CCFinderX)

オプションの「-ccfx」を追加することで，CCFinderX で使用されている形式でファイルの出力がされます．  
CCFinderX 形式のファイルは GemX で開くことができます．  
CCFinderX は，CCFinder の HP( http://www.ccfinder.net/ccfinderxos-j.html )でダウンロード可能です．  
GemX を起動するには，Java JDK の“32bit 版"が必要です．

1. gemx.bat で GemX を起動し，
1. ツールバーの File→Open Clone Data...を選択し，
1. CCFSW で出力された ccfxd ファイルを選択します．
