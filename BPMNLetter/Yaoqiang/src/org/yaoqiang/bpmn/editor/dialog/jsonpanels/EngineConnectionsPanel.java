package org.yaoqiang.bpmn.editor.dialog.jsonpanels;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.dialog.JSONPanel;
import org.yaoqiang.bpmn.editor.dialog.JSONTablePanel;
import org.yaoqiang.bpmn.editor.dialog.PanelContainer;

/**
 * EngineConnectionsPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class EngineConnectionsPanel extends JSONPanel {

	private static final long serialVersionUID = 3825364605958265614L;

	public EngineConnectionsPanel(PanelContainer pc, Object owner) {
		super(pc, owner);

		List<String> columnsToShow = new ArrayList<String>();
		columnsToShow.add("id");
		columnsToShow.add("name");
		columnsToShow.add("host");
		columnsToShow.add("port");
		columnsToShow.add("username");
		columnsToShow.add("vendor");
		this.add(new JSONTablePanel(panelContainer, owner, "engineConnections", null, columnsToShow, ((BPMNEditor)owner).getConnections().values(), 550, 150, true, false));

	}

	public void saveObjects() {
		Container parent = getParentPanel();
		if (parent instanceof DeploymentPanel) {
			((DeploymentPanel) parent).getConnectionPanel().refreshItem(BPMNEditor.getInstance().getConnections().values());
		} else if (parent instanceof RetrieveDefinitionsPanel) {
			((RetrieveDefinitionsPanel) parent).getConnectionPanel().refreshItem(BPMNEditor.getInstance().getConnections().values());
		}
	}

}
