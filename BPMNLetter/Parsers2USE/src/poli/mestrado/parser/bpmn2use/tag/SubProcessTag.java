package poli.mestrado.parser.bpmn2use.tag;

import java.util.List;

public class SubProcessTag extends AbstractBaseElement{

	public static final String 	TAG_NAME = "subProcess";
	public static final String 	COMPLETION_QUANTITY = "completionQuantity";
	public static final String 	IS_FORCOMPENSATION = "isForCompensation";
	public static final String 	START_QUANTITY = "startQuantity";
	public static final String 	TRIGGERED_BY_EVENT = "triggeredByEvent";
	
	private Integer completionQuantity;
	private Boolean isForCompensation;
	private Integer startQuantity;
	private Boolean triggeredByEvent;
	
	public SubProcessTag(String id, String name, List<Documentation> documentationList, Integer completionQuantity,
			Boolean isForCompensation, Integer startQuantity,
			Boolean triggeredByEvent) {
		super(id, name, documentationList);
		this.completionQuantity = completionQuantity;
		this.isForCompensation = isForCompensation;
		this.startQuantity = startQuantity;
		this.triggeredByEvent = triggeredByEvent;
	}

	public Integer getCompletionQuantity() {
		return completionQuantity;
	}

	public Boolean getIsForCompensation() {
		return isForCompensation;
	}

	public Integer getStartQuantity() {
		return startQuantity;
	}

	public Boolean getTriggeredByEvent() {
		return triggeredByEvent;
	}
	
	
	
	
	
	
	
	


}
