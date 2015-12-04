package poli.mestrado.parser.bpmn2use.tag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("serial")
public abstract class AbstractBaseElement implements Serializable, Comparable <AbstractBaseElement>{
	
	
	private String id;
	private String name;
	private List<Documentation> documentationList; 
	
	public AbstractBaseElement(String id, String name,  List<Documentation> documentations) {
		this.id = id;
		this.name = name;
		if(documentations != null){
			this.documentationList = new ArrayList<Documentation>();
			this.documentationList.addAll(documentations);
		}
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		String str = name.replace("\n", "");
		return str;
	}
	public void setName(String name) {
		this.name = name;
	}

	public void setDocumentation(List<Documentation> documentation) {
		this.documentationList = documentation;
	}

	@Override
	public String toString() {
		String strDocumentation = "";
		if(documentationList != null){
			for (Documentation documentation2 : documentationList) {
				strDocumentation+="Documentation ("+documentation2.getId()+") Contente:"+documentation2.getContent();
			} 
		}
		return "Type = "+this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".")+1)+" ID = "+id+" NAME = "+name +" "+strDocumentation;
	}
	
	@Override
	public int compareTo(AbstractBaseElement o) {
		return o.getId().compareTo((this.getId()));
	}
	
	
	
}
