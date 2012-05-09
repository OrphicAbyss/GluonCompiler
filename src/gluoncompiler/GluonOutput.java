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

	public String getOutput(){
		return output.toString();
	}

	public void setOutput(StringBuilder output){
		this.output = output;
	}
}
