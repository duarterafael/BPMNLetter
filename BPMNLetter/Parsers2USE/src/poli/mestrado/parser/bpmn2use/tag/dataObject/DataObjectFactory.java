package poli.mestrado.parser.bpmn2use.tag.dataObject;

import java.util.List;

import poli.mestrado.parser.bpmn2use.tag.Documentation;


public class DataObjectFactory {
	public static DataObject buildDataObject(String typeTask, String id, String name, List<Documentation> documentationList, Boolean isCollection, String state, String itemSubjectRef) {
		DataObject dataObject = null;
		switch (typeTask) {
		case DataObject.TAG_NAME:
			dataObject = new DataObject(id, name, documentationList, isCollection, state, itemSubjectRef);
			break;
		case DataInput.TAG_NAME:
			dataObject = new DataInput(id, name, documentationList, isCollection, state, itemSubjectRef);
			break;
		case DataOutput.TAG_NAME:
			dataObject = new DataOutput(id, name, documentationList, isCollection, state, itemSubjectRef);
			break;
		}
		return dataObject;
    }

}
