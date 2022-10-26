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
 * models an operand of this expression
 *
 * @author simplity.org
 */
public class Operand {
	/**
	 * operand not yet built
	 */
	public static final int NONE = 0;
	/**
	 * operand is a constant
	 */
	public static final int CONSTANT = 1;
	/**
	 * operand is a field. value attribute has the name of the field.
	 */
	public static final int FIELD = 2;
	/**
	 * operand is a function. value attribute is the name of the function, and
	 * expression attribute is the argument list
	 */
	public static final int FUNCTION = 3;
	/**
	 * operand is a sub-expression, represented by expression attribute
	 */
	public static final int EXPRESSION = 4;

	int operandType = Operand.NONE;

	/**
	 * unary operator is stored only if it is not a constant
	 */
	UnaryOperator uop;
	/**
	 * unary operator is stored only if it is not a constant
	 */
	BinaryOperator bop;
	/**
	 * value is the value of the constant. otherwise name of the field/function.
	 */
	String value;
	/** in case this is an expression */
	Expression expression;

	/**
	 * invalid if ~ ? are used on a non-field
	 *
	 * @return
	 */
	boolean isValid() {
		if (this.uop == null) {
			return true;
		}

		if (this.operandType == Operand.FIELD) {
			return true;
		}

		if (this.uop == UnaryOperator.IsKnown || this.uop == UnaryOperator.IsUnknown) {
			return false;
		}

		if (this.operandType != Operand.CONSTANT) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "unaryOperator: " + this.uop + " value:" + this.value;
	}

	/**
	 *
	 * @return type of operand. use constants to check the type
	 */
	public int getOperandType() {
		return this.operandType;
	}

	/**
	 *
	 * @return value attribute. may represent constant value, field name or
	 *         function name depending on th eoperand type
	 */
	public String getOperandValue() {
		return this.value;
	}

	/**
	 *
	 * @return expression attribute. non-null for a sub-expression and function
	 *         (in which case this is the argument list)
	 */
	public Expression getOperandExpression() {
		return this.expression;
	}

	/**
	 * @return unary operator associated with this opeand
	 */
	public UnaryOperator getUnaryOperator() {
		return this.uop;
	}

	/**
	 * @return binary operator associated with this opeand
	 */
	public BinaryOperator getBinaryOperator() {
		return this.bop;
	}
}
