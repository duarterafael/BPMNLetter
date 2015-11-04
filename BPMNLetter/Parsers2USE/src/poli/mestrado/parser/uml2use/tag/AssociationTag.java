package poli.mestrado.parser.uml2use.tag;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AssociationTag extends BaseTag{
	
	public static final String TAG_NAME = "UML:Association";
	public static final String AGGREGATION_ATTRIBUT = "aggregation";
	public static final String AGGREGATION_ATTRIBUT_VALUE_NONE = "none";
	public static final String ASSOCIATIONEND_PARTICIPANT = "UML:AssociationEnd.participant";
	
	public static final String AGGREGATION_ATTRIBUT_VALUE_AGGREGATION = "aggregate";
	public static final String AGGREGATION_ATTRIBUT_VALUE_COMPOSITE = "composite";
	
	
	
	private List<AssociationParticipant> associationParticipantsLis;
	
	public AssociationTag(String id, String name, List<AssociationParticipant> associationParticipantsLis) {
		super(id, name);
		this.associationParticipantsLis =  new ArrayList<AssociationParticipant>();
		this.associationParticipantsLis.addAll(associationParticipantsLis);
	}
	
	public List<AssociationParticipant> getAssociationParticipantsLis() {
		return associationParticipantsLis;
	}
	public void addAssociationParticipantsLis( AssociationParticipant associationParticipant) {
		this.associationParticipantsLis.add(associationParticipant);
	}
	public String getKindAssociation(){
		for (AssociationParticipant associationParticipant : associationParticipantsLis) {
		        if(associationParticipant.getAggregation().equalsIgnoreCase(AGGREGATION_ATTRIBUT_VALUE_AGGREGATION))
		            return "aggregation";
		        else if(associationParticipant.getAggregation().equalsIgnoreCase(AGGREGATION_ATTRIBUT_VALUE_COMPOSITE))
                            return "composition";
		}
		return "association";		
	}
	
	@Override
	public String toString() {
		String associtationStr = "";
		String assiciationParticipantStr = "";
		String fakeName = "";
		
		if(!associationParticipantsLis.isEmpty()){
		    for (AssociationParticipant associationParticipant : associationParticipantsLis) {
		        assiciationParticipantStr += associationParticipant.toString();
		        if(associationParticipant.getType() != null){
		        	fakeName += "_"+associationParticipant.getType().getName();
		        }
		    }
		}
		
		fakeName = getKindAssociation()+fakeName;
		
		if(!super.getName().equalsIgnoreCase("")){
		    associtationStr += getKindAssociation()+" "+super.getName()+" \nbetween \n\n";
		}else{
		    associtationStr += getKindAssociation()+" "+fakeName+" \nbetween \n\n";
		}
		
		
		associtationStr += assiciationParticipantStr+"\nend\n";
				
		return associtationStr;
	}
	
	
	
	
	
	

}

