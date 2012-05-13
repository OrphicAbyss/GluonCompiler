package gluoncompiler;

import java.io.File;

/**
 * Simple compiler for language code named 'Gluon'
 *
 * TODO: add functions
 * TODO: better token scanning
 * TODO: add classes
 * TODO: add strings
 * TODO: add arrays
 */
public class GluonCompiler {
	static GluonScanner scanner;
	static GluonOutput output;

	/** Parse and Translate Parentheses */
	static void Parentheses() {
		scanner.matchOperator(Operator.BRACKET_LEFT, null);
		Expression();
		scanner.matchOperator(Operator.BRACKET_RIGHT, null);
	}

	/** Parse and Translate a Function */
	static void Function(Token token){
		scanner.matchOperator(Operator.BRACKET_LEFT, "Function Call");
		scanner.matchOperator(Operator.BRACKET_RIGHT, "Function Call");
		output.outputLine("CALL " + token.getValue(), true);
	}

	/** Parse and Translate a Function or Variable */
	static void Ident() {
		Token token = scanner.getCurrentToken();
		scanner.nextToken();
		if (scanner.testOperator(Operator.BRACKET_LEFT)){
			Function(token);
		} else {
			GluonVariable.testVariableRegistered(token.getValue());
			output.outputLine("MOV EAX, [" + GluonLibrary.varToLabel(token.getValue()) + "]", true);
		}
	}

	/** Parse and Translate a Constant or Variable */
	static void ConstantOrVariable() {
		TokenType type = scanner.getCurrentToken().getType();
		switch (type){
			case IDENTIFIER:
				Ident();
				break;
			case LITERAL:
				output.outputLine("MOV EAX, " + scanner.getInteger(), true);
				break;
			default:
				TokenType[] expected = { TokenType.IDENTIFIER, TokenType.LITERAL };
				Error.expected(expected, scanner.getCurrentToken(), "Constant Or Variable");
		}
	}

	/** Parse and Translate a Unary Minus */
	static void UnaryMinus() {
		scanner.matchOperator(Operator.SUBTRACT, "Unary Minus");
		ConstantOrVariable();
		output.outputLine("NEG EAX", true);
	}

	/** Parse and Translate a Math Factor */
	static void Factor() {
		Operator oper = scanner.getCurrentToken().getOperator();
		switch (oper){
			case BRACKET_LEFT:
				Parentheses();
				break;
			case SUBTRACT:
				UnaryMinus();
				break;
			default:
				ConstantOrVariable();
				break;
		}
	}

	/** Parse and Translate a Multiply */
	static void Multiply() {
		scanner.matchOperator(Operator.MULTIPLY, "Multiplication");
		Factor();
		output.outputLine("POP EBX", true);
		output.outputLine("IMUL EAX,EBX", true);
	}

	/** Parse and Translate a Divide */
	static void Divide() {
		scanner.matchOperator(Operator.DIVIDE, "Division");
		Factor();
		output.outputLine("MOV EBX, EAX", true);
		output.outputLine("MOV EDX, 0", true);
		output.outputLine("POP EAX", true);
		output.outputLine("IDIV EBX", true);
	}

	/** Parse and Translate a Math Term */
	static void Term() {
		Factor();
		while (scanner.testOperator(Operator.MULTIPLY)
				|| scanner.testOperator(Operator.DIVIDE)) {
			output.outputLine("PUSH EAX", true);
			switch (scanner.getCurrentOperator()) {
				case MULTIPLY:
					Multiply();
					break;
				case DIVIDE:
					Divide();
					break;
			}
		}
	}

	/** Parse and Translate an Add */
	static void Add() {
		scanner.matchOperator(Operator.ADD, "Addition");
		Term();
		output.outputLine("POP EBX",true);
		output.outputLine("ADD EAX,EBX", true);
	}

	/** Parse and Translate a Subtract */
	static void Subtract() {
		scanner.matchOperator(Operator.SUBTRACT, "Subtraction");
		Term();
		output.outputLine("POP EBX", true);
		output.outputLine("SUB EAX,EBX", true);
		output.outputLine("NEG EAX", true);
	}

	/** Parse and Translate a Math Expression */
	static void Expression() {
		Term();
		while (scanner.testOperator(Operator.ADD)
			|| scanner.testOperator(Operator.SUBTRACT)) {
			output.outputLine("PUSH EAX", true);
			switch (scanner.getCurrentOperator()) {
				case ADD:
					Add();
					break;
				case SUBTRACT:
					Subtract();
					break;
			}
		}
	}

	/** Parse and Translate a Boolean Expression */
	static void BooleanExpression() {
		Expression();

		String testExp = null;

		switch (scanner.getCurrentOperator()){
			case EQUALS:
				scanner.matchOperator(Operator.EQUALS, "Boolean Expression");
				testExp = "SETE AL";
				break;
			case LESS_THAN:
				scanner.matchOperator(Operator.LESS_THAN, "Boolean Expression");
				testExp = "SETLE AL";
				break;
			case LESS_THAN_OR_EQUALS:
				scanner.matchOperator(Operator.LESS_THAN_OR_EQUALS,"Boolean Expression");
				testExp = "SETL AL";
				break;
			case GREATER_THAN:
				scanner.matchOperator(Operator.GREATER_THAN, "Boolean Expression");
				testExp = "SETGE AL";
				break;
			case GREATER_THAN_OR_EQUALS:
				scanner.matchOperator(Operator.GREATER_THAN_OR_EQUALS, "Boolean Expression");
				testExp = "SETG AL";
				break;
			case NOT_EQUALS:
				scanner.matchOperator(Operator.NOT_EQUALS, "Boolean Expression");
				testExp = "SETNE AL";
				break;
			default:
				return;
		}

		output.outputLine("PUSH EAX", true);
		Expression();
		output.outputLine("POP EBX", true);
		output.outputLine("CMP EAX, EBX", true);
		output.outputLine(testExp, true);
	}

	/** Parse and Translate an Assignment Statement */
	static void Assignment(Token token) {
		String name = token.getValue();
		GluonVariable.testVariableRegistered(name);

		switch (scanner.getCurrentOperator()){
			case ASSIGN:
				scanner.nextToken();
				BooleanExpression();
				output.outputLine("MOV [" + GluonLibrary.varToLabel(name) + "],EAX", true);
				break;
			case ASSIGN_ADD:
				scanner.nextToken();
				BooleanExpression();
				output.outputLine("MOV EBX,[" + GluonLibrary.varToLabel(name) + "]", true);
				output.outputLine("ADD EAX,EBX", true);
				output.outputLine("MOV [" + GluonLibrary.varToLabel(name) + "],EAX", true);
				break;
			case ASSIGN_SUBTRACT:
				scanner.nextToken();
				BooleanExpression();
				output.outputLine("MOV EBX,[" + GluonLibrary.varToLabel(name) + "]", true);
				output.outputLine("SUB EBX,EAX", true);
				output.outputLine("MOV [" + GluonLibrary.varToLabel(name) + "],EBX", true);
				break;
			case ASSIGN_MULTIPLY:
				scanner.nextToken();
				BooleanExpression();
				output.outputLine("MOV EBX,[" + GluonLibrary.varToLabel(name) + "]", true);
				output.outputLine("IMUL EAX,EBX", true);
				output.outputLine("MOV [" + GluonLibrary.varToLabel(name) + "],EBX", true);
				break;
			case ASSIGN_DIVIDE:
				scanner.nextToken();
				BooleanExpression();
				output.outputLine("MOV EBX, EAX", true);
				output.outputLine("MOV EDX, 0", true);
				output.outputLine("MOV EAX,[" + GluonLibrary.varToLabel(name) + "]", true);
				output.outputLine("IDIV EBX", true);
				output.outputLine("MOV [" + GluonLibrary.varToLabel(name) + "],EBX", true);
				break;
		}
	}

	/** Parse a variable declaration */
	static void DefineVariable(){
		Token varName = scanner.getCurrentToken();
		String name = varName.getValue();
		GluonVariable.registerVariable(name);
		scanner.nextToken();
		Operator[] ops = {Operator.ASSIGN, Operator.ASSIGN_ADD, Operator.ASSIGN_SUBTRACT, Operator.ASSIGN_MULTIPLY, Operator.DIVIDE};

		if (scanner.testOperators(ops)){
			Assignment(varName);
		}
	}

	/** Parse and Translate a While Statement */
	static void WhileStatement(){
		String labelStart = "while_on_line_" + scanner.lineNumber + "_start";
		String labelEnd = "while_on_line_" + scanner.lineNumber + "_end";
		output.outputLine(labelStart + ":", false);
		BooleanExpression();
		scanner.matchNewline("While Statement");
		output.outputLine("TEST EAX, EAX", true);
		output.outputLine("JNZ " + labelEnd, true);
		StatementsUntil("END");
		output.outputLine("JMP " + labelStart, true);
		output.outputLine(labelEnd + ":", false);
	}

	/** Parse and Translate a For Statement */
	static void ForStatement(){
		String labelStart = "for_on_line_" + scanner.lineNumber + "_start";
		String labelTest = "for_on_line_" + scanner.lineNumber + "_test";
		String labelInc = "for_on_line_" + scanner.lineNumber + "_inc";
		String labelEnd = "for_on_line_" + scanner.lineNumber + "_end";

		// first assignment
		if (!scanner.matchOnly(':'))
			Assignment(scanner.getCurrentToken());
		scanner.matchOperator(Operator.COLON, "For Statement");
		// loop test
		output.outputLine(labelTest + ":", false);
		BooleanExpression();
		scanner.matchOperator(Operator.COLON, "For Statement");
		output.outputLine("TEST EAX, EAX", true);
		output.outputLine("JNZ " + labelEnd, true);
		output.outputLine("JMP " + labelStart, true);
		// inc statement
		output.outputLine(labelInc + ":", false);
		Assignment(scanner.getCurrentToken());
		output.outputLine("JMP " + labelTest, true);
		// loop code
		output.outputLine(labelStart + ":", false);
		scanner.matchAndAccept('\n', "For Statement");
		StatementsUntil("END");
		output.outputLine("JMP " + labelInc, false);
		output.outputLine(labelEnd + ":", false);
	}

	/** Parse and Translate an If Statement */
	static void IfStatement(){
		BooleanExpression();
		String label = "if_on_ln_" + scanner.lineNumber;
		output.outputLine("TEST EAX, EAX", true);
		output.outputLine("JZ " + label, true);
		scanner.matchNewline("If Statement");
		StatementsUntil("END");
		output.outputLine(label + ":", false);
	}

	static void FunctionDef(){
		scanner.matchOperator(Operator.BRACKET_LEFT, "Function Def");
		int paramaters = 0;
		// TODO: Match paramaters
		scanner.matchOperator(Operator.BRACKET_RIGHT, "Function Def");
		if (scanner.matchOnly(':')){
			Error.abort("Functions currently don't support returns.");
		}
		// Push BP
		// Mov BP, SP
		// Push all reg

		// Pop all reg
		// Pop BP
		// Ret paramaters

	}

	/** Parse a statement */
	static void Statement(Token token){
		switch (token.getType()){
			case KEYWORD:
				switch (token.getKeyword()){
					case VAR:
						scanner.nextToken();
						DefineVariable();
						break;
					case IF:
						scanner.nextToken();
						IfStatement();
						break;
					case FOR:
						scanner.nextToken();
						ForStatement();
						break;
					case WHILE:
						scanner.nextToken();
						WhileStatement();
					default:
						Error.abort("Unexpected keyword found: " + token.getKeyword().name() + " when parsing Statement.");
				}
				break;
			case IDENTIFIER:
				if (scanner.testOperator(Operator.BRACKET_LEFT)){
					Function(token);
				} else {
					Assignment(token);
				}
				break;
			default:
				TokenType[] expected = {TokenType.KEYWORD, TokenType.IDENTIFIER};
				Error.expected(expected, token, "Statement");
		}
	}

	static void StatementsUntil(String identifier){
		while (!scanner.testEOF()){
			Token currentToken = scanner.getCurrentToken();
			if (identifier.equals(currentToken.getValue()))
				break;
			Statement(currentToken);

			scanner.matchNewline("Statements");
		}
	}

	/** Main code compiling */
	static void Init() {
		scanner.tokenise();

		Error.init(scanner);
		GluonVariable.init();

		output = new GluonOutput();
		GluonLibrary.printASMStart(output);

		// Match statements until the end of file
		StatementsUntil("");

		GluonLibrary.printASMEnd(output);
		GluonLibrary.printVariables(output, GluonVariable.getVariables());

		System.out.print(output.getOutput());
	}

	/**
	 * Takes a filename as the main argument.
	 */
	public static void main(String[] args) {
		if (args.length == 0){
			// Print out help
			System.out.println("Gluon Compiler - No filename provided input from terminal:");
			scanner = new GluonScanner();
		} else {
			System.out.println("Gluon Compiler - Reading source file: " + args[0]);
			// Make sure the source file exists
			File source = new File(args[0]);
			if (!source.exists()){
				Error.abort("Source file does not exist.");
			} else {
				scanner = new GluonScanner(source);
			}
		}

		Init();
	}
}
