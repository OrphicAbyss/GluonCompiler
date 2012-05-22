package gluoncompiler.syntax;

import gluoncompiler.Operator;
import gluoncompiler.Token;

/**
 * Parse a variable define with optional assignment
 */
class DefineVariable extends Statement {
	
	String name;
	Operator assignType;
	SyntaxObject expression;
	
	public DefineVariable(Token next) {
		super(next);
	}

	@Override
	public Token parse() {
		Token var = first.getNext();
		assert(var.isIdentifier());
		name = var.getValue();
		
		Token test = var.getNext();
		if (test.isOperator()){
			Operator testOp = test.getOperator();
			if (Operator.ASSIGN.equals(testOp)
				|| Operator.ASSIGN_ADD.equals(testOp)
				|| Operator.ASSIGN_DIVIDE.equals(testOp)
				|| Operator.ASSIGN_MULTIPLY.equals(testOp)
				|| Operator.ASSIGN_SUBTRACT.equals(testOp)){
				assignType = testOp;
				test = test.getNext();
				expression = new BooleanExpression(test);
				test = expression.parse();
			}
		}
		return test;
	}

	@Override
	public String emitCode() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
