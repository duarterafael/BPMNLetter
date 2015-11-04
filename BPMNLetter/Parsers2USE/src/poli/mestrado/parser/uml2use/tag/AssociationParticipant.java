package poli.mestrado.parser.uml2use.tag;

public class AssociationParticipant extends BaseTag{
	
	public static final String TAG_NAME = "UML:AssociationEnd";
	public static final String TAG_TYPE = "UML:AssociationEnd.participant";
	private String visibility;
	private MultiplicityTag multiplicity;
	private IType type;
	private String aggregation;
	
	
	
	
	public AssociationParticipant(String id, String name, String visibility,
			MultiplicityTag multiplicity, IType type, String aggregation) {
		super(id, name);
		this.visibility = visibility;
		this.multiplicity = multiplicity;
		this.type = type;
		this.aggregation = aggregation;
	}
	public String getVisibility() {
		return visibility;
	}
	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}
	public MultiplicityTag getMultiplicity() {
		return multiplicity;
	}
	public void setMultiplicity(MultiplicityTag multiplicity) {
		this.multiplicity = multiplicity;
	}
	public IType getType() {
		return type;
	}
	public void setType(IType type) {
		this.type = type;
	}
	
	public String getAggregation() {
		return aggregation;
	}
	public void setAggregation(String aggregation) {
		this.aggregation = aggregation;
	}
	
	@Override
	public String toString() {
		String associationParticipantStr = "";
		
		
		String name = type == null?"":type.getName();
		
		associationParticipantStr += "\t"+name+" "+multiplicity.toString();
		
		if(!super.getName().equalsIgnoreCase("")){
			associationParticipantStr += " role "+ super.getName();
		}
		
		associationParticipantStr += "\n";
		
		return associationParticipantStr;
	}
	
	
}

