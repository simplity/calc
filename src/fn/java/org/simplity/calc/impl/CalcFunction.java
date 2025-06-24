package org.simplity.calc.impl;

import org.simplity.calc.api.ICalcContext;
import org.simplity.calc.api.ICalcFunction;
import org.simplity.calc.api.IValue;
import org.simplity.calc.api.ValueType;

/**
 * Factory that
 */
class CalcFunction implements ICalcFunction {

	private CalcFunction() {

	}

	@Override
	public IValue call(IValue[] args, ICalcContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueType getReturnType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueType[] getParameterTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean lastOneIsVararg() {
		// TODO Auto-generated method stub
		return false;
	}

}
