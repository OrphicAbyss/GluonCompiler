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
		scanner.matchAndAccept('(', null);
		Expression();
		scanner.matchAndAccept(')', null);
	}

	/** Parse and Translate a Function */
	static void Function(String name){
		scanner.matchAndAccept('(', null);
		scanner.matchAndAccept(')', null);
		output.outputLine("CALL " + name, true);
	}

	/** Parse and Translate a Function or Variable */
	static void Ident() {
		String name = scanner.getIdentifier();
		if (scanner.matchOnly('(')) {
			Function(name);
		} else {
			GluonVariable.testVariableRegistered(name);
			output.outputLine("MOV EAX, [" + GluonLibrary.varToLabel(name) + "]", true);
		}
	}

	/** Parse and Translate a Constant or Variable */
	static void ConstVar() {
		if (scanner.isAlpha()) {
			Ident();
		} else {
			output.outputLine("MOV EAX, " + scanner.getInteger(), true);
		}
	}

	/** Parse and Translate a Unary Minus */
	static void UnaryMinus() {
		scanner.matchAndAccept('-', "Unary Minus");
		ConstVar();
		output.outputLine("NEG EAX", true);
	}

	/** Parse and Translate a Math Factor */
	static void Factor() {
		switch (scanner.current) {
			case '(':
				Parentheses();
				break;
			case '-':
				UnaryMinus();
				break;
			default:
				ConstVar();
				break;
		}
	}

	/** Parse and Translate a Multiply */
	static void Multiply() {
		scanner.matchAndAccept('*', "Multiplication");
		Factor();
		output.outputLine("POP EBX", true);
		output.outputLine("IMUL EAX,EBX", true);
	}

	/** Parse and Translate a Divide */
	static void Divide() {
		scanner.matchAndAccept('/', "Division");
		Factor();
		output.outputLine("MOV EBX, EAX", true);
		output.outputLine("MOV EDX, 0", true);
		output.outputLine("POP EAX", true);
		output.outputLine("IDIV EBX", true);
	}

	/** Parse and Translate a Math Term */
	static void Term() {
		Factor();
		while (scanner.current == '*' || scanner.current == '/') {
			output.outputLine("PUSH EAX", true);
			switch (scanner.current) {
				case '*':
					Multiply();
					break;
				case '/':
					Divide();
					break;
			}
		}
	}

	/** Parse and Translate an Add */
	static void Add() {
		scanner.matchAndAccept('+', "Addition");
		Term();
		output.outputLine("POP EBX",true);
		output.outputLine("ADD EAX,EBX", true);
	}

	/** Parse and Translate a Subtract */
	static void Subtract() {
		scanner.matchAndAccept('-', "Subtraction");
		Term();
		output.outputLine("POP EBX", true);
		output.outputLine("SUB EAX,EBX", true);
		output.outputLine("NEG EAX", true);
	}

	/** Parse and Translate a Math Expression */
	static void Expression() {
		Term();
		while (scanner.matchOnly('+') || scanner.matchOnly('-')) {
			output.outputLine("PUSH EAX", true);
			switch (scanner.current) {
				case '+':
					Add();
					break;
				case '-':
					Subtract();
					break;
			}
		}
	}

	/** Parse and Translate a Boolean Expression */
	static void BooleanExpression() {
		Expression();
		if (scanner.matchOnly('=') || scanner.matchOnly('<') || scanner.matchOnly('>') || scanner.matchOnly('!')){
			boolean eq = false;
			
			output.outputLine("PUSH EAX", true);
			switch(scanner.current){
				case '=':
					scanner.matchAndAccept('=', "Boolean Expression");
					scanner.matchAndAccept('=', "Boolean Expression");
					Expression();
					output.outputLine("POP EBX", true);
					output.outputLine("CMP EAX, EBX", true);
					output.outputLine("SETE AL", true);
					break;
				case '<':
					scanner.matchAndAccept('<', "Boolean Expression");
					if (scanner.matchOnly('=')){
						scanner.matchAndAccept('=', "Boolean Expression");
						eq = true;
					}
					Expression();
					output.outputLine("POP EBX", true);
					output.outputLine("CMP EAX, EBX", true);
					if (eq)
						output.outputLine("SETL AL", true);
					else
						output.outputLine("SETLE AL", true);
					
					break;
				case '>':
					scanner.matchAndAccept('>', "Boolean Expression");
					if (scanner.matchOnly('=')){
						scanner.matchAndAccept('=', "Boolean Expression");
						eq = true;
					}
					Expression();
					output.outputLine("POP EBX", true);
					output.outputLine("CMP EAX, EBX", true);
					if (eq)
						output.outputLine("SETG AL", true);
					else
						output.outputLine("SETGE AL", true);
					break;
				case '!':
					scanner.matchAndAccept('!', "Boolean Expression");
					scanner.matchAndAccept('=', "Boolean Expression");
					Expression();
					output.outputLine("POP EBX", true);
					output.outputLine("CMP EAX, EBX", true);
					output.outputLine("SETNE AL", true);
					break;
			}
		}
	}

	/** Parse and Translate an Assignment Statement */
	static void Assignment(String name) {
		GluonVariable.testVariableRegistered(name);
		
		if (scanner.matchOnly('+')){
			scanner.matchAndAccept('+', "Assignment");
			scanner.matchAndAccept('=', "Assignment");
			BooleanExpression();
			output.outputLine("MOV EBX,[" + GluonLibrary.varToLabel(name) + "]", true);
			output.outputLine("ADD EAX,EBX", true);
			output.outputLine("MOV [" + GluonLibrary.varToLabel(name) + "],EAX", true);
		} else if (scanner.matchOnly('-')){
			scanner.matchAndAccept('-', "Assignment");
			scanner.matchAndAccept('=', "Assignment");
			BooleanExpression();
			output.outputLine("MOV EBX,[" + GluonLibrary.varToLabel(name) + "]", true);
			output.outputLine("SUB EBX,EAX", true);
			output.outputLine("MOV [" + GluonLibrary.varToLabel(name) + "],EBX", true);			
		} else {
			scanner.matchAndAccept('=', "Assignment");
			BooleanExpression();
			output.outputLine("MOV [" + GluonLibrary.varToLabel(name) + "],EAX", true);
		}
	}

	/** Parse a variable declaration */
	static void DefineVariable(){
		scanner.skipWhitespace();
		String name = scanner.getIdentifier();
		GluonVariable.registerVariable(name);

		if (scanner.matchOnly('=')){
			Assignment(name);
		}
	}
	
	/** Parse and Translate a While Statement */
	static void WhileStatement(){
		String labelStart = "while_on_line_" + scanner.lineNumber + "_start";
		String labelEnd = "while_on_line_" + scanner.lineNumber + "_end";
		output.outputLine(labelStart + ":", false);
		BooleanExpression();
		scanner.matchAndAccept('\n', "While Statement");
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
			Assignment(scanner.getIdentifier());
		scanner.matchAndAccept(':', "For Statement");
		// loop test
		output.outputLine(labelTest + ":", false);
		BooleanExpression();
		scanner.matchAndAccept(':', "For Statement");
		output.outputLine("TEST EAX, EAX", true);
		output.outputLine("JNZ " + labelEnd, true);
		output.outputLine("JMP " + labelStart, true);
		// inc statement
		output.outputLine(labelInc + ":", false);
		Assignment(scanner.getIdentifier());
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
		scanner.matchAndAccept('\n', "If Statement");
		StatementsUntil("END");
		output.outputLine(label + ":", false);
	}
	
	/** Parse a statement */
	static void Statement(String name){
		switch (name.toUpperCase()){
			case "VAR":
				DefineVariable();
				break;
			case "IF":
				IfStatement();
				break;
			case "FOR":
				ForStatement();
				break;
			case "WHILE":
				WhileStatement();
				break;
			default:
				if (scanner.matchOnly('(')){
					Function(name);
				} else {
					Assignment(name);
				}
				break;
		}
	}

	static void StatementsUntil(String identifier){
		String currentIdentifier;
		while (!scanner.isEOF()){
			currentIdentifier = scanner.getIdentifier();
			if (identifier.equals(currentIdentifier.toUpperCase()))
				break;
			Statement(currentIdentifier);
			scanner.matchAndAccept('\n', "Statements");
		}
	}
	
	/** Main code compiling */
	static void Init() {
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
