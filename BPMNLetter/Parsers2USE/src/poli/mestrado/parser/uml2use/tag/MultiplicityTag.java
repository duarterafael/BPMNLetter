package poli.mestrado.parser.uml2use.tag;

import java.io.Serializable;

public class MultiplicityTag implements Serializable{
	private int lower, upper;
	public static final String TAG_NAME = "UML:MultiplicityRange";
	
	public MultiplicityTag(int lower, int upper) {
		this.lower = lower;
		this.upper = upper;
	}

	public int getLower() {
		return lower;
	}

	public void setLower(int lower) {
		this.lower = lower;
	}

	public int getUpper() {
		return upper;
	}

	public void setUpper(int upper) {
		this.upper = upper;
	}
	
	@Override
	public String toString() {
		return "["+lower+".."+(upper==-1?"*":upper)+"]";
	}
	

}
