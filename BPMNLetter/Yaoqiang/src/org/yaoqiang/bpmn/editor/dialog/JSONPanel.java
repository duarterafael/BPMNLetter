package org.yaoqiang.bpmn.editor.dialog;

import org.json.JSONObject;

/**
 * JSONPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class JSONPanel extends Panel {

	private static final long serialVersionUID = 1L;

	public JSONPanel(PanelContainer pc, Object owner) {
		super(pc, owner);
	}

	public void saveObjects() {
		Panel parentPanel = getParentPanel();
		if (parentPanel != null) {
			((JSONTablePanel) parentPanel).addRow(getOwner());
		}
	}

	public JSONObject getOwner() {
		return (JSONObject) owner;
	}

}
