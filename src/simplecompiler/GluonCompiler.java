package simplecompiler;

import java.io.File;
import java.util.ArrayList;

/**
 * Simple compiler for language code named 'Gluon'
 */
public class GluonCompiler {
	static ArrayList<String> variables;
	static GluonScanner scanner;
	static GluonOutput output;

	/**
	 * Register a variable in our list of variables if it is not already
	 * contained in it.
	 *
	 * @param name
	 */
	static void registerVariable(String name){
		if (!variables.contains(name)){
			variables.add(name);
		}
	}

	static void testVariableRegistered(String name){
		if (!variables.contains(name)){
			Error.abort(scanner, "Variable used before assigned to: " + name);
		}
	}
	
	/** Parse and Translate Parentheses */
	static void Parentheses() {
		scanner.matchAndAccept('(');
		Expression();
		scanner.matchAndAccept(')');
	}

	/** Parse and Translate a Function */
	static void Function(String name){
		scanner.matchAndAccept('(');
		scanner.matchAndAccept(')');
		output.outputLine("CALL " + name, true);		
	}
	
	/** Parse and Translate a Function or Variable */
	static void Ident() {
		String name = scanner.getIdentifier();
		if (scanner.matchOnly('(')) {
			Function(name);
		} else {
			testVariableRegistered(name);
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
		scanner.matchAndAccept('-');
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

	/** Recognize and Translate a Multiply */
	static void Multiply() {
		scanner.matchAndAccept('*');
		Factor();
		output.outputLine("POP EBX", true);
		output.outputLine("IMUL EAX,EBX", true);
	}

	/** Recognize and Translate a Divide */
	static void Divide() {
		scanner.matchAndAccept('/');
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

	/** Recognize and Translate an Add */
	static void Add() {
		scanner.matchAndAccept('+');
		Term();
		output.outputLine("POP EBX",true);
		output.outputLine("ADD EAX,EBX", true);
	}

	/** Recognize and Translate a Subtract */
	static void Subtract() {
		scanner.matchAndAccept('-');
		Term();
		output.outputLine("POP EBX", true);
		output.outputLine("SUB EAX,EBX", true);
		output.outputLine("NEG EAX", true);
	}

	/** Parse and Translate a Math Expression */
	static void Expression() {
		Term();
		while (scanner.current == '+' || scanner.current == '-') {
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

	/** Parse and Translate an Assignment Statement */
	static void Assignment(String name) {
		testVariableRegistered(name);
		scanner.matchAndAccept('=');
		Expression();
		output.outputLine("MOV [" + GluonLibrary.varToLabel(name) + "],EAX", true);
	}
	
	/** Parse a variable declaration */
	static void DefineVariable(){
		scanner.skipWhitespace();
		String name = scanner.getIdentifier();
		registerVariable(name);
		
		if (scanner.matchOnly('=')){
			Assignment(name);
		}
	}
	
	/** Parse a statement */
	static void Statement(){
		String name = scanner.getIdentifier();
		if (name.toUpperCase().equals("VAR")) {
			DefineVariable();
		} else if (scanner.matchOnly('(')){
			Function(name);
		} else {
			Assignment(name);
		}		
	}

	/** Main code compiling */
	static void Init() {
		variables = new ArrayList<>();
		output = new GluonOutput();
		
		GluonLibrary.printASMStart(output);
		
		// One statement per line
		while (!scanner.isEOF()){
			Statement();
			scanner.matchAndAccept('\n');
		}

		GluonLibrary.printASMEnd(output);
		GluonLibrary.printVariables(output, variables);
		
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
				Error.abort(scanner, "Source file does not exist.");
			} else {
				scanner = new GluonScanner(source);
			}
		}
		
		Init();
	}
}
