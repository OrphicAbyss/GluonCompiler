package gluoncompiler.syntax;

import gluoncompiler.GluonVariable;
import gluoncompiler.Operator;
import gluoncompiler.Token;

/**
 * Parse a variable define with optional assignment
 */
class DefineVariable extends Statement {
	
	Variable variable;
	AssignmentExpression assignExp;
	
	public DefineVariable(Token next) {
		super(next);
	}

	@Override
	public Token parse() {
		Token var = first.getNext();
		assert(var.isIdentifier());
		variable = new Variable(var);
		
		Token test = var.getNext();
		if (test.isOperator()){
			assignExp = new AssignmentExpression(var);
			test = assignExp.parse();
		}
		return test;
	}

	@Override
	public String emitCode() {
		GluonVariable.registerVariable(variable.getName());
		if (assignExp != null) {
			return assignExp.emitCode();
		}
		return "";
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
