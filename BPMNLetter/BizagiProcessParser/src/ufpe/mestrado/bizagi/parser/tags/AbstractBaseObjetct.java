package ufpe.mestrado.bizagi.parser.tags;

import java.io.Serializable;


@SuppressWarnings("serial")
public abstract class AbstractBaseObjetct implements Serializable, Comparable <AbstractBaseObjetct>{
	
	
	private String id;
	
	
	public AbstractBaseObjetct(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	
	@Override
	public String toString() {
		return "ID = "+id;
	}
	
	@Override
	public int compareTo(AbstractBaseObjetct o) {
		return o.getId().compareTo((this.getId()));
	}
	
	
	
}
