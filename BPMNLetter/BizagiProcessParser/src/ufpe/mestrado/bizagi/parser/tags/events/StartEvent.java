package ufpe.mestrado.bizagi.parser.tags.events;

import ufpe.mestrado.bizagi.parser.tags.AbstractFlowObject;

public class StartEvent extends AbstractFlowObject {
	
	public static final String TAG_NAME = "startEvent";
	public static final String ISINTERRUPTIOG_TAG = "isInterrupting";
	public static final String PARALLELMULTIPLE_TAG = "parallelMultiple";
	
	private Boolean isInterrupting;
	private Boolean parallelMultiple;
	
	public StartEvent(String id, String name, Boolean isInterrupting,
			Boolean parallelMultiple) {
		super(id, name);
		this.isInterrupting = isInterrupting;
		this.parallelMultiple = parallelMultiple;
	}

	public Boolean getIsInterrupting() {
		return isInterrupting;
	}

	public Boolean getParallelMultiple() {
		return parallelMultiple;
	}
	
	@Override
	public String toString() {
		return super.toString()+ " isInterrupting = "+isInterrupting+" parallelMultiple = "+parallelMultiple;
	}
	
	
	

}
