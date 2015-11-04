package org.yaoqiang.bpmn.editor.dialog.jsonpanels;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.dialog.JSONPanel;
import org.yaoqiang.bpmn.editor.dialog.JSONTablePanel;
import org.yaoqiang.bpmn.editor.dialog.PanelContainer;

/**
 * LdapConnectionsPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class LdapConnectionsPanel extends JSONPanel {

	private static final long serialVersionUID = 3825364605958265614L;

	public LdapConnectionsPanel(PanelContainer pc, Object owner) {
		super(pc, owner);

		List<String> columnsToShow = new ArrayList<String>();
		columnsToShow.add("id");
		columnsToShow.add("name");
		columnsToShow.add("host");
		columnsToShow.add("port");
		columnsToShow.add("userDN");
		columnsToShow.add("protocol");
		this.add(new JSONTablePanel(panelContainer, owner, "ldapConnections", null, columnsToShow, ((BPMNEditor)owner).getLdapConnections().values(), 550, 150, true, false));

	}

	public void saveObjects() {
		Container parent = getParentPanel();
		if (parent instanceof ConnectLdapPanel) {
			((ConnectLdapPanel) parent).getConnectionPanel().refreshItem(BPMNEditor.getInstance().getLdapConnections().values());
		}
	}

}
