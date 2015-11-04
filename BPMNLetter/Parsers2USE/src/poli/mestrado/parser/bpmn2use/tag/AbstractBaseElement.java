package poli.mestrado.parser.bpmn2use.tag;

import java.io.Serializable;


@SuppressWarnings("serial")
public abstract class AbstractBaseElement implements Serializable, Comparable <AbstractBaseElement>{
	
	
	private String id;
	private String name;
	
	
	public AbstractBaseElement(String id, String name) {
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
	
	@Override
	public String toString() {
		return "Type = "+this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".")+1)+" ID = "+id+" NAME = "+name;
	}
	
	@Override
	public int compareTo(AbstractBaseElement o) {
		return o.getId().compareTo((this.getId()));
	}
	
	
	
}
