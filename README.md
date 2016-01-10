# go2lang
Master: [![Build Status](https://travis-ci.org/FritzFlorian/go2lang.svg?branch=master)](https://travis-ci.com/FritzFlorian/go2lang)   Dev: [![Build Status](https://travis-ci.org/FritzFlorian/go2lang.svg?branch=dev)](https://travis-ci.com/FritzFlorian/go2lang)

go2lang is a modern JVM based language that handles projects with scope.

## Syntax Examples

The language in in an very early stage. Right now it only supports the most fundamental syntax.
This includes the following statements:
* scope ... end
* if ... end
* label labelName
* label start
* go to labelName
* run to labelName
* int varName = number
* varName = expression

Here is a quick code sample:
```batch
label start
int x = 1
int max = 99

label loop

scope
    int bottles = 0
    bottles = x
end

if x < max
    x = x + 1
    go to loop
end
```

Right now no dynamic memory allocation is possible.