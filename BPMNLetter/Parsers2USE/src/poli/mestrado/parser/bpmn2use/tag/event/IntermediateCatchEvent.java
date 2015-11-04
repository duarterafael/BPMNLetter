package poli.mestrado.parser.bpmn2use.tag.event;

import poli.mestrado.parser.bpmn2use.tag.AbstractBaseElement;

public class IntermediateCatchEvent extends AbstractBaseElement {
	
	public static final String TAG_NAME = "intermediateCatchEvent";
	public static final String PARALLELMULTIPLE_TAG = "parallelMultiple";
	
	private Boolean parallelMultiple;
	
	public IntermediateCatchEvent(String id, String name, Boolean parallelMultiple) {
		super(id, name);
		this.parallelMultiple = parallelMultiple;
	}

	public Boolean getParallelMultiple() {
		return parallelMultiple;
	}
	
	@Override
	public String toString() {
		return super.toString()+" parallelMultiple = "+parallelMultiple;
	}
	
	
	

}
