package gluoncompiler.syntax;

import gluoncompiler.GluonVariable;
import gluoncompiler.Token;

/**
 * Parse a variable define with optional assignment
 */
class DefineVariable extends Statement {
	
	Variable variable;
	AssignmentExpression assignExp;
	
	public DefineVariable(Token next, ScopeObject scope) {
		super(next, scope);
	}

	@Override
	public Token parse() {
		Token var = first.getNext();
		
		if (!var.isIdentifier())
			throw new RuntimeException("Expected identifier, found: " + var);

		variable = new Variable(var, scope);
		Token test = variable.parse();
		
		if (test.isOperator()){
			assignExp = new AssignmentExpression(var, scope);
			test = assignExp.parse();
		}
		return test;
	}

	@Override
	public void emitCode(StringBuilder code) {
		GluonVariable.registerVariable(variable.getName());
		if (assignExp != null) {
			assignExp.emitCode(code);
		}
	}
	
	@Override
	public void print(int level) {
		printLevel(level);
		printLn("DEFINE");
		if (assignExp == null)
			variable.print(level + 1);
		else
			assignExp.print(level + 1);
	}
}
