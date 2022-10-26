##Rules for Defining Rules

#integral expression
an integral expression is an arithmetic expression as per java conventions, where every operand is assumed to be integral.
* evaluates to an integral value at run time
* may contain integral constants and fields as operands
* fields are not declared, but used with implicit declaration to hold integral values (java long values to be precise)
* the scope of a field could be local(to a rule) or global (across rules within a rules context for a given run)
* field names follow java naming rules. good naming convention is a must, but it is not enforced or validated.
* local field names must start with an underscore. while this is actually not a syntactical requirement, it is enforced to avoid unintended confusions between global and local fields
*  _interestRate, _interest_rate, -InterestRate are valid local names but interestRate and interest-rate are not
* interestRate, interest_rate, InterestRate are all valid global names but _interestRate and interest-rate are not

#logical expression
a logical expression follows the java definition of an expression except that all its operands are integral.
* there is no notion of a boolean. instead integral value is used as boolean based on the context.
* an integral values is considered true if it is non-zero, and is false if it is zero.
* -1, 2, 3463 are all true, 0 is false and !0 is true !23 is false 
* logical expression evaluates to 0 or 1
* an expression is not inherently logical or integral. it is based on the context.

#rule step
a rule step is a calculation step within a  rule. its purpose is to calculate value for a field. 

a rule step:
* is meant calculate value for a local field, or for the global field (of the rule that the step is part of).
* has an integral expression to be evaluated at run time to get the value
* has an optional logical expression as condition. calculation is carried out if condition is missing, or it evaluates to true(nn 0)
* we refer to a step with a logical expression as conditional-step. otherwise it as an unconditional-step

#rule 
the purpose of a rule is to calculate value for a global field with a small list of calculation steps. when we say small we mean few steps, may be tens, but certainly not hundreds of steps. modular design principles are to be used to achieve this.
* is identified by its unique global field name
* consists of a list of (ordered) calculation steps. has at least one step
* may use local fields to modularize and simplify expressions 
* every local field has one or more steps associated with it
* all steps associated with a local field are to be grouped together.
* the last step for a group must be unconditional-step for that local field. This is to ensure that we certainly do have a value for that 
* any number of conditional-steps may be defined for the global field. However, they must appear outside of the groups. that is, they appear in between two groups. they may also appear as the first few steps, before the first group for a local field.
* last step must be an unconditional-step for the global field
When a rule is invoked, its steps are executed in the order:
* execution starts with the first step
* if the step is associated with a local field, and the value for that local field is already known, then the step is skipped
* that is, whenever a value for a local field is clculated, any more steps in that group are skipped.
* it is important to note that the value for a field is never "changed". once it is set, it remains at that value. this paradigm of design/thinking/logic is non-intuitive to a typical programmer. in typical programming, it is quite common to start a field with a value,and keep changing its value based on logic. a different paradigm of logic is to be used in the rules here
* a step is also skipped if it has a condition and the condition evaluates to true
* for the evaluation of an expression, global field values may be required. if the value for such a field is not readily available, the rule associated with that global field is invoked to set value for it
* execution stops whenever a value is assigned to the designated global field  
#Rules Context
Purpose of a rules context is to calculate one or more output global fields, based on a set of input fields. A rules context
* has set of rules, each associated with a global field.
* has set of meta-fields that are nothing but global fields whose values are pre-defined at design stage. this is a commonly used technique to externalize constants from rules, their by improving their readability and maintainability.
* has set of input fields that are noting but global fields whose values are provided as input for a run at run time.
To calculate the value of a global field:
* the rule associated with that global field is invoked
* the expressions in this rule require values for other global fields
* whenever an expression requires value for a global field, if the value is not yet available, the rule associated with that global field is invoked in a recursive way
* it is a symantic error if such a recursive call results in an infinite loop. like a requires b and b requires a. run time engine detects such an error and results in an error (exception) situation
* if more than one such global fields are to be calculated with a given input set, then they are invoked one after the other, in the given sequence. global field values are retained across such invocations.
 