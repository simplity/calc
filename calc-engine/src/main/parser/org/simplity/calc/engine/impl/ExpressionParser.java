package org.simplity.calc.engine.impl;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.simplity.calc.engine.impl.IExpression;
import org.simplity.calc.grammar.CalcLexer;
import org.simplity.calc.grammar.CalcParser;

/**
 * A facade class that encapsulates the entire ANTLR parsing pipeline. It
 * converts a raw expression string into our custom IExpression AST.
 */
class ExpressionParser implements IExpressionParser {
	private final IParserContext ctx;
	private final AstBuilder astBuilder;

	ExpressionParser(IParserContext ctx) {
		this.ctx = ctx;
		this.astBuilder = new AstBuilder(ctx);
	}

	@Override
	public IExpression parse(String expressionString, String variableName) {
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

			return expression;
		} catch (RecognitionException e) {
			this.ctx.logError(variableName, "rule", "Expression '" + expressionString + "' is not a valid expression");
			return null;
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

}