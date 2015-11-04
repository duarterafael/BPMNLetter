package org.yaoqiang.bpmn.editor.dialog.jsonpanels;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.naming.directory.Attributes;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.json.JSONObject;
import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.dialog.JSONComboPanel;
import org.yaoqiang.bpmn.editor.dialog.JSONPanel;
import org.yaoqiang.bpmn.editor.dialog.JSONTablePanel;
import org.yaoqiang.bpmn.editor.dialog.JSONTextPanel;
import org.yaoqiang.bpmn.editor.dialog.PanelContainer;
import org.yaoqiang.bpmn.editor.dialog.ldaptree.LdapTreeNode;
import org.yaoqiang.bpmn.editor.util.LdapUtils;

import com.mxgraph.util.mxResources;


/**
 * LdapEntryPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class LdapEntryPanel extends JSONPanel {

	private static final long serialVersionUID = -7257047844739551708L;

	protected JSONObject con = BPMNEditor.getInstance().getCurrentLdapConnection();

	protected String type;

	protected JSONComboPanel pdnPanel;

	protected JSONTextPanel uidPanel;
	protected JSONTextPanel mailPanel;
	protected JSONTextPanel namePanel;
	protected JSONTextPanel snPanel;
	protected JSONComboPanel ouPanel;
	protected JSONComboPanel managerPanel;
	protected JSONTextPanel descPanel;

	protected JSONComboPanel rdnPanel;

	protected JSONTablePanel membersPanel;

	public LdapEntryPanel(PanelContainer pc, JSONObject owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		type = owner.optString("type");
		this.setBorder(BorderFactory.createTitledBorder(mxResources.get(type)));

		pdnPanel = new JSONComboPanel(pc, owner, "pdn", LdapUtils.getLdapEntries(null), false, false, true);
		this.add(pdnPanel);

		String key = "cn";
		if ("ou".equals(type)) {
			key = "ou";
		} else if ("o".equals(type)) {
			key = "o";
		}
		namePanel = new JSONTextPanel(pc, owner, key);
		snPanel = new JSONTextPanel(pc, owner, "sn", false, 80, 26);

		List<String> choices = new ArrayList<String>();
		if ("ou".equals(type)) {
			choices.add(mxResources.get("ou"));
		} else if ("group".equals(type) || "role".equals(type)) {
			choices.add(mxResources.get("cn"));
		} else if ("o".equals(type)) {
			choices.add(mxResources.get("o"));
		} else {
			choices.add(mxResources.get("cn"));
			choices.add(mxResources.get("uid"));
		}
		rdnPanel = new JSONComboPanel(pc, owner, "rdn", choices, false, false, true);

		JPanel nameRdnPanel = new JPanel();
		nameRdnPanel.setLayout(new BoxLayout(nameRdnPanel, BoxLayout.X_AXIS));
		nameRdnPanel.add(namePanel);
		if ("person".equals(type)) {
			nameRdnPanel.add(snPanel);
		}
		nameRdnPanel.add(rdnPanel);
		this.add(nameRdnPanel);

		uidPanel = new JSONTextPanel(pc, owner, "uid", false, 80, 26);
		mailPanel = new JSONTextPanel(pc, owner, "mail", "email", null);
		if ("person".equals(type)) {
			JPanel userPanel = new JPanel();
			userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.X_AXIS));
			userPanel.add(uidPanel);
			userPanel.add(mailPanel);
			this.add(userPanel);
		}

		if ("person".equals(type) || "group".equals(type) || "role".equals(type)) {
			boolean hasEmpty = false;
			if ("group".equals(type)) {
				hasEmpty = true;
			}
			ouPanel = new JSONComboPanel(pc, owner, "ou", LdapUtils.getLdapEntries("ou"), hasEmpty, false, true);
			this.add(ouPanel);
			if ("person".equals(type)) {
				List<String> entries = LdapUtils.getLdapEntries("person");
				entries.remove(owner.optString("dn"));
				managerPanel = new JSONComboPanel(pc, owner, "manager", entries, true, false, true);
				this.add(managerPanel);
			}
		}

		descPanel = new JSONTextPanel(pc, null, "description", owner.optString("description"));
		this.add(descPanel);

		if ("group".equals(type) || "role".equals(type)) {
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
			buttonPanel.add(Box.createHorizontalStrut(5));
			buttonPanel.add(createToolbarButton(selectAction));
			buttonPanel.add(Box.createHorizontalGlue());
			this.add(buttonPanel);

			List<JSONObject> members = new ArrayList<JSONObject>();
			String title = null;
			if ("group".equals(type)) {
				members = LdapUtils.getGroupMembers(owner);
				title = "members";
			} else if ("role".equals(type)) {
				members = LdapUtils.getRoleOccupants(owner);
				title = "roleOccupants";
			}
			membersPanel = new JSONTablePanel(panelContainer, owner, type, title, Arrays.asList("dn"), members, 550, 150, false, false);
			this.add(membersPanel);
		}

	}

	public JButton createToolbarButton(Action a) {
		String actionName = (String) a.getValue(Action.NAME);
		if ("select".equals(actionName)) {
			if ("group".equals(type)) {
				actionName = "SelectMembers";
			} else if ("role".equals(type)) {
				actionName = "SelectRoleOccupants";
			}
		}
		JButton b = new JButton(mxResources.get(actionName));
		b.setName(actionName);
		b.addActionListener(a);
		b.setToolTipText(mxResources.get(actionName));
		return b;
	}

	protected Action selectAction = new AbstractAction("select") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			String selectType = null;
			if ("group".equals(type)) {
				selectType = "selectMembers";
			} else if ("role".equals(type)) {
				selectType = "selectRoleOccupants";
			}
			BPMNEditor.getInstance().createDialog().initDialog().editObject(membersPanel, owner, selectType);
		}
	};

	public void saveObjects() {
		if (con != null) {

			String ou = null;
			if ("person".equals(type) || "group".equals(type) || "role".equals(type)) {
				ou = ouPanel.getSelectedItem().toString();
			}
			
			String manager = null;
			if ("person".equals(type)) {
				manager = managerPanel.getSelectedItem().toString();
			}

			List<JSONObject> members = null;
			if ("group".equals(type) || "role".equals(type)) {
				members = membersPanel.getAllElements();
			}

			String pdn = pdnPanel.getSelectedItem().toString();
			if (pdn.length() == 0) {
				pdn = con.optString("baseDN");
			}
			String rdn = rdnPanel.getSelectedItem().toString();
			
			String dn = null;
			if ("edit".equals(((JSONObject) owner).optString("action"))) {
				dn = LdapUtils.editLdapEntry(con, type, uidPanel.getText(), mailPanel.getText(), namePanel.getText(), snPanel.getText(), ou, manager, members, pdn, rdn,
						((JSONObject) owner).optString("dn"));
			} else {
				dn = LdapUtils.editLdapEntry(con, type, uidPanel.getText(), mailPanel.getText(), namePanel.getText(), snPanel.getText(), ou, manager, members, pdn, rdn, null);
			}
			if (dn != null) {
				Map<String, Attributes> entries = LdapUtils.searchLdap(con);
				BPMNEditor.setLdapEntries(entries);
				Map<String, LdapTreeNode> nodes = LdapUtils.buildLdapTreeNodes(con.optString("baseDN"), entries);
				BPMNEditor.getInstance().resetLdapTree(new LdapTreeNode[] { nodes.get(con.optString("baseDN")), nodes.get(dn) });
			}
		}
	}

}
