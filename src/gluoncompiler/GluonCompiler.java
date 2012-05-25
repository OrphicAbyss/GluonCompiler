package gluoncompiler;

import gluoncompiler.syntax.*;
import java.io.File;

/**
 * Simple compiler for language code named 'Gluon'
 *
 * TODO: add functions
 * TODO: add classes
 * TODO: add strings
 * TODO: add arrays
 */
public class GluonCompiler {
	static GluonScanner scanner;
	static Tokeniser tokeniser;
	static SyntaxBuilder syntaxBuilder;
	static GluonOutput output;

	/** Parse and Translate Parentheses */
	public static void Parentheses() {
		tokeniser.matchOperator(Operator.BRACKET_LEFT, null);
		BooleanExpression();
		tokeniser.matchOperator(Operator.BRACKET_RIGHT, null);
	}

	/** Parse and Translate a Function */
	public static void Function(Token token){
		SyntaxObject func = new FunctionCall(token);
		output.code(func.emitCode());
	}

	/** Parse and Translate a Function or Variable */
	public static void Ident() {
		Token token = tokeniser.getCurrentToken();
		tokeniser.nextToken();
		if (tokeniser.testOperator(Operator.BRACKET_LEFT)){
			Function(token);
		} else {
			// Variable value
			SyntaxObject variable = new Variable(token);
			output.code("MOV EAX, [" + variable.emitCode() + "]");
		}
	}

	/** Parse and Translate a Constant or Variable */
	public static void ConstantOrVariable() {
		TokenType type = tokeniser.getCurrentToken().getType();
		switch (type){
			case IDENTIFIER:
				Ident();
				break;
			case LITERAL:
				SyntaxObject constant = new LiteralNumber(tokeniser.getCurrentToken());
				tokeniser.nextToken();
				output.code("MOV EAX, " + constant.emitCode());
				break;
			default:
				TokenType[] expected = { TokenType.IDENTIFIER, TokenType.LITERAL };
				Error.expected(expected, tokeniser.getCurrentToken(), "Constant Or Variable");
		}
	}

	/** Parse and Translate a Unary Minus */
	public static void UnaryMinus() {
		tokeniser.matchOperator(Operator.SUBTRACT, "Unary Minus");
		ConstantOrVariable();
		output.code("NEG EAX");
	}

	public static void UnaryPlus(){
		tokeniser.matchOperator(Operator.ADD, "Unary Plus");
		ConstantOrVariable();
	}
	
	/** Parse and Translate a Math Factor */
	public static void Factor() {
		Token token = tokeniser.getCurrentToken();
		if (token.isOperator()){
			Operator oper = token.getOperator();
			switch (oper){
				case BRACKET_LEFT:
					Parentheses();
					break;
				case SUBTRACT:
					UnaryMinus();
					break;
				case ADD:
					UnaryPlus();
					break;
			}
		} else {
			ConstantOrVariable();
		}
	}

	/** Parse and Translate a Multiply */
	public static void Multiply() {
		tokeniser.matchOperator(Operator.MULTIPLY, "Multiplication");
		Factor();
		output.code("POP EBX");
		output.code("IMUL EAX,EBX");
	}

	/** Parse and Translate a Divide */
	public static void Divide() {
		tokeniser.matchOperator(Operator.DIVIDE, "Division");
		Factor();
		output.code("MOV EBX, EAX");
		output.code("MOV EDX, 0");
		output.code("POP EAX");
		output.code("IDIV EBX");
	}

	/** Parse and Translate a Math Term */
	public static void Term() {
		Factor();
		while (tokeniser.testOperator(Operator.MULTIPLY)
				|| tokeniser.testOperator(Operator.DIVIDE)) {
			output.code("PUSH EAX");
			switch (tokeniser.getCurrentOperator()) {
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
	public static void Add() {
		tokeniser.matchOperator(Operator.ADD, "Addition");
		Term();
		output.code("POP EBX");
		output.code("ADD EAX,EBX");
	}

	/** Parse and Translate a Subtract */
	public static void Subtract() {
		tokeniser.matchOperator(Operator.SUBTRACT, "Subtraction");
		Term();
		output.code("POP EBX");
		output.code("SUB EAX,EBX");
		output.code("NEG EAX");
	}

	/** Parse and Translate a Math Expression */
	public static void Expression() {
		Term();
		while (tokeniser.testOperator(Operator.ADD)
			|| tokeniser.testOperator(Operator.SUBTRACT)) {
			output.code("PUSH EAX");
			switch (tokeniser.getCurrentOperator()) {
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
	public static void BooleanExpression() {
		Expression();

		if (tokeniser.getCurrentToken().isOperator()){
			String testExp;
			switch (tokeniser.getCurrentOperator()){
				case EQUALS:
					testExp = "SETE AL";
					break;
				case LESS_THAN:
					testExp = "SETG AL";
					break;
				case LESS_THAN_OR_EQUALS:
					testExp = "SETGE AL";
					break;
				case GREATER_THAN:
					testExp = "SETL AL";
					break;
				case GREATER_THAN_OR_EQUALS:
					testExp = "SETLE AL";
					break;
				case NOT_EQUALS:
					testExp = "SETNE AL";
					break;
				default:
					return;
			}
			tokeniser.nextToken();
			output.code("PUSH EAX");
			Expression();
			output.code("POP EBX");
			output.code("CMP EAX, EBX");
			output.code(testExp);
		}

	}

	/** Parse and Translate an Assignment Statement */
	public static void Assignment(Token token) {
		output.comment("Assignment Statement");
		String name = token.getValue();
		GluonVariable.testVariableRegistered(name);

		switch (tokeniser.getCurrentOperator()){
			case ASSIGN:
				tokeniser.nextToken();
				BooleanExpression();
				output.code("MOV [" + GluonLibrary.varToLabel(name) + "],EAX");
				break;
			case ASSIGN_ADD:
				tokeniser.nextToken();
				BooleanExpression();
				output.code("MOV EBX,[" + GluonLibrary.varToLabel(name) + "]");
				output.code("ADD EAX,EBX");
				break;
			case ASSIGN_SUBTRACT:
				tokeniser.nextToken();
				BooleanExpression();
				output.code("MOV EBX, EAX");
				output.code("MOV EAX,[" + GluonLibrary.varToLabel(name) + "]");
				output.code("SUB EAX,EBX");
				break;
			case ASSIGN_MULTIPLY:
				tokeniser.nextToken();
				BooleanExpression();
				output.code("MOV EBX,[" + GluonLibrary.varToLabel(name) + "]");
				output.code("IMUL EAX,EBX");
				break;
			case ASSIGN_DIVIDE:
				tokeniser.nextToken();
				BooleanExpression();
				output.code("MOV EBX, EAX");
				output.code("MOV EDX, 0");
				output.code("MOV EAX,[" + GluonLibrary.varToLabel(name) + "]");
				output.code("IDIV EBX");
				break;
		}
		output.code("MOV [" + GluonLibrary.varToLabel(name) + "],EAX");
	}

	/** Parse a variable declaration */
	public static void DefineVariable(){
		Token varName = tokeniser.getCurrentToken();
		String name = varName.getValue();
		GluonVariable.registerVariable(name);
		tokeniser.nextToken();
		Operator[] ops = {Operator.ASSIGN, Operator.ASSIGN_ADD, Operator.ASSIGN_SUBTRACT, Operator.ASSIGN_MULTIPLY, Operator.DIVIDE};

		if (tokeniser.testOperators(ops)){
			Assignment(varName);
		}
	}

	/** Parse and Translate a While Statement */
	public static void WhileStatement(){
		output.comment("While Statement");
		Token cur = tokeniser.getPreviousToken();
		String labelStart = GluonLabels.createLabel(cur, "start");
		String labelEnd = GluonLabels.createLabel(cur, "end");
		GluonLabels.addEndLabel(labelEnd);
		output.label(labelStart);
		BooleanExpression();
		tokeniser.matchNewline("While Statement");
		output.code("TEST EAX, EAX");
		output.code("JZ " + labelEnd);
		StatementsUntil(Keyword.END);
		output.code("JMP " + labelStart);
		output.label(labelEnd);
		GluonLabels.removeEndLabel(labelEnd);
	}

	/** Parse and Translate a For Statement */
	public static void ForStatement(){
		output.comment("For Statememt");
		Token cur = tokeniser.getPreviousToken();
		String labelStart = GluonLabels.createLabel(cur, "start");
		String labelTest = GluonLabels.createLabel(cur, "test");
		String labelInc = GluonLabels.createLabel(cur, "inc");
		String labelEnd = GluonLabels.createLabel(cur, "end");
		GluonLabels.addEndLabel(labelEnd);
		// first assignment
		if (!tokeniser.testOperator(Operator.COLON))
			Assignment(tokeniser.getCurrentToken());
		tokeniser.matchOperator(Operator.COLON, "For Statement");
		// loop test
		output.label(labelTest);
		BooleanExpression();
		tokeniser.matchOperator(Operator.COLON, "For Statement");
		output.code("TEST EAX, EAX");
		output.code("JNZ " + labelEnd);
		output.code("JMP " + labelStart);
		// inc statement
		output.label(labelInc);
		Assignment(tokeniser.getCurrentToken());
		output.code("JMP " + labelTest);
		// loop code
		output.label(labelStart);
		tokeniser.matchNewline("For Statement");
		StatementsUntil(Keyword.END);
		output.code("JMP " + labelInc);
		output.label(labelEnd);
		
		GluonLabels.removeEndLabel(labelEnd);
	}

	public static void BreakStatement(){
		output.comment("Break");
		String label = GluonLabels.getEndLabel();
		output.code("JMP " + label);
	}
	
	/** Parse and Translate an If Statement */
	public static void IfStatement(){
		output.comment("If Statement");
		Token cur = tokeniser.getPreviousToken();
		String label = GluonLabels.createLabel(cur, "end");
		BooleanExpression();
		output.code("TEST EAX, EAX");
		output.code("JZ " + label);
		tokeniser.matchNewline("If Statement");
		StatementsUntil(Keyword.END);
		output.label(label);
	}

	public static void FunctionDef(){
		tokeniser.matchOperator(Operator.BRACKET_LEFT, "Function Def");
		int paramaters = 0;
		// TODO: Match paramaters
		tokeniser.matchOperator(Operator.BRACKET_RIGHT, "Function Def");
		if (tokeniser.testOperator(Operator.COLON)){
			tokeniser.nextToken();
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
	public static void Statement(Token token){
		tokeniser.nextToken();
		switch (token.getType()){
			case KEYWORD:
				switch (token.getKeyword()){
					case VAR:
						DefineVariable();
						break;
					case IF:
						IfStatement();
						break;
					case FOR:
						ForStatement();
						break;
					case WHILE:
						WhileStatement();
						break;
					case BREAK:
						BreakStatement();
						break;
					default:
						Error.abort("Unexpected keyword found: " + token.getKeyword().name() + " when parsing Statement.");
				}
				break;
			case IDENTIFIER:
				if (tokeniser.testOperator(Operator.BRACKET_LEFT)){
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

	public static void StatementsUntil(Keyword end){
		while (!tokeniser.testEOF()){
			Token currentToken = tokeniser.getCurrentToken();
			
			if (tokeniser.testKeyword(end)){
				tokeniser.nextToken();
				break;
			}
			
			Statement(currentToken);

			tokeniser.matchNewline("Statements");
		}
	}

	/** Main code compiling */
	public static void Init() {
		tokeniser = new Tokeniser(scanner);
		tokeniser.tokenise();
		
		syntaxBuilder = new SyntaxBuilder(tokeniser);
		syntaxBuilder.buildTree();
		
		Error.init(scanner);
		GluonVariable.init();

		output = new GluonOutput();
		GluonLibrary.printASMStart(output);

		// Match statements until the end of file
		GluonCompiler.StatementsUntil(null);

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
