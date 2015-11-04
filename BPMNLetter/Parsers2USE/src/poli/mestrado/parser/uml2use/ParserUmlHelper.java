package poli.mestrado.parser.uml2use;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import poli.mestrado.parser.uml2use.tag.AssociationClassTag;
import poli.mestrado.parser.uml2use.tag.AssociationParticipant;
import poli.mestrado.parser.uml2use.tag.AssociationTag;
import poli.mestrado.parser.uml2use.tag.AttributeTag;
import poli.mestrado.parser.uml2use.tag.ClassTag;
import poli.mestrado.parser.uml2use.tag.DataTypeTag;
import poli.mestrado.parser.uml2use.tag.EnumerationLiteralTag;
import poli.mestrado.parser.uml2use.tag.EnumerationTag;
import poli.mestrado.parser.uml2use.tag.ICommunAttributeTag;
import poli.mestrado.parser.uml2use.tag.IType;
import poli.mestrado.parser.uml2use.tag.ModelTag;
import poli.mestrado.parser.uml2use.tag.MultiplicityTag;
import poli.mestrado.parser.uml2use.tag.OperationTag;
import poli.mestrado.parser.uml2use.tag.ParameterTag;



public class ParserUmlHelper {
	private static ParserUmlHelper instance = null;
	private DocumentBuilder documentBuilde;
	private ModelTag model = null;

	private ParserUmlHelper(){
		try {
			documentBuilde = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}  
	}

	public static ParserUmlHelper getInstance(){
		if(instance == null){
			instance = new ParserUmlHelper();
		}
		return instance;
	}
	
	public void saveModel(){
		UseConceptModelFileManager.getInstance().writeLine(model.toString());
		UseConceptModelFileManager.getInstance().closeFile();
	}

	public ModelTag parserXML2Use(){
		try {
			Document doc = documentBuilde.parse(UmlFileManager.getInstance().getExportXmiFile());  
			doc.getDocumentElement().normalize();  
			NodeList nodeList = doc.getElementsByTagName(ModelTag.TAG_NAME);  
			if(nodeList.getLength() > 0){
				Element modelTag = (Element) nodeList.item(0);
				model = new ModelTag(modelTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_NAME), getEnumerationTagList(modelTag), getClassTagList(modelTag), getAssociationList(modelTag));
				setClassEnumTypeReference();
				setGeneralization(modelTag);
			}
			
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return model; 
	}

	private List<AssociationTag> getAssociationList(Element modelTag) {
		List<AssociationTag> associationList =  new ArrayList<AssociationTag>();

		NodeList associationTagList = modelTag.getElementsByTagName(AssociationTag.TAG_NAME);
		for (int i = 0; i < associationTagList.getLength(); i++) {
			Element associationTag = (Element) associationTagList.item(i);

			//            if(!associationTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_NAME).equals("")){
			associationList.add(new AssociationTag(associationTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_ID),
					associationTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_NAME),
					getAssociationParticipantList(associationTag)));

			//            }

		}


		return associationList;
	}

	private List<EnumerationTag> getEnumerationTagList(Element modelTag){
		List<EnumerationTag> enumerationList =  new ArrayList<EnumerationTag>();
		NodeList enumerationTagList = modelTag.getElementsByTagName(EnumerationTag.TAG_NAME);
		for (int i = 0; i < enumerationTagList.getLength(); i++) {
			Element enumerationTag = (Element) enumerationTagList.item(i);


			if(!enumerationTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_NAME).equals("")){
				enumerationList.add(new EnumerationTag(enumerationTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_ID), 
						enumerationTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_NAME),
						getStereotype(enumerationTag),
						getEnumerationLiteralTag(enumerationTag)
						));

			}

		}

		return enumerationList;
	}

	private List<EnumerationLiteralTag> getEnumerationLiteralTag(Element enumerationTag){
		List<EnumerationLiteralTag> enumerationList =  new ArrayList<EnumerationLiteralTag>();
		NodeList enumerationTagList = enumerationTag.getElementsByTagName(EnumerationLiteralTag.TAG_NAME);
		for (int i = 0; i < enumerationTagList.getLength(); i++) {
			Element eumerationLiteralTag = (Element) enumerationTagList.item(i);


			if(!eumerationLiteralTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_NAME).equals("")){
				enumerationList.add(new EnumerationLiteralTag(eumerationLiteralTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_ID),
						eumerationLiteralTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_NAME)
						));

			}

		}
		return enumerationList;
	}

	private String getStereotype(Element enumerationTag){
		String stereotype = "";

		NodeList stereotypeTagList = enumerationTag.getElementsByTagName("UML:Stereotype");
		if(stereotypeTagList.getLength() > 0){
			Element stereotypeTag = (Element) stereotypeTagList.item(0);
			stereotype = stereotypeTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_ID_REF);
		}

		return stereotype;
	}

	private List<ClassTag> getClassTagList(Element modelTag){
		List<ClassTag> classList =  new ArrayList<ClassTag>();
		NodeList classTagList = modelTag.getElementsByTagName(ClassTag.TAG_NAME);
		for (int i = 0; i < classTagList.getLength(); i++) {
			Element classTag = (Element) classTagList.item(i);


			if(!classTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_NAME).equals("")){
				classList.add(new ClassTag(classTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_ID), 
						classTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_NAME),
						classTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_VISIBILITY),
						new Boolean(classTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_IS_ABSTRACT)),
						getAttributeTagsList(classTag),
						getOperationTagList(classTag)

						));

			}

		}
		classList.addAll(getAssociantionClassTag(modelTag));

		return classList;

	}

	private List<AssociationClassTag> getAssociantionClassTag(Element modelTag){
		List<AssociationClassTag> AssociationClassList =  new ArrayList<AssociationClassTag>();
		NodeList associationClassTagList = modelTag.getElementsByTagName(AssociationClassTag.TAG_NAME);
		for (int i = 0; i < associationClassTagList.getLength(); i++) {
			Element associationClassTag = (Element) associationClassTagList.item(i);


			if(!associationClassTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_NAME).equals("")){
				AssociationClassList.add(new AssociationClassTag(associationClassTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_ID), 
						associationClassTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_NAME),
						associationClassTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_VISIBILITY),
						new Boolean(associationClassTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_IS_ABSTRACT)),
						getAttributeTagsList(associationClassTag),
						getOperationTagList(associationClassTag),
						getAssociationParticipantList(associationClassTag)

						));

			}

		}
		return AssociationClassList;

	}

	private List<AssociationParticipant> getAssociationParticipantList(Element associationClassTag) {
		List<AssociationParticipant> associationParticipantList =  new ArrayList<AssociationParticipant>();

		NodeList associationParticipantsTagList = associationClassTag.getElementsByTagName(AssociationParticipant.TAG_NAME);
		for (int i = 0; i < associationParticipantsTagList.getLength(); i++) {
			Element associationParticipantTag = (Element) associationParticipantsTagList.item(i);

			associationParticipantList.add(new AssociationParticipant(associationParticipantTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_ID), 
					associationParticipantTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_NAME),
					associationParticipantTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_VISIBILITY),
					getMutiplicityTag(associationParticipantTag), 
					getType(associationParticipantTag, AssociationTag.ASSOCIATIONEND_PARTICIPANT),
					associationParticipantTag.getAttribute(AssociationTag.AGGREGATION_ATTRIBUT)
					));

		}
		return associationParticipantList;              
	}


	private List<AttributeTag> getAttributeTagsList(Element classTag){
		List<AttributeTag> attributeList =  new ArrayList<AttributeTag>();

		NodeList attributeTagList = classTag.getElementsByTagName(AttributeTag.TAG_NAME);
		for (int i = 0; i < attributeTagList.getLength(); i++) {
			Element attributeTag = (Element) attributeTagList.item(i);

			attributeList.add(new AttributeTag(attributeTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_ID),
					attributeTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_NAME),
					attributeTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_VISIBILITY),
					new Boolean(attributeTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_IS_ABSTRACT)),
					getType(attributeTag, IType.STRUTURAL_FEATURE_TYPE_TAG),
					getMutiplicityTag(attributeTag)));

		}
		return attributeList;
	}

	private MultiplicityTag getMutiplicityTag(Element attributeTag){

		NodeList multiplicityTagList = attributeTag.getElementsByTagName(MultiplicityTag.TAG_NAME);
		if(multiplicityTagList.getLength() > 0){
			Element multiplicityTag = (Element) multiplicityTagList.item(0);
			return new MultiplicityTag(new Integer(multiplicityTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_LOWER)),
					new Integer(multiplicityTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_UPPER)));

		}else{
			return new MultiplicityTag(1,1);

		}

	}

	private IType getType(Element attributeTag, String nameTag){
		NodeList estruturalFeatureTagList = attributeTag.getElementsByTagName(nameTag);
		if(estruturalFeatureTagList.getLength() > 0){
			Element estruturalFeatureTag = (Element) estruturalFeatureTagList.item(0);
			for (int i = 0; i < estruturalFeatureTag.getChildNodes().getLength(); i++) {
				if(estruturalFeatureTag.getChildNodes().item(i).getNodeType() == Element.ELEMENT_NODE){
					Element typeTag = (Element) estruturalFeatureTag.getChildNodes().item(i);
					if(typeTag.getNodeName().equalsIgnoreCase(IType.DATA_TYPE_TAG)){
						return new DataTypeTag(typeTag.getAttribute(IType.ATTRIBUTE_TAG_HREF));
					}else if(typeTag.getNodeName().equalsIgnoreCase(IType.ENUMERATION_TAG)){
						String idRef = typeTag.getAttribute(IType.ATTRIBUTE_TAG_HREF);
						if(!idRef.equalsIgnoreCase("")){
							return new DataTypeTag(idRef);
						}
						idRef = typeTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_ID_REF);
						if(!idRef.equalsIgnoreCase("")){
							return new EnumerationTag(typeTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_ID_REF));
						}
					}else if(typeTag.getNodeName().equalsIgnoreCase(ClassTag.TAG_NAME) || typeTag.getNodeName().equalsIgnoreCase(AssociationClassTag.TAG_NAME)){
						String classID = typeTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_ID);
						if(!classID.equals("")){
							return new ClassTag(classID);
						}
						classID = typeTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_ID_REF);
						if(!classID.equals("")){
							return new ClassTag(classID);
						}
					}

					break;
				}
			}
		}
		return null;
	}

	private List<OperationTag> getOperationTagList(Element classTag){
		List<OperationTag> operationList =  new ArrayList<OperationTag>();

		NodeList operationTagList = classTag.getElementsByTagName(OperationTag.TAG_NAME);
		for (int i = 0; i < operationTagList.getLength(); i++) {
			Element operationTag = (Element) operationTagList.item(i);

			operationList.add(new OperationTag(operationTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_ID),
					operationTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_NAME),
					operationTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_VISIBILITY),
					getParametersList(operationTag)));
		}

		return operationList;

	}

	private List<ParameterTag> getParametersList(Element operationTag){
		List<ParameterTag> parametersList =  new ArrayList<ParameterTag>();

		NodeList parametersTagList = operationTag.getElementsByTagName(ParameterTag.TAG_NAME);
		for (int i = 0; i < parametersTagList.getLength(); i++) {
			Element parameterTag = (Element) parametersTagList.item(i);

			parametersList.add(new ParameterTag(parameterTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_ID),
					parameterTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_KIND), 
					parameterTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_NAME), 
					getType(parameterTag, IType.PARAMETER_TYPE_TAG)));

		}


		return parametersList;

	}

	private ClassTag findClass(String id){
		for (ClassTag classTag : model.getClassList()) {
			if(classTag.getId().equalsIgnoreCase(id)){
				return classTag;
			}
		}
		return null;
	}

	private EnumerationTag findEnum(String id){
		if(!model.getEnumList().isEmpty()){
			for (EnumerationTag enumTag : model.getEnumList()) {
				if(enumTag.getId().equalsIgnoreCase(id)){
					return enumTag;
				}
			}
		}
		return null;
	}

	private void setClassEnumTypeReference(){
		for (ClassTag classTag : model.getClassList()) {
			for (AttributeTag attributeTag : classTag.getAttributesList()) {
				if(attributeTag.getType() instanceof ClassTag){
					attributeTag.setType(findClass(attributeTag.getType().getIdRef()));
				}else if(attributeTag.getType() instanceof EnumerationTag){
					attributeTag.setType(findEnum(attributeTag.getType().getIdRef()));
				}
			}
			for (OperationTag operationTag : classTag.getOperationlist()) {
				for (ParameterTag parameterTag : operationTag.getParameterList()) {
					if(parameterTag.getType() != null && parameterTag.getType() instanceof ClassTag){
						parameterTag.setType(findClass(parameterTag.getType().getIdRef()));
					}else if(parameterTag.getType() != null && parameterTag.getType() instanceof EnumerationTag){
						parameterTag.setType(findEnum(parameterTag.getType().getIdRef()));
					}
				}
			}
			if(classTag  instanceof AssociationClassTag){
				AssociationClassTag associationClassTag = (AssociationClassTag) classTag;
				for (AssociationParticipant associationParticipant : associationClassTag.getAssociationParticipantsLis()) {
					if(associationParticipant.getType() != null && associationParticipant.getType() instanceof ClassTag){
						associationParticipant.setType(findClass(associationParticipant.getType().getIdRef()));
					}else if(associationParticipant.getType() != null && associationParticipant.getType() instanceof EnumerationTag){
						associationParticipant.setType(findEnum(associationParticipant.getType().getIdRef()));
					}
				}
			}
		}

		for (AssociationTag associationTag : model.getAssociationList()) {
			for (AssociationParticipant associationParticipant : associationTag.getAssociationParticipantsLis()) {
				associationParticipant.setType(findClass(associationParticipant.getType().getIdRef()));
			}
		}
	}

	private void setGeneralization(Element modelTag){
		NodeList gerenalizationTagList = modelTag.getElementsByTagName(ClassTag.GENERALIZATION_TAG);
		for (int i = 0; i < gerenalizationTagList.getLength(); i++) {
			ClassTag subClass = null, superClass = null;

			Element gerenalizationTag = (Element) gerenalizationTagList.item(i);

			NodeList subClassTagList = gerenalizationTag.getElementsByTagName(ClassTag.SUBCLASS_TAG);
			if(subClassTagList.getLength() > 0){
				Element subClassTag = (Element) subClassTagList.item(0);

				String idRef = getClassIdRef(subClassTag);
				if(idRef != null){
					subClass = findClass(idRef);
				}

			}
			NodeList superClassTagList = gerenalizationTag.getElementsByTagName(ClassTag.SUPERCLASS_TAG);
			if(superClassTagList.getLength() > 0){
				Element superClassTag = (Element) superClassTagList.item(0);

				String idRef = getClassIdRef(superClassTag);
				if(idRef != null){
					superClass = findClass(idRef);
				}

			}

			if(superClass != null && subClass != null){
				subClass.addSuperClassList(superClass);
			}
		}
	}


	private String getClassIdRef(Element parentClassTAg){
		NodeList classTagList = parentClassTAg.getElementsByTagName(ClassTag.TAG_NAME);
		if(classTagList.getLength()>0){
			Element classTag = (Element) classTagList.item(0);
			return classTag.getAttribute(ICommunAttributeTag.ATTRIBUTE_TAG_ID_REF);
		}
		return null;
	}



}
