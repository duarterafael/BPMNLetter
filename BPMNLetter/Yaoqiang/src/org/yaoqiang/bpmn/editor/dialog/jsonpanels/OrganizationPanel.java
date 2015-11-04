package org.yaoqiang.bpmn.editor.dialog.jsonpanels;

import javax.swing.BoxLayout;

import org.json.JSONObject;
import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.dialog.JSONPanel;
import org.yaoqiang.bpmn.editor.dialog.JSONTextPanel;
import org.yaoqiang.bpmn.editor.dialog.PanelContainer;
import org.yaoqiang.bpmn.editor.util.BPMNEditorUtils;
import org.yaoqiang.graph.model.OrganizationGraphModel;

/**
 * OrganizationPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class OrganizationPanel extends JSONPanel {

	private static final long serialVersionUID = -7257047844739551708L;

	protected JSONTextPanel idPanel;

	protected JSONTextPanel namePanel;

	public OrganizationPanel(PanelContainer pc, JSONObject owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		idPanel = new JSONTextPanel(pc, owner, "id", false, false);
		namePanel = new JSONTextPanel(pc, owner, "name");
		this.add(idPanel);
		this.add(namePanel);

	}

	public void saveObjects() {
		if (namePanel.getText().length() > 0) {
			namePanel.saveObjects();
			OrganizationGraphModel model = (OrganizationGraphModel) BPMNEditor.getInstance().getOrgGraphComponent().getGraph().getModel();
			if (!namePanel.getText().equals(model.getOrganizationName())) {
				boolean create = false;
				if (!BPMNEditor.getInstance().getOrganizations().contains(owner)) {
					create = true;
					model.clear();
					model.addSampleCells(namePanel.getText());
				}
				model.setOrganizationName(namePanel.getText());
				BPMNEditorUtils.saveOrganization(idPanel.getText(), model);
				if (create) {
					BPMNEditor.getInstance().getOrganizations().add((JSONObject) owner);
				}
				OrganizationsPanel parent = (OrganizationsPanel) getParentPanel().getParent();
				parent.getOrganizationPanel().refreshItem(BPMNEditor.getInstance().getOrganizations());
				super.saveObjects();
			}
		}
	}

}
