package gluoncompiler.syntax;

import gluoncompiler.GluonOutput;
import gluoncompiler.Operator;
import gluoncompiler.Token;

/**
 * Factor := ['+'|'-'](<Variable>|<Literal>|'('<Boolean Expression>')')
 * 
 * TODO: add support for more unary operators
 * TODO: add level under for postfix operators
 */
class Factor extends SyntaxObject {
	Token first;
	boolean unaryMinus;
	boolean unaryPlus;
	BooleanExpression subExpression;
	SyntaxObject value;
	
	public Factor(Token test) {
		first = test;
	}

	@Override
	public Token parse() {
		Token test = first;
		
		// test for unary operators
		if (test.isOperator()){
			Operator testOp = test.getOperator();
			switch (testOp){
				case BRACKET_LEFT:
					// handle after
					break;
				case SUBTRACT:
					unaryMinus = true;
					test = test.getNext();
					break;
				case ADD:
					unaryPlus = true;
					test = test.getNext();
					break;
				default:
					throw new RuntimeException("Unknown operator: " + testOp);
			}
		}
		
		// test for 
		if (test.isOperator()){
			if (Operator.BRACKET_LEFT.equals(test.getOperator())){
				// We need to seperate the unary ops to their own level so that we can have -(10*2)
				subExpression = new BooleanExpression(test.getNext());
				test = subExpression.parse();
				assert(Operator.BRACKET_RIGHT.equals(test.getOperator()));
				return test.getNext();
			}
			throw new RuntimeException("Unknown operator: " + test.getOperator());
		} else if (test.isLiteral()){
			value = new LiteralNumber(test);
		} else if (test.isIdentifier()){
			value = new Variable(test);	
		} else {
			throw new RuntimeException("Unknown token: " + test);
		}
		
		test = value.parse();
		return test;
	}

	@Override
	public String emitCode() {
		StringBuilder sb = new StringBuilder();
		if (subExpression != null){
			sb.append(subExpression.emitCode());
		} else {
			sb.append(value.emitCode());
		}
		
		if (unaryMinus){
			sb.append(GluonOutput.codeLine("NEG EAX"));
		} else if (unaryPlus){
			// do nothing
		}
		return sb.toString();
	}

	@Override
	public void print(int level) {
		if (subExpression != null){
			subExpression.print(level);
		} else {
			if (unaryMinus){
				printLevel(level);
				printLn("-");
			} else if (unaryPlus){
				printLevel(level);
				printLn("+");
			}
			value.print(level);
		}
	}
	
}
