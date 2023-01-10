# CCFinderSW

CCFinderSW (CCFSW) is a token-based code clone detection tool.
[Japanese Readme](READMEJP.md) is also available.

## What is a code clone?

A code clone is a matching or similar piece of code that exists in source code.  
The tool detects and outputs clone pairs, which are pieces of code that are similar to each other.  
The tool detects and outputs clone pairs that are similar to each other, and excludes clone pairs that contain inserted or deleted sentences.

## Latest version

[CCFinderSW-1.0.zip](build/distributions/CCFinderSW-1.0.zip)

## How to use

There are several documents.

- About execution
  - [Run.md](Usage/Run.md)
- About reserved word files and comment files
  - [OptionFile.md](Usage/OptionFile.md)
- About Available Viewers
  - [OutputAndViewer.md](Usage/OutputAndViewer.md)
- Possible troubles shooting
  - [TroubleShooting.md](Usage/TroubleShooting.md)

## Required Environment

- Java Runtime Environment( >=8 )  
  Currently, this program is implemented in Java only.
- We are planning to reduce the amount of RAM as much as possible.

## Test Environment

some quick tests.

- CCFinderSW
  - Windows 10 Pro
  - Max OS
  - (probably) Linux
- Icca
  - Windows 10 Pro

## Former Versions

- [CCFinder](http://sel.ist.osaka-u.ac.jp/cdtools/ccfinder-e.html)
- [CCFinderX](http://www.ccfinder.net/ccfinderxos.html)

## Copyright and Licensing

See LICENSE.  
This software contains libraries distributed under Apache Licence 2.0.

## Other Information

Author: Yuichi Semura
Affiliation: Graduate School of Information Science and Technology, Osaka University, Osaka, Japan

## Paper Information

The detailed information is described in our paper. The following is our paper information.

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

## Notes on use

CCFSW performs lexical analysis, but does not have separate lexical analysis mechanisms for each language.  
CCFSW performs lexical analysis tailored to each language by using optional files.  
(At the same time, this does not mean that the lexer algorithm is optimized for each language.)  
This option file can be modified by the user.  
Refer [OptionFile.md](Usage/OptionFile.md) for the notation of the option file.
