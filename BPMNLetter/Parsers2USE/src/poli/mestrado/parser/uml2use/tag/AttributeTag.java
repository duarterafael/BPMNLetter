package poli.mestrado.parser.uml2use.tag;

public class AttributeTag extends AbstactFlowElementTag{

	private MultiplicityTag multiplicity;
	private IType type;
	public static final String TAG_NAME = "UML:Attribute";
	public String valuesInterval;

	public AttributeTag(String id, String name, String visibility,Boolean isAbstract, IType type, MultiplicityTag multiplicityTag) {
		super(id, name, visibility, isAbstract);
		this.multiplicity = multiplicityTag;
		this.type = type;
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

	@Override
	public String toString() {
		return "\t"+super.getName() +" : "+ type.getName();
	}


	public String getValuesInterval() {
		return valuesInterval;
	}


	public void setValuesInterval(String valuesInterval) {
		this.valuesInterval = valuesInterval;
	}




}
