/*
 * Copyright (c) 2015 EXILANT Technologies Private Limited (www.exilant.com)
 * Copyright (c) 2016 simplity.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.simplity.core.expr;

/**
 * models a binary operator that operates like operand1 Operator operand2.
 * Example a + b
 *
 * @author simplity.org
 */
public enum BinaryOperator {
	/** multiply */
	Multiply {
		@Override
		public String toString() {
			return "" + Chars.MULT;
		}
	},
	/** divide */
	Divide {
		@Override
		public String toString() {
			return "" + Chars.DIVIDE;
		}
	},
	/** Modulo /Remainder */
	Modulo {
		@Override
		public String toString() {
			return "" + Chars.MODULO;
		}
	},
	/** add */
	Plus {
		@Override
		public String toString() {
			return "" + Chars.PLUS;
		}
	},
	/** subtract */
	Minus {

		@Override
		public String toString() {
			return "" + Chars.MINUS;
		}
	},
	/** less than */
	Less {

		@Override
		public String toString() {
			return "" + Chars.LESS;
		}
	},
	/** less than or equal */
	LessOrEqual {
		@Override
		public String toString() {
			return BinaryOperator.LESS_THAN_OR_EQUAL;
		}
	},
	/** greater than */
	Greater {
		@Override
		public String toString() {
			return "" + Chars.GREATER;
		}
	},
	/** greater than or equal */
	GreaterOrEqual {

		@Override
		public String toString() {
			return BinaryOperator.GREATER_THAN_OR_EQUAL;
		}
	},
	/** equal */
	Equal {
		@Override
		public String toString() {
			return EQUAL;
		}
	},
	/** not equal */
	NotEqual {
		@Override
		public String toString() {
			return BinaryOperator.NOT_EQUAL;
		}
	},

	/** logical AND */
	And {
		@Override
		public String toString() {
			return AND;
		}
	},
	/** Logical Or */
	Or {
		@Override
		public String toString() {
			return OR;
		}
	},
	/**
	 * list (comma) is a pseudo operator to handle list of arguments for a
	 * function
	 */
	List {

		@Override
		public String toString() {
			return "" + Chars.LIST;
		}
	};
	/*
	 * string for operators that have more than one char
	 */
	private static final String NOT_EQUAL = "!=";
	private static final String LESS_THAN_OR_EQUAL = "<=";
	private static final String GREATER_THAN_OR_EQUAL = ">=";
	private static final String EQUAL = "==";
	private static final String AND = "&&";
	private static final String OR = "||";

	/**
	 * get an instance of the desired operator
	 *
	 * @param operator
	 * @return binary operator, or null if the input char has no operator
	 *         associated with that
	 */
	static BinaryOperator getOperator(char operator) {
		switch (operator) {
		case Chars.PLUS:
			return BinaryOperator.Plus;
		case Chars.MINUS:
			return BinaryOperator.Minus;
		case Chars.MULT:
			return BinaryOperator.Multiply;
		case Chars.DIVIDE:
			return BinaryOperator.Divide;
		case Chars.MODULO:
			return BinaryOperator.Modulo;
		case Chars.GREATER:
			return BinaryOperator.Greater;
		case Chars.GREATER_OR_EQUAL:
			return BinaryOperator.GreaterOrEqual;
		case Chars.LESS:
			return BinaryOperator.Less;
		case Chars.LESS_OR_EQUAL:
			return BinaryOperator.LessOrEqual;
		case Chars.EQUAL:
			return BinaryOperator.Equal;
		case Chars.NOT_EQUAL:
			return BinaryOperator.NotEqual;
		case Chars.AND:
			return BinaryOperator.And;
		case Chars.OR:
			return BinaryOperator.Or;
		case Chars.LIST:
			return BinaryOperator.List;
		default:
			return null;
		}
	}
}
