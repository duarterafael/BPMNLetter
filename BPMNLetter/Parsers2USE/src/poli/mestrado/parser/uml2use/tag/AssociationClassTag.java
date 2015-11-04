package poli.mestrado.parser.uml2use.tag;

import java.util.ArrayList;
import java.util.List;

public class AssociationClassTag extends ClassTag{
	
	public static final String TAG_NAME = "UML:AssociationClass";
	private List<AssociationParticipant> associationParticipantsLis;

	public AssociationClassTag(String id, String name, String visibility,	Boolean isAbstract, List<AttributeTag> attributesList,	List<OperationTag> operationlist, List<AssociationParticipant> associationParticipantsLis) {
		super(id, name, visibility, isAbstract, attributesList, operationlist);
		this.associationParticipantsLis = new ArrayList<AssociationParticipant>(associationParticipantsLis);
	}
	
	public List<AssociationParticipant> getAssociationParticipantsLis() {
		return associationParticipantsLis;
	}

	public void setAssociationParticipantsLis(
			List<AssociationParticipant> associationParticipantsLis) {
		this.associationParticipantsLis = associationParticipantsLis;
	}

	@Override
	public String toString() {
	String associationClassStr = "";
		
		if(super.getIsAbstract()){
			associationClassStr+= "abstract ";
		}

		associationClassStr += "associationclass "+super.getName()+"\n\nbetween\n\n";
		
		if(!associationParticipantsLis.isEmpty()){
			for (AssociationParticipant associationParticipant : associationParticipantsLis) {
				associationClassStr += associationParticipant.toString();
			}
		}
		

		if(!super.getAttributesList().isEmpty()){
			associationClassStr += "\nattributes\n";
			for (AttributeTag attributeTag : super.getAttributesList()) {
				associationClassStr += "\n"+ attributeTag.toString();
			}
		}

		if(!super.getOperationlist().isEmpty()){
			associationClassStr += "\n\noperations\n";
			for (OperationTag operationTag : super.getOperationlist()) {
				associationClassStr += "\n"+ operationTag.toString();
			}
		}

		associationClassStr += "\n\nend\n";

		return associationClassStr;
	}

	
	
	

}
