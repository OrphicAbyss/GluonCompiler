package simplecompiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Simple compiler for language code named 'Gluon'
 */
public class SimpleCompiler {
	static ArrayList<String> variables;

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
			Error.abort("Variable used before assigned to: " + name);
		}
	}
	
	static String varToLabel(String varName){
		return "var_"+varName;
	}
	
	static String funcToLabel(String funcName){
		return "func_"+funcName;
	}
	
	/**
	 * Test if the next token is an identifier. If it is, build the
	 * identifier out of the stream.
	 *
	 * @return The identifier
	 */
	static String getIdentifier(){
		if (!GluonScanner.isAlpha())
			Error.expected("Identifier");

		StringBuilder ident = new StringBuilder();

		while (GluonScanner.isAlpha() || GluonScanner.isDigit()){
			ident.append(GluonScanner.current);
			GluonScanner.getChar();
		}

		GluonScanner.skipWhitespace();
		return ident.toString();
	}

	/**
	 * Test if the next token is an integer. If it is, build the integer
	 * out of the stream.
	 *
	 * @return The integer found
	 */
	static String getInteger(){
		if (!GluonScanner.isDigit())
			Error.expected("Integer");

		StringBuilder integer = new StringBuilder();
		while (GluonScanner.isDigit()){
			integer.append(GluonScanner.current);
			GluonScanner.getChar();
		}

		GluonScanner.skipWhitespace();
		return integer.toString();
	}

	/*
	 * Parse and Translate Parentheses
	 */
	static void Parentheses() {
		GluonScanner.matchAndAccept('(');
		Expression();
		GluonScanner.matchAndAccept(')');
	}

	static void Function(String name){
		GluonScanner.matchAndAccept('(');
		GluonScanner.matchAndAccept(')');
		GluonOutput.emitLn("CALL " + name, true);		
	}
	
	/*
	 * Parse and Translate a Function or Variable
	 */
	static void Ident() {
		String name = getIdentifier();
		if (GluonScanner.matchOnly('(')) {
			Function(name);
		} else {
			testVariableRegistered(name);
			GluonOutput.emitLn("MOV EAX, [" + varToLabel(name) + "]", true);
		}
	}

	/*
	 * Parse and Translate a Constant or Variable
	 */
	static void ConstVar() {
		if (GluonScanner.isAlpha()) {
			Ident();
		} else {
			GluonOutput.emitLn("MOV EAX, " + getInteger(), true);
		}
	}

	/*
	 * Parse and Translate a Unary Minus
	 */
	static void UnaryMinus() {
		GluonScanner.matchAndAccept('-');
		ConstVar();
		GluonOutput.emitLn("NEG EAX", true);
	}

	/*
	 * Parse and Translate a Math Factor
	 */
	static void Factor() {
		switch (GluonScanner.current) {
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

	/*
	 * Recognize and Translate a Multiply
	 */
	static void Multiply() {
		GluonScanner.matchAndAccept('*');
		Factor();
		GluonOutput.emitLn("POP EBX", true);
		GluonOutput.emitLn("IMUL EAX,EBX", true);
	}

	/*
	 * Recognize and Translate a Divide
	 */
	static void Divide() {
		GluonScanner.matchAndAccept('/');
		Factor();
		GluonOutput.emitLn("MOV EBX, EAX", true);
		GluonOutput.emitLn("MOV EDX, 0", true);
		GluonOutput.emitLn("POP EAX", true);
		GluonOutput.emitLn("IDIV EBX", true);
	}

	/*
	 * Parse and Translate a Math Term
	 */
	static void Term() {
		Factor();
		while (GluonScanner.current == '*' || GluonScanner.current == '/') {
			GluonOutput.emitLn("PUSH EAX", true);
			switch (GluonScanner.current) {
				case '*':
					Multiply();
					break;
				case '/':
					Divide();
					break;
			}
		}
	}

	/*
	 * Recognize and Translate an Add
	 */
	static void Add() {
		GluonScanner.matchAndAccept('+');
		Term();
		GluonOutput.emitLn("POP EBX",true);
		GluonOutput.emitLn("ADD EAX,EBX", true);
	}

	/*
	 * Recognize and Translate a Subtract
	 */
	static void Subtract() {
		GluonScanner.matchAndAccept('-');
		Term();
		GluonOutput.emitLn("POP EBX", true);
		GluonOutput.emitLn("SUB EAX,EBX", true);
		GluonOutput.emitLn("NEG EAX", true);
	}

	/*
	 * Parse and Translate a Math Expression
	 */
	static void Expression() {
		Term();
		while (GluonScanner.current == '+' || GluonScanner.current == '-') {
			GluonOutput.emitLn("PUSH EAX", true);
			switch (GluonScanner.current) {
				case '+':
					Add();
					break;
				case '-':
					Subtract();
					break;
			}
		}
	}

	/*
	 * Parse and Translate an Assignment Statement
	 */
	static void Assignment(String name) {
		testVariableRegistered(name);
		GluonScanner.matchAndAccept('=');
		Expression();
		GluonOutput.emitLn("MOV [" + varToLabel(name) + "],EAX", true);
	}
	
	static void Define(){
		GluonScanner.skipWhitespace();
		String name = getIdentifier();
		registerVariable(name);
	}
	
	static void printVariables(){
		GluonOutput.emitLn("; print all vars", false);
		GluonOutput.emitLn("print:", false);
		GluonOutput.emitLn("MOV BX, 10",true);
		for (String var: variables){
			GluonOutput.emitLn("MOV EAX, [" + varToLabel(var) + "]", true);
			GluonOutput.emitLn("CALL print_number", true);
		}
		GluonOutput.emitLn("RET",true);
		GluonOutput.emitLn("", false);
		GluonOutput.emitLn("; data section", false);
		for (String var: variables){
			GluonOutput.emitLn(varToLabel(var) + "\tdd\t?", true);
		}
	}	
	
	/*
	 * Initialize
	 */
	static void Init() {
		variables = new ArrayList<String>();
		GluonOutput.output = new StringBuilder();
		
		GluonScanner.skipWhitespace();
		GluonLibrary.printASMStart();
		
		while (!GluonScanner.matchOnly('.')){
			String name = getIdentifier();
			if (name.toUpperCase().equals("VAR")) {
				Define();
			} else if (GluonScanner.matchOnly('(')){
				Function(name);
			} else {
				Assignment(name);
			}
			GluonScanner.matchAndAccept('\n');
		}

		GluonLibrary.printASMEnd();
		printVariables();
		
		System.out.print(GluonOutput.getOutput());
	}

	/**
	 * Takes a filename as the main argument.
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		try {
			if (args.length == 0){
				// Print out help
				System.out.println("Gluon Compiler - No filename provided input from terminal:");
				GluonScanner.init();
				Init();
			} else {
				System.out.println("Gluon Compiler - Reading source file: " + args[0]);
				// Make sure the source file exists
				File source = new File(args[0]);
				if (!source.exists()){
					System.out.println("Unable to find file to compile.");
				} else {
					GluonScanner.init(source);
					Init();
				}
			}
		} catch (FileNotFoundException ex) {
			System.out.println("Should not occur.");
		}
	}
}
