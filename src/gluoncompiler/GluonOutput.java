package gluoncompiler;

/**
 * Output class to hold output ready to be written to a file.
 */
public class GluonOutput {
	private StringBuilder output;

	public GluonOutput(){
		output = new StringBuilder();
	}

	public void outputLine(String codeLine, boolean prependTab){
		if (prependTab)
			output.append(String.format("\t%s\n",codeLine));
		else
			output.append(String.format("%s\n",codeLine));
	}
	
	public static String codeLine(String code){
		return "\t" + code + "\n";
	}
	
	public void code(String code){
		output.append("\t");
		output.append(code);
		output.append("\n");
	}
	
	public static String commentLine(String comment){
		return ";" + comment + "\n";
	}
	
	public void comment(String comment){
		output.append(";");
		output.append(comment);
		output.append("\n");
	}
	
	public static String labelLine(String label){
		return label + ":\n";
	}
	
	public void label(String label){
		output.append(label);
		output.append(":\n");
	}

	public void append(String str){
		output.append(str);
	}
	
	/**
	 * @return Return the output code 
	 */
	public String getOutput(){
		return output.toString();
	}

	public void setOutput(StringBuilder output){
		this.output = output;
	}
}
