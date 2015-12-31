package poli.mestrado.parser.bpmn2use.tag.event;

import java.util.List;

import poli.mestrado.parser.bpmn2use.tag.AbstractBaseElement;
import poli.mestrado.parser.bpmn2use.tag.Documentation;
import poli.mestrado.parser.bpmn2use.tag.event.definition.EventDefinition;

public class StartEvent extends AbstractEventElement {
	
	public static final String TAG_NAME = "startEvent";
	public static final String ISINTERRUPTIOG_TAG = "isInterrupting";
	public static final String PARALLELMULTIPLE_TAG = "parallelMultiple";
	
	private Boolean isInterrupting;
	private Boolean parallelMultiple;
	
	public StartEvent(String id, String name, List<Documentation> documentationList, Boolean isInterrupting,
			Boolean parallelMultiple, EnumEventElementType eventElementType, EventDefinition eventDefinition) {
		super(id, name, documentationList, eventElementType, eventDefinition);
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
