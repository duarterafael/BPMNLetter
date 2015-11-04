package poli.mestrado.parser.uml2use.tag;

import java.io.Serializable;

public class ParameterTag implements Serializable{
	private String id;
	private String kind;
	private String name;
	private IType type;
	public static final String TAG_NAME = "UML:Parameter";
	
	
	
	public ParameterTag(String id, String kind, String name, IType type) {
		super();
		this.id = id;
		this.kind = kind;
		this.name = name;
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public IType getType() {
		return type;
	}
	public void setType(IType type) {
		this.type = type;
	}
	
	
	

}
