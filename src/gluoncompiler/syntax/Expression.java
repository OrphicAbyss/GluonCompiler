package gluoncompiler.syntax;

import gluoncompiler.GluonOutput;
import gluoncompiler.Operator;
import gluoncompiler.Token;
import java.util.ArrayList;

/**
 * Expression := <Term> [ (+|-) <Term> ]+
 */
class Expression extends SyntaxObject {
	private Token first;
	private Term term;
	ArrayList<Term> terms;
	ArrayList<Operator> ops;

	public Expression(Token next) {
		first = next;
		terms = new ArrayList<>();
		ops = new ArrayList<>();
	}
	
	@Override
	public Token parse() {
		Token test = first;
		term = new Term(test);
		test = term.parse();
		
		while (test.isOperator()) {
			Operator testOp = test.getOperator();
			if (Operator.ADD.equals(testOp) || Operator.SUBTRACT.equals(testOp)) {
				ops.add(testOp);
				Term t = new Term(test.getNext());
				terms.add(t);
				test = t.parse();
			} else {
				break;
			}
		}
		
		return test;
	}

	@Override
	public String emitCode() {
		StringBuilder sb = new StringBuilder();
		sb.append(term.emitCode());
		for (int i=0; i<terms.size(); i++){
			sb.append(GluonOutput.codeLine("PUSH EAX"));
			sb.append(terms.get(i).emitCode());
			switch (ops.get(i)) {
				case ADD:
					sb.append(GluonOutput.codeLine("POP EBX"));
					sb.append(GluonOutput.codeLine("ADD EAX,EBX"));
					break;
				case SUBTRACT:
					sb.append(GluonOutput.codeLine("POP EBX"));
					sb.append(GluonOutput.codeLine("SUB EAX,EBX"));
					sb.append(GluonOutput.codeLine("NEG EAX"));
					break;
			}
		}
		return sb.toString();
	}

	@Override
	public void print(int level) {
		term.print(level);
		for (int i=0; i<ops.size(); i++){
			printLevel(level);
			printLn(ops.get(i).name());
			terms.get(i).print(level);
		}
	}
	
}
