
# Lexer Generator

## (work in progress, only a regex engine so far)

### What currently works

Currently working on a lexer / scanner generator similar to [Flex](https://www.cs.princeton.edu/~appel/modern/c/software/flex/flex.html).
So far, I only have gotten the regex engine to work but different scanner states / actual tokenization may follow in the future.

You can use it like this:
```
RegexEngine regexEngine = new RegexEngine("a | b*");
// RegexEngine regexEngine = new RegexEngine("0 | (1 | 2)(0 | 1 | 2)*");
// RegexEngine regexEngine = new RegexEngine("c?a? | bb");
// egexEngine regexEngine = new RegexEngine("(a | ε)(b | ε)");

System.out.println(regexEngine.isAccepted("bbbbbbb"));
```
NOTE: the + operator is not supported. Did not want to implement all the [syntactic sugar](https://en.wikipedia.org/wiki/Syntactic_sugar) that regex offers. For ```a+```, just use ```aa*```

### How it works

- the regex specification (string) is parsed using a small recursive descent parser
  (nothing has to be lexed since regex works on the character-level). The outcome is an abstract syntax tree (AST).

- a nondeterministic finite automaton (NFA) is generated from AST of the regex.
  The construction is given by [Glushkov's construction algorithm](https://en.wikipedia.org/wiki/Glushkov%27s_construction_algorithm). This is also known as the Berry-Sethi method.

- the NFA is simulated by an "implicit" [power set construction](https://en.wikipedia.org/wiki/Powerset_construction). We only "materialize" the states of the NFA
  that we reach. This saves memory (an equivalent DFA has up to 2^n states for a given NFA with n states) 
