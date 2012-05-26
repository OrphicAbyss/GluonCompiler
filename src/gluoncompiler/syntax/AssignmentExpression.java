package gluoncompiler.syntax;

import gluoncompiler.GluonLibrary;
import gluoncompiler.GluonOutput;
import gluoncompiler.Operator;
import gluoncompiler.Token;

/**
 * Assignment Expression := [<Identifier> [+|-|*|/]=] <Boolean Expression>
 */
public class AssignmentExpression extends SyntaxObject {

	private Token first;
	private Variable variable;
	private Operator assignmentOp;
	private BooleanExpression assignmentExp;
	private BooleanExpression expression;
	
	public AssignmentExpression(Token next){
		first = next;
	}
	
	@Override
	public Token parse() {
		Token test = first.getNext();
		if (test.isOperator()) {
			Operator testOp = test.getOperator();
			if (testOp.name().startsWith("ASSIGN")){
				variable = new Variable(first);
				assignmentOp = testOp;
				test = test.getNext();
				assignmentExp = new BooleanExpression(test);
				test = assignmentExp.parse();
			}
		}
		
		if (variable == null) {
			expression = new BooleanExpression(first);
		}
		
		return test;
	}

	@Override
	public String emitCode() {
		StringBuilder sb = new StringBuilder();
		
		if (variable != null){
			String varName = GluonLibrary.varToLabel(variable.getName());
			sb.append(assignmentExp.emitCode());
			
			switch (assignmentOp){
				case ASSIGN:
					break;
				case ASSIGN_ADD:
					sb.append(GluonOutput.codeLine("MOV EBX,[" + varName + "]"));
					sb.append(GluonOutput.codeLine("ADD EAX,EBX"));
					break;
				case ASSIGN_SUBTRACT:
					sb.append(GluonOutput.codeLine("MOV EBX, EAX"));
					sb.append(GluonOutput.codeLine("MOV EAX,[" + varName + "]"));
					sb.append(GluonOutput.codeLine("SUB EAX,EBX"));
					break;
				case ASSIGN_MULTIPLY:
					sb.append(GluonOutput.codeLine("MOV EBX,[" + varName + "]"));
					sb.append(GluonOutput.codeLine("IMUL EAX,EBX"));
					break;
				case ASSIGN_DIVIDE:
					sb.append(GluonOutput.codeLine("MOV EBX, EAX"));
					sb.append(GluonOutput.codeLine("MOV EDX, 0"));
					sb.append(GluonOutput.codeLine("MOV EAX,[" + varName + "]"));
					sb.append(GluonOutput.codeLine("IDIV EBX"));
					break;
			}
			sb.append(GluonOutput.codeLine("MOV [" + varName + "],EAX"));
		} else {
			sb.append(expression.emitCode());
		}
		
		return sb.toString();
	}

	@Override
	public void print(int level) {
		if (assignmentExp != null) {
			variable.print(level);
			printLevel(level + 1);
			printLn(assignmentOp.name());
			assignmentExp.print(level + 1);
		} else {
			expression.print(level);
		}
	}
	
}
