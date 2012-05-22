package gluoncompiler.syntax;

import gluoncompiler.Operator;
import gluoncompiler.Token;

/**
 * Boolean expression := <Expression> [ ( == | != | < | <= | > | >= ) <Expression> ]
 */
class BooleanExpression extends SyntaxObject {
	private Token first;
	private Expression exp1;
	private Expression exp2;
	private Operator compare;
	
	public BooleanExpression(Token next) {
		first = next;
	}

	@Override
	public Token parse() {
		Token test = first;
		exp1 = new Expression(test);
		test = exp1.parse();
		
		if (test.isOperator()){
			Operator testOp = test.getOperator();
			if (Operator.EQUALS.equals(testOp)
				|| Operator.NOT_EQUALS.equals(testOp)
				|| Operator.GREATER_THAN.equals(testOp)
				|| Operator.GREATER_THAN_OR_EQUALS.equals(testOp)
				|| Operator.LESS_THAN.equals(testOp)
				|| Operator.LESS_THAN_OR_EQUALS.equals(testOp)) {
				compare = testOp;
				test = test.getNext();
				exp2 = new Expression(test);
				test = exp2.parse();
			}
		}
		return test;
	}

	@Override
	public String emitCode() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
