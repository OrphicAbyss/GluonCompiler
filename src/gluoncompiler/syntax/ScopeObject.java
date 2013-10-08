package gluoncompiler.syntax;

import gluoncompiler.GluonOutput;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Scope object holds registered variables for the current scope. Variables not
 * found in the current scope are checked against the parent scope.
 */
public class ScopeObject {
	
	ArrayList<ScopeObject> children;
	ArrayList<Variable> variables;
	ScopeObject parent;
	boolean mainScope;
	boolean functionScope;
	
	public ScopeObject() {
		variables = new ArrayList<>();
		children = new ArrayList<>();
		parent = null;
		mainScope = true;
		functionScope = false;
	}
	
	public ScopeObject(ScopeObject parent, boolean isFunction) {
		variables = new ArrayList<>();
		children = new ArrayList<>();
		this.parent = parent;
		mainScope = false;
		functionScope = isFunction;
		parent.addChild(this);
	}
	
	public void addChild(ScopeObject child) {
		children.add(child);
	}
	
	public void registerVariable(Variable var) {
		if (variables.contains(var))
			throw new RuntimeException("Variable already registered in this scope.");
		
		variables.add(var);
	}
	
	public void testVariableRegistered(Variable var) {
		if (variables.contains(var))
			return;
		
		if (parent != null)
			parent.testVariableRegistered(var);
		else
			throw new RuntimeException("Variable not available in this or it's parent scopes. Variable: " + var);
	}
	
	public Collection<Variable> getVariables() {
		return variables;
	}
	
	public String getVariableLabel(String variableName) {
		return "var_" + variableName;
	}
	
	public void emitCreateScope(GluonOutput code) {
		int varCount = countOfLocalVars();
		code.comment("Start new scope");
		if (varCount == 0)
			return;

		code.comment("Variables: " + varCount);
		code.code("SUB ESP, " + varCount * 4);

		ArrayList<Variable> varList = new ArrayList<>(varCount);
		getVariables(varList);
		
		int count = 0;
		for (Variable var: varList) {
			if (!var.isParameter())
				code.code(var.getLabel() + " EQU EBP" + (-4 * ++count) +"");
		}
	}
	
	public void emitDistroyScope(GluonOutput code) {
		code.comment("End scope");
		int varCount = countOfLocalVars();
		
		
		if (varCount == 0)
			return;
		
		ArrayList<Variable> varList = new ArrayList<>(varCount);
		getVariables(varList);
		
		buildRestore(code, varList, false);
		
		code.code("ADD ESP, " + varCount * 4);
	}
	
	public void buildRestore(GluonOutput code, ArrayList<Variable> vars, boolean parameters) {
		if (!vars.isEmpty()) {
			boolean first = true;
			for (Variable var: vars){
				if (parameters == var.isParameter()) {
					if (!first)
						code.append(", ");
					else {
						code.append("\tRESTORE ");
						first = false;
					}
					code.append(var.getLabel());
				}
			}
			code.append("\n");
		}
	}
	
	private int countOfLocalVars() {
		int count = 0;
		for (Variable var: variables)
			if (!var.isParameter())
				count++;
				
		for (ScopeObject child: children) {
			if (!child.isFunctionScope())
				count += child.countOfLocalVars();
		}
		
		return count;
	}
	
	private void getVariables(ArrayList<Variable> list){
		list.addAll(variables);
		
		for (ScopeObject child: children) {
			if (!child.isFunctionScope())
				child.getVariables(list);
		}
	}
	
	public boolean isFunctionScope() {
		return functionScope;
	}
}
