package poli.mestrado.parser.bpmn2use.tag.dataObject;


public class DataObjectFactory {
	public static DataObject buildDataObject(String typeTask, String id, String name, Boolean isCollection, String state, String itemSubjectRef) {
		DataObject dataObject = null;
		switch (typeTask) {
		case DataObject.TAG_NAME:
			dataObject = new DataObject(id, name, isCollection, state, itemSubjectRef);
			break;
		case DataInput.TAG_NAME:
			dataObject = new DataInput(id, name, isCollection, state, itemSubjectRef);
			break;
		case DataOutput.TAG_NAME:
			dataObject = new DataOutput(id, name, isCollection, state, itemSubjectRef);
			break;
		}
		return dataObject;
    }

}
