# TroubleShooting

## Console output at runtime contains "LOC = 0"

- This is often due to a misconfiguration of the extension.
  - Please refer to the comment file description in [OptionFile.md](/Usage/OptionFile.md).

## The contents of the Comment file are not reflected

- Please check if the file name of the comment file is correct.
  - Refer to [OptionFile.md](/Usage/OptionFile.md) to check if the syntax is correct.
- Change the character encoding of the Comment file to UTF-8 without BOM.

## Error (exception) such as file does not exist

- Make sure that the optional arguments select the correct directory.
- Make sure that the file separator for the optional argument is the one used in your environment.
- If there are extra file separators at the end of the optional arguments, remove them.
