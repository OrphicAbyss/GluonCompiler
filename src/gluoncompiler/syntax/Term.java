package gluoncompiler.syntax;

import gluoncompiler.GluonOutput;
import gluoncompiler.Operator;
import gluoncompiler.Token;
import java.util.ArrayList;

/**
 * Term := Factor [ (*|/) Factor ]+
 */
class Term extends SyntaxObject {
	
	Token first;
	Factor factor;
	ArrayList<Factor> factors;
	ArrayList<Operator> ops;
	
	public Term(Token test, ScopeObject parentScope) {
		first = test;
		scope = parentScope;
		factors = new ArrayList<>();
		ops = new ArrayList<>();
	}

	@Override
	public Token parse() {
		Token test = first;
		
		factor = new Factor(test, scope);
		test = factor.parse();
		
		while (test.isOperator()) {
			Operator testOp = test.getOperator();
			if (Operator.MULTIPLY.equals(testOp) || Operator.DIVIDE.equals(testOp)) {
				ops.add(testOp);
				Factor f = new Factor(test.getNext(), scope);
				factors.add(f);
				test = f.parse();
			} else {
				break;
			}
		}
		
		return test;
	}

	@Override
	public void emitCode(StringBuilder code) {
		factor.emitCode(code);
		for (int i=0; i<factors.size(); i++) {
			code.append(GluonOutput.codeLine("PUSH EAX"));
			factors.get(i).emitCode(code);
			switch (ops.get(i)) {
				case MULTIPLY:
					code.append(GluonOutput.codeLine("POP EBX"));
					code.append(GluonOutput.codeLine("IMUL EAX,EBX"));
					break;
				case DIVIDE:
					code.append(GluonOutput.codeLine("MOV EBX, EAX"));
					code.append(GluonOutput.codeLine("MOV EDX, 0"));
					code.append(GluonOutput.codeLine("POP EAX"));
					code.append(GluonOutput.codeLine("IDIV EBX"));
					break;
			}
		}
	}

	@Override
	public void print(int level) {
		factor.print(level);
		for (int i=0; i<ops.size(); i++){
			printLevel(level);
			printLn(ops.get(i).name());
			factors.get(i).print(level);
		}
	}
	
}
