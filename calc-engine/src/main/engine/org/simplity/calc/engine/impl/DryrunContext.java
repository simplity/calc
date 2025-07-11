package org.simplity.calc.engine.impl;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simplity.calc.engine.api.CalcErrorDS;

/**
 * Implements IDryrunContext to detect circular dependencies ("catch-22s").
 */
class DryrunContext {
	private final Map<String, IVariable> variables;
	private final List<CalcErrorDS> errors;

	private final Set<String> clearedOnes = new HashSet<>();
	private final Set<String> faildOens = new HashSet<>();
	// Stack to track the current dependency path
	private final LinkedHashSet<String> beingEvaluated = new LinkedHashSet<>();

	/**
	 * Initializes the dry-run context.
	 */
	protected DryrunContext(Map<String, IVariable> variables, List<CalcErrorDS> errors) {
		this.variables = variables;
		this.errors = errors;
		// Pre-populate the cache with required inputs, as they are always evaluatable.
		for (IVariable variable : variables.values()) {
			if (variable.isRequiredInput()) {
				this.clearedOnes.add(variable.getName());
			}
		}
	}

	public boolean isEvaluatable(String variableName) {
		if (this.clearedOnes.contains(variableName)) {
			return true;
		}

		if (this.faildOens.contains(variableName)) {
			return false;
		}

		// This is the core cycle detection logic.
		if (this.beingEvaluated.contains(variableName)) {
			this.reportError(variableName);
			this.faildOens.addAll(this.beingEvaluated);
			this.beingEvaluated.clear();
			return false;
		}

		IVariable variable = this.variables.get(variableName);
		// A variable can be null if it's an optional input that is not provided.
		if (variable == null) {
			this.faildOens.add(variableName);
			return false;
		}

		this.beingEvaluated.add(variableName);
		boolean isOk = variable.dryrun(this);
		// IMPORTANT: remove from path whether it succeeds or fails.
		this.beingEvaluated.remove(variableName);

		if (isOk) {
			this.clearedOnes.add(variableName);
		} else {
			this.faildOens.add(variableName);
		}
		return isOk;
	}

	/**
	 * Formats and logs a circular dependency error message.
	 */
	private void reportError(String finalLink) {
		StringBuilder sb = new StringBuilder("Circular dependency detected: ");
		for (String s : this.beingEvaluated) {
			sb.append(s).append(" -> ");
		}
		sb.append(finalLink);
		this.errors.add(new CalcErrorDS("dataElement:" + finalLink, sb.toString()));
	}
}
