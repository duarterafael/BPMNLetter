package org.yaoqiang.bpmn.editor.dialog.jsonpanels;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.naming.directory.Attributes;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.json.JSONException;
import org.json.JSONObject;
import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.dialog.JSONComboPanel;
import org.yaoqiang.bpmn.editor.dialog.JSONPanel;
import org.yaoqiang.bpmn.editor.dialog.JSONTextPanel;
import org.yaoqiang.bpmn.editor.dialog.PanelContainer;
import org.yaoqiang.bpmn.editor.dialog.ldaptree.LdapTreeNode;
import org.yaoqiang.bpmn.editor.util.LdapUtils;

import com.mxgraph.util.mxResources;


/**
 * ConnectLdapPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class ConnectLdapPanel extends JSONPanel {

	private static final long serialVersionUID = 1L;

	protected JSONComboPanel connectionPanel;

	protected JSONTextPanel urlPanel;

	protected JSONTextPanel baseDNPanel;

	protected JSONTextPanel userDNPanel;

	protected JSONTextPanel passwordPanel;

	protected JSONTextPanel timelimitPanel;

	protected JSONTextPanel countlimitPanel;

	public ConnectLdapPanel(final PanelContainer pc, final BPMNEditor owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		urlPanel = new JSONTextPanel(pc, null, "URL", false, 180, 26, false);
		baseDNPanel = new JSONTextPanel(pc, null, "baseDN", false, 200, 26, true);

		connectionPanel = new JSONComboPanel(pc, owner, "connection", owner.getLdapConnections().values(), false, false, true);
		connectionPanel.addActionListener(new AbstractAction("connection") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Object sel = connectionPanel.getSelectedItem();
				if (sel instanceof JSONObject) {
					JSONObject obj = (JSONObject) sel;
					urlPanel.setText("ldap://" + obj.optString("host", "localhost") + ":" + obj.optString("port", "389"));
					baseDNPanel.setText(obj.optString("baseDN"));
					if (obj.optBoolean("useSecurityCredentials")) {
						userDNPanel.setEnabled(true);
						passwordPanel.setEnabled(true);
						userDNPanel.setText(obj.optString("userDN"));
						passwordPanel.setText(obj.optString("password"));
					} else {
						userDNPanel.setEnabled(false);
						passwordPanel.setEnabled(false);
						userDNPanel.setText("");
						passwordPanel.setText("");
					}
				} else {
					urlPanel.setText("");
					baseDNPanel.setText("");
					userDNPanel.setText("");
					passwordPanel.setText("");
				}
			}
		});
		JButton editButton = new JButton(mxResources.get("Edit"));
		editButton.addActionListener(new AbstractAction("edit") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				owner.createDialog().initDialog().editObject(ConnectLdapPanel.this, owner, "ldapConnections");
			}

		});
		userDNPanel = new JSONTextPanel(pc, null, "userDN", false, false);
		passwordPanel = new JSONTextPanel(pc, null, "password", true, false);

		JPanel a1Panel = new JPanel();
		a1Panel.setLayout(new BoxLayout(a1Panel, BoxLayout.X_AXIS));
		a1Panel.add(connectionPanel);
		a1Panel.add(editButton);
		this.add(a1Panel);

		JPanel a2Panel = new JPanel();
		a2Panel.setLayout(new BoxLayout(a2Panel, BoxLayout.X_AXIS));
		a2Panel.add(urlPanel);
		a2Panel.add(baseDNPanel);
		this.add(a2Panel);
		this.add(userDNPanel);
		this.add(passwordPanel);

		timelimitPanel = new JSONTextPanel(pc, null, "timeout", false, 20, 26, "0");
		countlimitPanel = new JSONTextPanel(pc, null, "countlimit", false, 20, 26, "0");

		JPanel limitPanel = new JPanel();
		limitPanel.setLayout(new BoxLayout(limitPanel, BoxLayout.X_AXIS));
		limitPanel.add(timelimitPanel);
		limitPanel.add(countlimitPanel);
		this.add(limitPanel);

		Object sel = connectionPanel.getSelectedItem();
		if (sel instanceof JSONObject) {
			JSONObject obj = (JSONObject) sel;
			urlPanel.setText("ldap://" + obj.optString("host", "localhost") + ":" + obj.optString("port", "389"));
			baseDNPanel.setText(obj.optString("baseDN"));
			if (obj.optBoolean("useSecurityCredentials")) {
				userDNPanel.setEnabled(true);
				passwordPanel.setEnabled(true);
				userDNPanel.setText(obj.optString("userDN"));
				passwordPanel.setText(obj.optString("password"));
			} else {
				userDNPanel.setEnabled(false);
				passwordPanel.setEnabled(false);
				userDNPanel.setText("");
				passwordPanel.setText("");
			}
		}
	}

	public JSONComboPanel getConnectionPanel() {
		return connectionPanel;
	}

	public void saveObjects() {
		Object item = connectionPanel.getSelectedItem();
		if (item == null || item.equals("") || !(item instanceof JSONObject)) {
			return;
		}
		JSONObject con = (JSONObject) item;

		Map<String, Attributes> results = LdapUtils.connectLdap(con.optString("protocol").substring(6), urlPanel.getText(), baseDNPanel.getText(),
				Long.parseLong(countlimitPanel.getText()), Integer.parseInt(timelimitPanel.getText()) * 1000, !con.optBoolean("useSecurityCredentials"),
				userDNPanel.getText(), passwordPanel.getText());
		if (!results.isEmpty()) {
			try {
				con.put("url", urlPanel.getText());
				con.put("baseDN", baseDNPanel.getText());
				con.put("userDN", userDNPanel.getText());
				con.put("password", passwordPanel.getText());
				con.put("scope", "Subtree");
				con.put("filter", "(objectClass=*)");
				con.put("countlimit", countlimitPanel.getText());
				con.put("timelimit", timelimitPanel.getText());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			BPMNEditor.getInstance().setCurrentLdapConnection(con);
			BPMNEditor.getInstance().getLdapConnectionTextPanel().setText(con.optString("name"));
			
			Map<String, LdapTreeNode> nodes = LdapUtils.buildLdapTreeNodes(con.optString("baseDN"), results);
			BPMNEditor.getInstance().resetLdapTree(new LdapTreeNode[] { nodes.get(con.optString("baseDN")), nodes.get(con.optString("baseDN")) });
			
			BPMNEditor.getInstance().getLdapPanel().enableToolbar(true);
			BPMNEditor.setLdapEntries(results);
		}
	}

}
