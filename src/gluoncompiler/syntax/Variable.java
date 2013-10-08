package gluoncompiler.syntax;

import gluoncompiler.GluonLibrary;
import gluoncompiler.GluonOutput;
import gluoncompiler.Token;
import java.util.Objects;

/**
 * Variable syntax object
 */
public class Variable extends SyntaxObject {
	private String name;
	private Token token;
	private boolean parameter;
	
	public Variable(Token token, ScopeObject parentScope) {
		scope = parentScope;
		this.token = token;
	}
	
	public Variable(Token token, ScopeObject parentScope, boolean parameter) {
		scope = parentScope;
		this.token = token;
		this.parameter = parameter;
	}
	
	@Override
	public Token parse() {
		return parse(false);
	}
	
	public Token parse(boolean define) {
		if (!token.isIdentifier())
			throw new RuntimeException("Expected identifier, found: " + token);

		name = token.getValue();
		if (!define)
			scope.testVariableRegistered(this);
		else
			scope.registerVariable(this);
		
		return token.getNext();		
	}

	
	@Override
	public void emitCode(GluonOutput code) {
		code.code("MOV EAX, " + getLabelName());
	}

	@Override
	public void print(int level) {
		printLevel(level);
		printLn("VARIABLE " + name);
	}
	
	public String getName() {
		return name;
	}
	
	public String getLabel() {
		return GluonLibrary.varToLabel(name);
	}
	
	public String getLabelName() {
		return "dword [" + GluonLibrary.varToLabel(name) + "]";
	}
	
	public boolean isParameter() {
		return parameter;
	}
	
	@Override
	public boolean equals(Object other) {
		// If we are the same object
		if (super.equals(other))
			return true;
		
		// Or our names match
		if (other instanceof Variable) {
			return ((Variable)other).getName().equals(name);
		}
		
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 67 * hash + Objects.hashCode(name);
		return hash;
	}
}
