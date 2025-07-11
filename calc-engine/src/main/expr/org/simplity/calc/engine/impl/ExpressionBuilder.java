package org.simplity.calc.engine.impl;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.simplity.calc.engine.api.IValueType;
import org.simplity.calc.grammar.CalcLexer;
import org.simplity.calc.grammar.CalcParser;

/**
 * Builds an instance of IExpression from a string expression using the
 * ASTBuilder
 */
class ExpressionBuilder {
	private final IEngineBuilder ctx;
	private final AstBuilder astBuilder;

	ExpressionBuilder(IEngineBuilder ctx) {
		this.ctx = ctx;
		this.astBuilder = new AstBuilder(ctx);
	}

	/**
	 * parse a string-representation of an expression into its run-time-optimised
	 * instance of {@link IExpression}
	 *
	 * @param expressionString String representation of an expression
	 * @param variableName     variable name with which this expression is
	 *                         associated with. Used for logging errors if any
	 * @param expectedType     null if no pre-defined expectation. If this is
	 *                         specified, then the parsed expression is matched with
	 *                         this type, failing which an error is logged and a
	 *                         null expression is returned
	 * @return an instance of {@link IExpression} or null in case of any error. Any
	 *         error would have been logged into the context
	 */

	public IExpression parse(String expressionString, String variableName, IValueType expectedType) {
		if (expressionString == null || expressionString.isEmpty()) {
			this.ctx.logError("Expression must be specified", "expression", variableName);
			return null;
		}
		// 1. Create a CharStream from the input string.
		CharStream charStream = CharStreams.fromString(expressionString);

		// 2. Create a Lexer that feeds off the CharStream.
		CalcLexer lexer = new CalcLexer(charStream);

		// 3. Create a buffer of tokens drawn from the Lexer.
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);

		// 4. Create a Parser that feeds off the token buffer.
		CalcParser parser = new CalcParser(tokenStream);

		// 5. Begin parsing at the 'parse' rule (our entry point in the grammar).
		// This returns ANTLR's internal ParseTree.
		try {
			ParseTree parseTree = parser.parse();

			// 6. Walk the ParseTree with our visitor to build our IExpression tree.
			IExpression expression = this.astBuilder.build(parseTree, variableName);
			if (expression == null) {
				this.ctx.logError("Expression: '" + expressionString + "' is invalid", "expression", variableName);
				return null;
			}

			if (expectedType != null && expression.getValueType() != expectedType) {
				this.ctx.logError("Expression: '" + expressionString + "' returns a value of type "
						+ expression.getValueType().getDataTypeName().toLowerCase() + ". A "
						+ expectedType.getDataTypeName() + " is expected", "expression", variableName);
				return null;
			}
			return expression;
		} catch (RecognitionException | IllegalArgumentException e) {
			this.ctx.logError(variableName, "rule", "Expression '" + expressionString + "' is not a valid expression");
			return null;
		}
	}

}