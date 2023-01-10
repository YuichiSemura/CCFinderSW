# Run

- Java 8 or later is recommended for execution. (Not tested with lower versions.)
- Run on the command line with the appropriate arguments.
  - `./CCFinderSW D -d directory -l language -o outputtxt`
- The clone pair information output by CCFSW can be read by Gemini or CCFX.

## [D] CloneDetector command line arguments

- -help
  - A reasonable amount of help will be given.

### Required Arguments

- `-d <arg>`
  - Specify the directory path where the source files are located.
- `-l <arg>`
  - Specify the language name for code clone detection.
  - This language name is used when loading the options file.
- `-o <arg>`
  - Specify the output file name which contains clone pair information.
    - e.g., Entering `-o outputfile` will create _outputfile.txt_.
  - If you are in ccfx mode, _outputfile.ccfxd_ will be created.
  - If you are in ccfsw mode, _outputfile_ccfsw.txt_ is also created.

### Optional Arguments

#### lexical analysis and detection

- `-t <arg>`
  - Specify the minimum number of tokens (threshold) of code clones to be detected.  
    Specify a number, e.g. `-t 90`.
  - The default value is 50. If no argument is given, the default value is used.
- `-w <arg>`
  - Sets the range of clone pairs (0 <= detectionRange <= 2)
  - 0 All range
  - 1 within files only
  - 2 Between files only
- `-b`
  - Recognizes code blocks with blank indentation.
  - Cannot be used in antlr mode.
- `-antlr <arg>`
  - Extracts information used for comment removal and identifier identification from the grammar files in `src/main/dist/grammarsv4`.
  - Normal rules cannot be used.
  - The argument must be a regular expression of the extension name to recognize the target source code. (just put the extensions in a row with a vertical bar between).
    - Example1: `-antlr h|hh|hpp|hxx|c|cc|cpp|cxx`
    - Example2: `-antlr py`
- `-charset <arg>`
  - The character encoding of the target file can be specified.
  - The analysis of source code that uses double-byte characters requires correct character encoding recognition.
  - There are four types of character encoding: sjis, utf8, euc, and auto. Spcify encoding such as `-charset sjis`.  
    _sjis_ recognizes all files with the character encoding "Shift-JIS".  
    _utf8_ Recognizes all files with the character code "UTF-8".  
    _euc_ recognizes all files with the character encoding "EUC-JP".  
    _auto_ recognizes each file automatically from the above three character codes. It takes a little time.
- `-g <arg>`
  - Detect target source codes by dividing them into N groups.
  - (This was a mechanism to enable detection even if the memory size is small, but it is not available now.)
- `-nolx`
  - Omit the lexical analysis part. It can be used to repeat execution once.

#### Output

Output is available in four formats and can be in more than one format at the same time.  
If none of the following options are included, the program assumes that -ccf is included.

- `-ccf`
  - Outputs the file in a format that can be opened by Gemini (CCFinder).
  - The file name is `outputFile.txt`.
- `-ccfx`
  - Creates a ccfxd file in the GemX (CCFinderX) output format.
  - The file name is `outputFile.ccfxd`.
- `-ccfsw <arg>`
  - Outputs the file in CCFSW output format.
  - The file name is `outputFile_ccfsw.txt`.
  - Specify `pair` or `set` for \<arg>. (The output will be different.)
- `-json <arg>`
  - Outputs the data in JSON format.
  - The file name is `outputFile.json`.
  - Specify `+` or `-` for \<arg>. (This means whether the JSON is indented or not)

#### Metrics Filtering

- `-tks <arg>`
  - Filter by the minimum number of token types in the clone fragments.
  - Specify by integer value.
  - 12 or more is recommended.
- `-rnr <arg>`
  - Filters by the minimum rate of non-repeating parts in the clone fragments.
  - Specify a decimal number between 0 and 1.
  - A value of 0.3 is probably sufficient.

### Running without arguments or arguments with errors

- Running without arguments or with errors will output usage.

## About the target source code

- Put all the target source code to be detected in a directory, and set the path of the directory with the aforementioned arguments (-d <arg>).
- When using CCFX mode, a folder named ".ccfxprep" is created in the directory. This is necessary when using CCFX Viewer.

## Preparation and conditions for execution

- A comment rule file and a reserved word's list file for the language are required for execution.

  - Comment rule file
    - **A minimum description is required.**
    - Details are described in [OptionFile.md](/Usage/OptionFile.md).
  - Reserved word's list file
    - It is not required, but recommended.
    - If the file exists, it detects type 1 and 2 code clones; otherwise, it detects type 1 code clones.

- File placement
  - Comment rule file and Reserved word's list file should be placed in the `src/main/dist/comment` directory and the `src/main/dist/reserved` directory, respectively.

## [P] PrittyPrinter command line arguments

Not implemented.

## [S] MetricsFiltering command line arguments

Not implemented.
