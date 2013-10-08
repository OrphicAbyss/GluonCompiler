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
	boolean preIncrement;
	boolean postIncrement;
	boolean preDecrement;
	boolean postDecrement;

	BooleanExpression subExpression;
	Variable variable;
	LiteralNumber literal;
	
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
					preIncrement = true;
					test = test.getNext();
					break;
				case DECREMENT:
					preDecrement = true;
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

			test = test.getNext();
		} else if (test.isLiteral()) {
			if (preIncrement || preDecrement)
				throw new RuntimeException("Increment/decrement operators can't operate on literals. Found: " + test.toString());
			
			literal = new LiteralNumber(test, scope);
			test = literal.parse();
			if (unaryMinus) {
				literal.setNegative();
				unaryMinus = false;
			}
		} else if (test.isIdentifier()) {
			variable = new Variable(test, scope);
			test = variable.parse();
			if (test.isOperator(Operator.INCREMENT)) {
				postIncrement = true;
				test = test.getNext();
			} else if (test.isOperator(Operator.DECREMENT)) {
				postDecrement = true;
				test = test.getNext();
			}
		} else {
			throw new RuntimeException("Unknown token: " + test);
		}
		return test;
	}

	@Override
	public void emitCode(GluonOutput code) {
		if (subExpression != null){
			subExpression.emitCode(code);
		} else if (literal != null) {
			literal.emitCode(code);
		} else {
			if (preIncrement)
				code.code("INC " + variable.getLabelName());
			else if (preDecrement)
				code.code("DEC " + variable.getLabelName());
			
			variable.emitCode(code);
			
			if (postIncrement)
				code.code("INC " + variable.getLabelName());
			else if (postDecrement)
				code.code("DEC " + variable.getLabelName());
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
			} else if (preIncrement) {
				printLevel(level);
				printLn("INCREMENT THEN USE");
			} else if (preDecrement) {
				printLn("DECREMENT THEN USE");
			}
			
			if (postIncrement) {
				printLevel(level);
				printLn("INCREMENT AFTER USE");
			} else if (postDecrement) {
				printLevel(level);
				printLn("DECREMENT AFTER USE");
			}
			if (variable != null)
				variable.print(level);
			else
				literal.print(level);
		}
	}
	
}
