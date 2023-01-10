# About Option File

## Preparation for execution

- Comment rule file and reserved word's list file for the target language are required for execution.
  - Comment rule file
    - **A minimum description is required.**
  - Reserved word's list file
    - It is not required, but recommended.
    - If the file exists, it detects type 1 and 2 code clones; otherwise, it detects type 1 code clones.
- Location of the file
  - Comment rule file must be located in `src/main/dist/comment`. Reserved word's list file must be located in `src/main/dist/reserved`.

## About Comment rule file

- File name
  - The file name must be the same as the argument `language`.
    - e.g., If "c" is given as the argument `language`, a file named `c_comment.txt` is required.
- File extension setting
  - You can set the file extension of the target files of clone detection. This setting is required.
  - If the tool outputs "LOC = 0" in the console at runtime, it is often due to a mistake in setting the file extension.
- Comment rule
  - Given the comment rule adopted by the target language, Code clone detection can be performed ignoring comments.
  - Detailed file notation is described later.

## About Reserved word's list file

- Reserved words are keywords that cannot be used in variable names.
- The file name must correspond to the input of the argument language.
  - e.g., If "c" is given as the argument `language`, a file named `c_reserved.txt` is required.
- If this file does not exist, only type 1 code clones are detected.
- One reserved word should be listed per line. The following is an example.

```
if
while
class
...
```

- When the file exists but nothing is written, type 2 code clones are detected without reserved words.

## Comment rule file notation

This section describes specific examples of comment rule file notation.

### File extension setting

- You can set the file extension of the target files of clone detection.
- **Only this option is required**.
- In the case of C language, files with the extensions "c" and "h" are considered as target files for detection by specifying the following in the file.

```
#extension
c
#extension
h
```

### Comment rule

Given the comment rule adopted by the target language, Code clone detection can be performed ignoring comments.

#### Line comment

- This rule treats any code after symbol `A` as a comment.
- Notation: _#start + line break + start symbol_.

```
#start
//
```

#### Multi-line comment

- This rule treats any code between symbol `A` and symbol `B` as a comment.
- Notation: _#startend + line break + start sign + line break + end sign_.
- Notation (with nesting): _#startendnest + line break + start symbol + line break + end symbol_.

```
#startend
/*
*/
```

#### Whole line comment

- This rule treats entire line after symbol `A` at the beginning of a line as a comment.
- Notation: _#linestart + line break + start symbol_.

```
#linestart
*
```

#### Multi-line whole comment

- This rule treats entire lines between symbol `A` at the beginning of a line and symbol `B` at the beginning of a line as a comment.
- Notation: _#linestartend + line break + start symbol + line break + end symbol_.

```
#linestartend
=begin
=end
```

#### String literal

```
#prior
"
"
```

or

```
#literal
"
"
```

#### Verbatim string literals

- The rules are the same as for verbatim string literals used in C# and other languages.
- The rules for line breaks and escape sequences are different.

```
#literalverbatim
@"
"
```

#### line continuation character

- This setting is used to substitute other characters for the equivalent function of the line continuation character "\" in the C language. Only one character can be set.

```
#linecontinue
\
```

#### Setting the characters that can be used in variable names

- You can set the characters that can be used for variable names. Enter a regular expression corresponding to the variable name.
- The default is "alphanumeric string + double-byte characters + underscore".  
  Example:
  Alphanumeric string only: [0-9a-zA-Z]+  
  alphanumeric string and double-byte characters: [0-9a-zA-Z\u3040-\u30FF 一-龠]+  
  alphanumeric string, double-byte characters and underscore (default setting): [0-9a-zA-Z\u3040-\u30FF 一-龠_]+

```
#variableregex
[0-9a-zA-Z]+
```
