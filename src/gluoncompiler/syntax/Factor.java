package gluoncompiler.syntax;

import gluoncompiler.Operator;
import gluoncompiler.Token;

/**
 * Factor := '(' <Boolean Expression> ')' 
 *		  OR ['+'|'-'](<Variable>|<Literal>)
 */
class Factor extends SyntaxObject {
	Token first;
	boolean unaryMinus;
	boolean unaryPlus;
	BooleanExpression subExpression;
	LiteralNumber constant;
	Variable variable;
	
	public Factor(Token test) {
		first = test;
	}

	@Override
	public Token parse() {
		Token test = first;
		
		if (test.isOperator()){
			Operator testOp = test.getOperator();
			switch (testOp){
				case BRACKET_LEFT:
					// We need to seperate the unary ops to their own level so that we can have -(10*2)
					subExpression = new BooleanExpression(test.getNext());
					test = subExpression.parse();
					assert(Operator.BRACKET_RIGHT.equals(test.getOperator()));
					return test.getNext();
				case SUBTRACT:
					unaryMinus = true;
					break;
				case ADD:
					unaryPlus = true;
					break;
				default:
					assert(false);
					break;
			}
			test = test.getNext();
		}
		
		if (test.isLiteral()){
			constant = new LiteralNumber(test);
			test = constant.parse();
		} else if (test.isIdentifier()){
			variable = new Variable(test);
			test = variable.parse();
		} else {
			assert(false);
		}
		
		return test;
	}

	@Override
	public String emitCode() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
