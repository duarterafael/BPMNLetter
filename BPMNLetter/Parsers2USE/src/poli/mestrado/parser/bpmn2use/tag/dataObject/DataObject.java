package poli.mestrado.parser.bpmn2use.tag.dataObject;

import java.util.List;

import poli.mestrado.parser.bpmn2use.tag.AbstractBaseElement;
import poli.mestrado.parser.bpmn2use.tag.Documentation;

@SuppressWarnings("serial")
public  class DataObject extends AbstractBaseElement  {
	
	public static final String 	TAG_NAME = "dataObject";
	public static final String IOSPECIFICATION_TAG = "ioSpecification";
	public static final String ISCOLLECTION_ATTRIBUTE = "isCollection";
	public static final String DATASTATE_TAG = "dataState";
	
	public static final String DATAINPUTASSOCIATION_TAG = "dataInputAssociation";
	public static final String DATAOUTPUTASSOCIATION_TAG = "dataOutputAssociation";
	
	private String itemSubjectRef;
	
	
	private Boolean isCollection;
	private String state;
	
	public DataObject(String id, String name, List<Documentation> documentationList, Boolean isCollection,  String state, String itemSubjectRef) {
		super(id, name, documentationList);
		this.isCollection = isCollection;
		this.state =  state;
		this.itemSubjectRef = itemSubjectRef;
	}
	
	public String getState() {
		return state;
	}

	public Boolean getIsCollection() {
		return isCollection;
	}
	
	public String getItemSubjectRef() {
		return itemSubjectRef;
	}
	
	@Override
	public String toString() {
//		String strItemSubjectRef = this.itemSubjectRef == null ? "" : " itemSubjectRef = "+this.itemSubjectRef;
//		return super.toString()+" Type = "+TAG_NAME+strItemSubjectRef;
		
		String strItemSubjectRef = this.itemSubjectRef == null ? "" : this.itemSubjectRef.substring(this.itemSubjectRef.lastIndexOf(":")+1, this.itemSubjectRef.length());
		return super.getName().replace(" ", "_") +" : "+strItemSubjectRef;
	}


	
	@Override
	public int compareTo(AbstractBaseElement o) {
		if( ((DataObject) o).getName().equals(this.getName()) && ((DataObject) o).getItemSubjectRef().equals(this.getItemSubjectRef()))
			return 0;
		else 
			return 1;
	}
	
	
	
	
	
}
