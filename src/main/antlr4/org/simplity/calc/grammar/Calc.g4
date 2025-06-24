grammar Calc;


parse: expr EOF;

expr
    : <assoc=right> op=(SUB|NOT) expr   #UnaryExpr
    | expr op=(MUL|DIV|MOD) expr        #InfixExpr
    | expr op=(ADD|SUB) expr            #InfixExpr
    | expr op=(GT|LT|GTE|LTE) expr      #InfixExpr
    | expr op=(EQ|NEQ) expr             #InfixExpr
    | expr op=AND expr                  #InfixExpr
    | expr op=OR expr                   #InfixExpr
    | atom                              #AtomExpr
    ;

atom
    : ID '(' (expr (',' expr)*)? ')'    #FuncExpr
    | ID                                #VariableExpr
    | literal                           #LiteralExpr
    | '(' expr ')'                      #ParenExpr
    ;

literal
    : NUMBER
    | STRING
    | BOOLEAN
    ;

// Lexer Rules (Tokens)
BOOLEAN: 'true' | 'false';
ID:      [a-zA-Z_] [a-zA-Z_0-9]*;
NUMBER:  [0-9]+ ('.' [0-9]+)?;
STRING:  '\'' ( ('\'\'') | ~('\'') )* '\'';

// Operator Tokens
MUL: '*';
DIV: '/';
MOD: '%';
ADD: '+';
SUB: '-';
NOT: '!';
GT:  '>';
LT:  '<';
GTE: '>=';
LTE: '<=';
EQ:  '=';
NEQ: '!=';
AND: '&';
OR:  '|';

WS:      [ \t\r\n]+ -> skip;