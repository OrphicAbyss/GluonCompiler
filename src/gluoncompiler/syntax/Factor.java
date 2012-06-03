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
	
	public Factor(Token start, ScopeObject parentScopet) {
		first = start;
		scope = parentScopet;
	}

	@Override
	public Token parse() {
		Token test = first;
		
		// test for unary operators
		if (test.isOperator()) {
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
		if (test.isOperator(Operator.BRACKET_LEFT)) {
			// We need to seperate the unary ops to their own level so that we can have -(10*2)
			subExpression = new BooleanExpression(test.getNext(), scope);
			test = subExpression.parse();

			if (!test.isOperator(Operator.BRACKET_RIGHT))
				throw new RuntimeException("Expected closing bracket. Found: " + test);

			return test.getNext();
		} else if (test.isLiteral()) {
			if (increment || decrement)
				throw new RuntimeException("Increment/decrement operators can't operate on literals. Found: " + test.toString());
			
			value = new PostFix(test, scope);
			return value.parse();
		} else if (test.isIdentifier()) {
			value = new PostFix(test, scope);
			return value.parse();
		} else {
			throw new RuntimeException("Unknown token: " + test);
		}
	}

	@Override
	public void emitCode(GluonOutput code) {
		if (subExpression != null){
			subExpression.emitCode(code);
		} else {
			if (increment)
				code.code("INC [" + GluonLibrary.varToLabel(value.getVariable().getName()) + "]");
			else if (decrement)
				code.code("DEC [" + GluonLibrary.varToLabel(value.getVariable().getName()) + "]");
			
			value.emitCode(code);
		}
		
		if (unaryMinus){
			code.code("NEG EAX");
		} else if (unaryPlus){
			// do nothing
		}
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
