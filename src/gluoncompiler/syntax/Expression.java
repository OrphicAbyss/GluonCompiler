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

	public Expression(Token next, ScopeObject parentScope) {
		first = next;
		scope = parentScope;
		terms = new ArrayList<>();
		ops = new ArrayList<>();
	}
	
	@Override
	public Token parse() {
		Token test = first;
		term = new Term(test, scope);
		test = term.parse();
		
		while (test.isOperator()) {
			Operator testOp = test.getOperator();
			if (Operator.ADD.equals(testOp) || Operator.SUBTRACT.equals(testOp)) {
				ops.add(testOp);
				Term t = new Term(test.getNext(), scope);
				terms.add(t);
				test = t.parse();
			} else {
				break;
			}
		}
		
		return test;
	}

	@Override
	public void emitCode(GluonOutput code) {
		term.emitCode(code);
		for (int i=0; i<terms.size(); i++){
			code.code("PUSH EAX");
			terms.get(i).emitCode(code);
			switch (ops.get(i)) {
				case ADD:
					code.code("POP EBX");
					code.code("ADD EAX,EBX");
					break;
				case SUBTRACT:
					code.code("POP EBX");
					code.code("SUB EAX,EBX");
					code.code("NEG EAX");
					break;
			}
		}
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
