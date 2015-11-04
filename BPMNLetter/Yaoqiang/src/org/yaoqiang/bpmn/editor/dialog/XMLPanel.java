package org.yaoqiang.bpmn.editor.dialog;

import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.swing.BPMNGraphComponent;
import org.yaoqiang.bpmn.model.elements.XMLCollection;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.XMLExtensionElement;

/**
 * XMLPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class XMLPanel extends Panel {

	private static final long serialVersionUID = 1L;

	public XMLPanel(PanelContainer pc, XMLElement owner) {
		super(pc, owner);
	}

	public void saveObjects() {
		Panel parentPanel = getParentPanel();
		if (parentPanel != null && !(owner instanceof XMLCollection)) {
			if (getOwner().getParent() instanceof XMLCollection) {
				((XMLCollection) getOwner().getParent()).add(getOwner());
			} else if (getOwner().getParent() instanceof XMLExtensionElement) {
				XMLExtensionElement parent = (XMLExtensionElement) getOwner().getParent();
				if (!parent.contains((XMLExtensionElement) owner)) {
					parent.addChildElement((XMLExtensionElement) owner);
				}
			}
			((XMLTablePanel) parentPanel).addRow(getOwner());
		}
	}

	public XMLElement getOwner() {
		return (XMLElement) owner;
	}

	public BPMNGraphComponent getGraphComponent() {
		return BPMNEditor.getInstance().getGraphComponent();
	}

}
