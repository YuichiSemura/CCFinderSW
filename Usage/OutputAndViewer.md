# Output format and how to use Viewer

## -ccfsw output format

- The information held inside ccfsw and the file/clone information are output as a text file.
- You can select either `pair` or `set` as the argument of -ccfsw.
  - The output of the clone part changes. If no argument is specified, `pair` is selected.

The output file consists of version name, options, rule file information, source file information, and clone information.

### Version

Version name of the tool.

```
#version\t<version name>\n
```

### Options 　

Contents of "-d", "-l", "-o", "-t" and "-charset".

```
#option
<option name>\t<argument>\n
...
```

### rule file information

Paths to language and grammar files.

```
#rule_constructor\n
<language name>{\n
\tcomment_file\t<comment rule file path>\n
\treserved_file\t<path of reserved word's list file>\n
}\n
...
```

### Source file information

Information about the target file (file number starts from 0).

```
#source_files\n
<file number>\t<line number>\t<token number>\t<file path>\n
...
```

### Clone information

Clone information is output as either #clone_pairs or #clone_sets.

#### pair

- A single clone pair is output as 3 lines
- A clone pair with the same clone ID is a clone set.
  (The clone set information is not lost if you choose to output format `pair`)
- If code fragmments A and B are a clone pair, then  
  The first line is the clone ID, the  
  The second line is the location of code fragment A (start and end)
  The third line is the location of code fragment B (start and end)

```
#clone_pairs
cloneID:<clone ID>\n
\t<file number>:<start line>,<start column>Space-Space<end line>,<end column>\n
\t<file number>:<start line>,<start column>Space-Space<end line>,<end column>\n
cloneID:<clone ID>\n
...
```

#### set

If code fragments A, B, and C are a clone set, then  
The first line is the clone ID,
The second line is the location of code fragment A (start and end)
The third line is the location of code fragment B (start and end)  
The fourth line is the location of code fragment C (start and end)

```
#clone_sets
cloneID:<clone ID>\n
\t<file number>:<start line>,<start column>Space-Space<end line>,<end column>\n
\t<file number>:<start line>,<start column>Space-Space<end line>,<end column>\n
\t<file number>:<start line>,<start column>Space-Space<end line>,<end column>\n
cloneID:<clone ID>\n
...
```

## Viewer Usage

### Gemini(CCFinder)

You can open the output files with Gemini.

1. Open the folder named "Icca" included in CCFSW.
2. Launch `Iccagui.jar`. Select (2) `analyze token-based...` in Gemini
3. Select `Analysis Result File` and launch the file.

### GemX(CCFinderX)

By adding the option `-ccfx`, the file will be output in the format used in CCFinderX.  
Files in CCFinderX format can be opened with GemX.  
CCFinderX can be downloaded from CCFinder's homepage ( http://www.ccfinder.net/ccfinderxos.html ).  
To run GemX, a "32-bit version" of the Java JDK is required.

1.  Run GemX with `gemx.bat`
2.  Select `File ->Open Clone Data...` from the toolbar.
3.  Select the ccfxd file output by CCFSW.
