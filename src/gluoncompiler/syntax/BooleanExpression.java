package gluoncompiler.syntax;

import gluoncompiler.GluonOutput;
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
	public void emitCode(StringBuilder code) {
		if (exp2 == null) {
			exp1.emitCode(code);
			return;
		}
		
		exp1.emitCode(code);		
		code.append(GluonOutput.codeLine("PUSH EAX"));
		exp2.emitCode(code);
		code.append(GluonOutput.codeLine("POP EBX"));
		code.append(GluonOutput.codeLine("CMP EAX, EBX"));
		
		switch (compare){
			case EQUALS:
				code.append(GluonOutput.codeLine("SETE AL"));
				break;
			case LESS_THAN:
				code.append(GluonOutput.codeLine("SETG AL"));
				break;
			case LESS_THAN_OR_EQUALS:
				code.append(GluonOutput.codeLine("SETGE AL"));
				break;
			case GREATER_THAN:
				code.append(GluonOutput.codeLine("SETL AL"));
				break;
			case GREATER_THAN_OR_EQUALS:
				code.append(GluonOutput.codeLine("SETLE AL"));
				break;
			case NOT_EQUALS:
				code.append(GluonOutput.codeLine("SETNE AL"));
				break;
			default:
				throw new RuntimeException("Unknown compare type.");
		}
	}

	@Override
	public void print(int level) {
		exp1.print(level);
		if (compare != null) {
			printLevel(level);
			printLn(compare.name());
			if (exp2 != null) {
				exp2.print(level);
			}
		}
	}
	
}
