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
	
	public Term(Token test) {
		first = test;
		factors = new ArrayList<>();
		ops = new ArrayList<>();
	}

	@Override
	public Token parse() {
		Token test = first;
		
		factor = new Factor(test);
		test = factor.parse();
		
		while (test.isOperator()) {
			Operator testOp = test.getOperator();
			if (Operator.MULTIPLY.equals(testOp) || Operator.DIVIDE.equals(testOp)) {
				ops.add(testOp);
				Factor f = new Factor(test.getNext());
				factors.add(f);
				test = f.parse();
			} else {
				break;
			}
		}
		
		return test;
	}

	@Override
	public String emitCode() {
		StringBuilder sb = new StringBuilder();
		sb.append(factor.emitCode());
		for (int i=0; i<factors.size(); i++) {
			sb.append(GluonOutput.codeLine("PUSH EAX"));
			sb.append(factors.get(i).emitCode());
			switch (ops.get(i)) {
				case MULTIPLY:
					sb.append(GluonOutput.codeLine("POP EBX"));
					sb.append(GluonOutput.codeLine("IMUL EAX,EBX"));
					break;
				case DIVIDE:
					sb.append(GluonOutput.codeLine("MOV EBX, EAX"));
					sb.append(GluonOutput.codeLine("MOV EDX, 0"));
					sb.append(GluonOutput.codeLine("POP EAX"));
					sb.append(GluonOutput.codeLine("IDIV EBX"));
					break;
			}
		}
		return sb.toString();
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
