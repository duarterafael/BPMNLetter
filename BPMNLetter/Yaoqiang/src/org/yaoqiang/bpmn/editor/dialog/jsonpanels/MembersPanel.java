package org.yaoqiang.bpmn.editor.dialog.jsonpanels;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.json.JSONObject;
import org.json.JSONUtils;
import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.dialog.JSONPanel;
import org.yaoqiang.bpmn.editor.dialog.JSONTablePanel;
import org.yaoqiang.bpmn.editor.dialog.Panel;
import org.yaoqiang.bpmn.editor.dialog.PanelContainer;

import com.mxgraph.util.mxResources;


/**
 * MembersPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class MembersPanel extends JSONPanel {

	private static final long serialVersionUID = 1L;

	protected JSONTablePanel membersPanel;

	public MembersPanel(PanelContainer panelContainer, JSONObject owner) {
		super(panelContainer, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.add(Box.createVerticalStrut(5));
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(createToolbarButton(selectAction));
		buttonPanel.add(createToolbarButton(deselectAction));
		buttonPanel.add(Box.createHorizontalGlue());
		this.add(buttonPanel);

		List<String> columnsToShow = new ArrayList<String>();
		columnsToShow.add("");
		columnsToShow.add("dn");
		List<String> dnList = new ArrayList<String>(BPMNEditor.getLdapEntries().keySet());
		dnList.remove(BPMNEditor.getInstance().getLdapSelectedEntry());
		membersPanel = new JSONTablePanel(getPanelContainer(), owner, "members", null, columnsToShow, JSONUtils.convertCollectionToJSONObject(dnList, "dn"),
				350, 300, false, true);
		this.add(membersPanel);

	}

	public void saveObjects() {
		Panel targetPanel = getParentPanel();
		((JSONTablePanel) targetPanel).removeAllRows();
		((JSONTablePanel) targetPanel).fillTableContent(membersPanel.getCheckedElements());
	}

	public JButton createToolbarButton(Action a) {
		String actionName = (String) a.getValue(Action.NAME);

		JButton b = new JButton(mxResources.get(actionName));

		b.setName(actionName);
		b.addActionListener(a);
		b.setToolTipText(mxResources.get(actionName));

		return b;
	}

	protected Action selectAction = new AbstractAction("selectAll") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			membersPanel.selectAllElements(true);
		}
	};

	protected Action deselectAction = new AbstractAction("deselectAll") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			membersPanel.selectAllElements(false);
		}
	};
}
