package org.yaoqiang.bpmn.editor.dialog.jsonpanels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.directory.Attributes;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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
 * SearchLdapPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class SearchLdapPanel extends JSONPanel {

	private static final long serialVersionUID = 1L;

	protected JSONObject con = BPMNEditor.getInstance().getCurrentLdapConnection();

	protected JSONTextPanel connectionPanel;

	protected JSONTextPanel urlPanel;

	protected JSONTextPanel baseDNPanel;

	protected JSONComboPanel scopePanel;

	protected JSONTextPanel timelimitPanel;

	protected JSONTextPanel countlimitPanel;

	protected JSONTextPanel filterPanel;

	public SearchLdapPanel(final PanelContainer pc, final BPMNEditor owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		String baseDN = owner.getLdapSelectedEntry();
		if (baseDN.length() == 0) {
			baseDN = con.optString("baseDN");
		}
		JPanel ldapPanel = new JPanel();
		ldapPanel.setLayout(new BoxLayout(ldapPanel, BoxLayout.Y_AXIS));
		ldapPanel.setBorder(BorderFactory.createTitledBorder(mxResources.get("ldapServer")));

		connectionPanel = new JSONTextPanel(pc, null, "connection", false, null, con.optString("name"), 180, 26, false);
		urlPanel = new JSONTextPanel(pc, null, "URL", false, null, con.optString("url"), 180, 26, false);

		JPanel a1Panel = new JPanel();
		a1Panel.setLayout(new BoxLayout(a1Panel, BoxLayout.X_AXIS));
		a1Panel.add(connectionPanel);
		a1Panel.add(urlPanel);
		ldapPanel.add(a1Panel);

		baseDNPanel = new JSONTextPanel(pc, null, "baseDN", false, baseDN);
		ldapPanel.add(baseDNPanel);

		this.add(ldapPanel);

		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
		searchPanel.setBorder(BorderFactory.createTitledBorder(mxResources.get("searchOptions")));

		List<String> choices = new ArrayList<String>();
		choices.add("Subtree");
		choices.add("One level");
		choices.add("Base");
		scopePanel = new JSONComboPanel(pc, owner, "scope", choices, false, false, true);
		timelimitPanel = new JSONTextPanel(pc, null, "timeout", false, 20, 26, "0");
		countlimitPanel = new JSONTextPanel(pc, null, "countlimit", false, 20, 26, "0");

		JPanel a3Panel = new JPanel();
		a3Panel.setLayout(new BoxLayout(a3Panel, BoxLayout.X_AXIS));
		a3Panel.add(scopePanel);
		a3Panel.add(timelimitPanel);
		a3Panel.add(countlimitPanel);
		searchPanel.add(a3Panel);

		filterPanel = new JSONTextPanel(pc, null, "filter", false, "(objectClass=*)");
		searchPanel.add(filterPanel);

		this.add(searchPanel);
	}

	public void saveObjects() {
		if (con == null) {
			return;
		}

		Map<String, Attributes> results = LdapUtils.searchLdap(con.optString("protocol").substring(6), urlPanel.getText(), baseDNPanel.getText(), scopePanel
				.getSelectedItem().toString(), filterPanel.getText(), Long.parseLong(countlimitPanel.getText()),
				Integer.parseInt(timelimitPanel.getText()) * 1000, !con.optBoolean("useSecurityCredentials"), con.optString("userDN"), con
						.optString("password"));
		if (!results.isEmpty()) {
			try {
				con.put("scope", scopePanel.getSelectedItem().toString());
				con.put("filter", filterPanel.getText());
				con.put("countlimit", countlimitPanel.getText());
				con.put("timelimit", timelimitPanel.getText());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			BPMNEditor.getInstance().setCurrentLdapConnection(con);
			BPMNEditor.getInstance().getLdapConnectionTextPanel().setText(con.optString("name"));

			Map<String, LdapTreeNode> nodes = LdapUtils.buildLdapTreeNodes(baseDNPanel.getText(), results);
			BPMNEditor.getInstance().resetLdapTree(new LdapTreeNode[] { nodes.get(baseDNPanel.getText()), nodes.get(baseDNPanel.getText()) });

			BPMNEditor.setLdapEntries(results);
		}
	}

}
