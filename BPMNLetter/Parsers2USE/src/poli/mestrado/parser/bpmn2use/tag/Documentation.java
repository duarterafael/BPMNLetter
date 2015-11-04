package poli.mestrado.parser.bpmn2use.tag;

import java.io.Serializable;

public class Documentation implements Serializable {
	public static final String TAG_NAME = "documentation";
	
	
	private String id;
	private String contents;
	
	public Documentation(String id, String contents) {
		super();
		this.id = id;
		this.contents = contents; 
//		(String) contents.subSequence(contents.indexOf("<![CDATA[")+"<![CDATA[".length(), contents.indexOf("]]>"));;
		
	}

	public String getId() {
		return id;
	}


	public String getContents() {
		return contents;
	}

	
	
	

}
