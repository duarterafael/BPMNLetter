package poli.mestrado.parser.uml2use.tag;

import java.util.ArrayList;
import java.util.List;

public class ClassTag extends AbstactFlowElementTag implements IType{
	private List<ClassTag> superClassList;
	private List<AttributeTag> attributesList;
	private List<OperationTag> operationlist;
	public static final String TAG_NAME = "UML:Class";
	public static final String GENERALIZATION_TAG = "UML:Generalization";
	public static final String SUBCLASS_TAG = "UML:Generalization.child";
	public static final String SUPERCLASS_TAG = "UML:Generalization.parent";


	public ClassTag(String id){
		super(id, null, null, null);
	}
	
	public ClassTag(String id, String name, String visibility,Boolean isAbstract, List<AttributeTag> attributesList, List<OperationTag> operationlist) {
		super(id, name, visibility, isAbstract);
		this.attributesList = new ArrayList<AttributeTag>();
		if(attributesList != null){
			this.attributesList.addAll(attributesList);
		}
		this.operationlist = new ArrayList<OperationTag>();
		if(operationlist != null){
			this.operationlist.addAll(operationlist);
		}
		this.superClassList = new ArrayList<ClassTag>();

	}
	
	public void addSuperClassList(ClassTag superClass) {
		this.superClassList.add(superClass);
	}
	
	public List<AttributeTag> getAttributesList() {
		return attributesList;
	}

	public void addAttribute(AttributeTag attribute) {
		this.attributesList.add(attribute);
	}

	public List<OperationTag> getOperationlist() {
		return operationlist;
	}

	public void addOperation(OperationTag operation) {
		this.operationlist.add(operation);
	}
	
	@Override
	public String getIdRef() {
		return super.getId();
	}
	

	public List<ClassTag> getSuperClassList() {
		return superClassList;
	}


	@Override
	public String toString() {
		String classStr = "";
		String superClassList = "";
		
		if(super.getIsAbstract()){
			classStr+= "abstract ";
		}

		classStr += "class "+super.getName();
		
		if(!this.superClassList.isEmpty()){
			classStr +=" < ";
			for (ClassTag classTag : this.superClassList) {
				superClassList += classTag.getName()+", ";
			}
			classStr +=superClassList.substring(0, superClassList.length()-2);
		}

		classStr +="\n";	
			
		if(!attributesList.isEmpty()){
			classStr += "\nattributes\n";
			for (AttributeTag attributeTag : attributesList) {
				classStr += "\n"+ attributeTag.toString();
			}
		}

		if(!operationlist.isEmpty()){
			classStr += "\n\noperations\n";
			for (OperationTag operationTag : operationlist) {
				classStr += "\n"+ operationTag.toString();
			}
		}

		classStr += "\n\nend\n";

		return classStr;
	}

	





}
