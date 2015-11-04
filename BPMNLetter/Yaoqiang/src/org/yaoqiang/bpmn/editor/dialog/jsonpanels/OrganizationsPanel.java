package org.yaoqiang.bpmn.editor.dialog.jsonpanels;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;

import org.json.JSONObject;
import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.action.ModelActions;
import org.yaoqiang.bpmn.editor.dialog.JSONComboPanel;
import org.yaoqiang.bpmn.editor.dialog.JSONPanel;
import org.yaoqiang.bpmn.editor.dialog.JSONTablePanel;
import org.yaoqiang.bpmn.editor.dialog.PanelContainer;
import org.yaoqiang.bpmn.editor.util.Utils;
import org.yaoqiang.graph.util.Constants;

/**
 * OrganizationsPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class OrganizationsPanel extends JSONPanel {

	private static final long serialVersionUID = -6222541878845897056L;

	protected JSONComboPanel organizationPanel;

	protected JSONTablePanel organizationsPanel;

	public OrganizationsPanel(PanelContainer pc, final Object owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		organizationPanel = new JSONComboPanel(pc, owner, "currentOrganization", ((BPMNEditor) owner).getOrganizations(), false, false, true);
		organizationPanel.addActionListener(new AbstractAction("organization") {

			private static final long serialVersionUID = 7815670878606763245L;

			public void actionPerformed(ActionEvent e) {
				Object sel = organizationPanel.getSelectedItem();
				if (sel instanceof JSONObject) {
					String orgId = ((JSONObject) sel).optString("id");
					Constants.SETTINGS.put("organization", orgId);
					Utils.saveConfigureFile();
					((BPMNEditor) owner).setCurrentOrganization((JSONObject) sel);
					ModelActions.getOpenOrganizationAction(orgId).actionPerformed(new ActionEvent(((BPMNEditor) owner).getOrgGraphComponent(), 0, ""));
				}
			}
		});
		this.add(organizationPanel);

		List<String> columnsToShow = new ArrayList<String>();
		columnsToShow.add("id");
		columnsToShow.add("name");
		organizationsPanel = new JSONTablePanel(panelContainer, owner, "organizations", "organizations", columnsToShow, ((BPMNEditor) owner).getOrganizations(),
				350, 120, true, false);
		this.add(organizationsPanel);

	}

	public JSONComboPanel getOrganizationPanel() {
		return organizationPanel;
	}

	public JSONTablePanel getOrganizationsPanel() {
		return organizationsPanel;
	}

}
