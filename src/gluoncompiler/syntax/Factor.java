package gluoncompiler.syntax;

import gluoncompiler.GluonLibrary;
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
	boolean increment;
	boolean decrement;
	BooleanExpression subExpression;
	PostFix value;
	
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
				case INCREMENT:
					increment = true;
					test = test.getNext();
					break;
				case DECREMENT:
					decrement = true;
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
		} else if (test.isLiteral()) {
			if (increment || decrement)
				throw new RuntimeException("Increment/decrement operators can't operate on literals. Found: " + test.toString());
			
			value = new PostFix(test);
			return value.parse();
		} else if (test.isIdentifier()){
			value = new PostFix(test);
			return value.parse();
		} else {
			throw new RuntimeException("Unknown token: " + test);
		}
	}

	@Override
	public String emitCode() {
		StringBuilder sb = new StringBuilder();
		if (subExpression != null){
			sb.append(subExpression.emitCode());
		} else {
			if (increment)
				sb.append(GluonOutput.codeLine("INC [" + GluonLibrary.varToLabel(value.getVariable().getName()) + "]"));
			else if (decrement)
				sb.append(GluonOutput.codeLine("DEC [" + GluonLibrary.varToLabel(value.getVariable().getName()) + "]"));
			
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
		if (subExpression != null) {
			subExpression.print(level);
		} else {
			if (unaryMinus) {
				printLevel(level);
				printLn("-");
			} else if (unaryPlus) {
				printLevel(level);
				printLn("+");
			} else if (increment) {
				printLevel(level);
				printLn("INCREMENT THEN USE");
			} else if (decrement) {
				printLn("DECREMENT THEN USE");
			}
			value.print(level);
		}
	}
	
}
