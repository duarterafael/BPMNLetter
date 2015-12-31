package poli.mestrado.parser.bpmn2use.tag.connectioElement;

import poli.mestrado.parser.bpmn2use.tag.AbstractBaseElement;

public abstract class AbstractConnectionElement {
	
	public static final String ATTRIBUTE_TAG_SOURCE_REF = "sourceRef";
	public static final String ATTRIBUTE_TAG_TARGET_REF = "targetRef";
	
	
	private String id;
	private AbstractBaseElement sourceRef;
	private AbstractBaseElement targetRef;
	
	public AbstractConnectionElement(String id, AbstractBaseElement sourceRef,
			AbstractBaseElement targetRef) {
		super();
		this.id = id;
		this.sourceRef = sourceRef;
		this.targetRef = targetRef;
	}
	
	public String getId() {
		return id;
	}

	public AbstractBaseElement getSourceRef() {
		return sourceRef;
	}

	public AbstractBaseElement getTargetRef() {
		return targetRef;
	}
	
	@Override
	public String toString() {
		return "ID = "+ id + " sourceRef = "+sourceRef.getId() + " targetRef =  " + targetRef.getId() ;
	}	
	

}
