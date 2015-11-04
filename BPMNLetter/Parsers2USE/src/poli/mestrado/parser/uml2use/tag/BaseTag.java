package poli.mestrado.parser.uml2use.tag;

import java.io.Serializable;


public abstract class BaseTag implements Serializable{
	private String id;
	private String name;
	
	
	public BaseTag(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
