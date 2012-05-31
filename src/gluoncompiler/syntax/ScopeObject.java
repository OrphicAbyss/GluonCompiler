package gluoncompiler.syntax;

import java.util.ArrayList;

/**
 * Scope object holds registered variables for the current scope. Variables not
 * found in the current scope are checked against the parent scope.
 */
public class ScopeObject {
	
	ArrayList<String> variables;
	ScopeObject parent;
	
	public ScopeObject() {
		variables = new ArrayList<>();
		parent = null;
	}
	
	public ScopeObject(ScopeObject parent) {
		variables = new ArrayList<>();
		this.parent = parent;
	}
	
	public void registerVariable(String name) {
		if (variables.contains(name))
			throw new RuntimeException("Variable already registered in this scope.");
		
		variables.add(name);
	}
	
	public void testVariableRegistered(String name){
		if (variables.contains(name))
			return;
		
		if (parent != null)
			parent.testVariableRegistered(name);
		else
			throw new RuntimeException("Variable not available in this or it's parent scopes.");
	}
	
	public String getVariableLabel() {
		return "";
	}
}
