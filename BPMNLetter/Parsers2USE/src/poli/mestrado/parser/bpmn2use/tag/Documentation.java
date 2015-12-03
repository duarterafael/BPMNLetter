package poli.mestrado.parser.bpmn2use.tag;

import java.io.Serializable;

public class Documentation implements Serializable {
	public static final String TAG_NAME = "documentation";
	
	
	private String id;
	private String content;
	
	public Documentation(String id, String content) {
		super();
		this.id = id;
		this.content = content;
//				(String) contents.subSequence(contents.indexOf("<![CDATA[")+"<![CDATA[".length(), contents.indexOf("]]>"));;
		
	}

	public String getId() {
		return id;
	}


	public String getContent() {
		return content;
	}

	
	
	

}
