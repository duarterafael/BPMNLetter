package org.yaoqiang.bpmn.editor.dialog.jsonpanels;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.json.JSONException;
import org.json.JSONObject;
import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.dialog.JSONCheckboxPanel;
import org.yaoqiang.bpmn.editor.dialog.JSONComboPanel;
import org.yaoqiang.bpmn.editor.dialog.JSONPanel;
import org.yaoqiang.bpmn.editor.dialog.JSONTextPanel;
import org.yaoqiang.bpmn.editor.dialog.PanelContainer;
import org.yaoqiang.bpmn.editor.util.LdapUtils;

import com.mxgraph.util.mxResources;


/**
 * LdapConnectionPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class LdapConnectionPanel extends JSONPanel {

	private static final long serialVersionUID = 1L;

	protected JSONTextPanel namePanel;

	protected JSONTextPanel hostPanel;

	protected JSONTextPanel portPanel;

	protected JSONComboPanel protocolPanel;

	protected JSONTextPanel baseDNPanel;

	protected JSONCheckboxPanel useCredentialsPanel;

	protected JSONCheckboxPanel saveCredentialsPanel;

	protected JSONTextPanel userDNPanel;

	protected JSONTextPanel passwordPanel;

	public LdapConnectionPanel(final PanelContainer pc, final JSONObject owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		namePanel = new JSONTextPanel(pc, owner, "name");
		this.add(namePanel);

		hostPanel = new JSONTextPanel(pc, owner, "host");
		portPanel = new JSONTextPanel(pc, owner, "port", false, 50, 26);

		List<String> choices = new ArrayList<String>();
		choices.add("LDAP v3");
		choices.add("LDAP v2");
		protocolPanel = new JSONComboPanel(pc, owner, "protocol", choices, false, false, true);

		baseDNPanel = new JSONTextPanel(pc, owner, "baseDN");

		useCredentialsPanel = new JSONCheckboxPanel(pc, owner, "useSecurityCredentials", true);
		useCredentialsPanel.addActionListener(new AbstractAction("useSecurityCredentials") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (useCredentialsPanel.isSelected()) {
					userDNPanel.setEnabled(true);
					passwordPanel.setEnabled(true);
					saveCredentialsPanel.setEnabled(true);
					userDNPanel.setText(owner.optString("userDN"));
					passwordPanel.setText(owner.optString("password"));
					saveCredentialsPanel.setSelected(true);
				} else {
					userDNPanel.setEnabled(false);
					passwordPanel.setEnabled(false);
					saveCredentialsPanel.setEnabled(false);
					userDNPanel.setText("");
					passwordPanel.setText("");
					saveCredentialsPanel.setSelected(false);
				}
			}
		});
		userDNPanel = new JSONTextPanel(pc, owner, "userDN", false, useCredentialsPanel.isSelected());
		passwordPanel = new JSONTextPanel(pc, owner, "password", true, useCredentialsPanel.isSelected());
		saveCredentialsPanel = new JSONCheckboxPanel(pc, owner, "saveSecurityCredentials", useCredentialsPanel.isSelected());

		JPanel a3Panel = new JPanel();
		a3Panel.setLayout(new BoxLayout(a3Panel, BoxLayout.Y_AXIS));
		a3Panel.setBorder(BorderFactory.createTitledBorder(mxResources.get("location")));

		JPanel serverPanel = new JPanel();
		serverPanel.setLayout(new BoxLayout(serverPanel, BoxLayout.X_AXIS));
		serverPanel.add(hostPanel);
		serverPanel.add(portPanel);
		serverPanel.add(protocolPanel);
		a3Panel.add(serverPanel);
		a3Panel.add(baseDNPanel);
		this.add(a3Panel);

		JPanel a2Panel = new JPanel();
		a2Panel.setLayout(new BoxLayout(a2Panel, BoxLayout.Y_AXIS));
		a2Panel.setBorder(BorderFactory.createTitledBorder(mxResources.get("authentication")));
		a2Panel.add(useCredentialsPanel);
		a2Panel.add(userDNPanel);
		a2Panel.add(passwordPanel);
		a2Panel.add(saveCredentialsPanel);
		this.add(a2Panel);

		JButton buttonOpt = getDialog().getOptionButton();
		if (buttonOpt != null) {
			buttonOpt.addActionListener(new AbstractAction("testConnection") {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					String url = "ldap://" + hostPanel.getText() + ":" + portPanel.getText();
					boolean success = LdapUtils.testLdapConnection(protocolPanel.getSelectedItem().toString().substring(6), url, baseDNPanel.getText(),
							!useCredentialsPanel.isSelected(), userDNPanel.getText(), passwordPanel.getText());
					if (success) {
						JOptionPane.showMessageDialog(null, mxResources.get("connectionSucceeded"), mxResources.get("testConnection"),
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
		}
	}

	public void saveObjects() {
		namePanel.saveObjects();
		hostPanel.saveObjects();
		portPanel.saveObjects();
		protocolPanel.saveObjects();
		baseDNPanel.saveObjects();
		useCredentialsPanel.saveObjects();
		saveCredentialsPanel.saveObjects();

		if (saveCredentialsPanel.isSelected()) {
			userDNPanel.saveObjects();
			passwordPanel.saveObjects();
		} else {
			if (useCredentialsPanel.isSelected()) {
				userDNPanel.saveObjects();
			} else {
				try {
					((JSONObject) owner).put("userDN", (Object) null);
					((JSONObject) owner).put("password", (Object) null);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		Map<String, JSONObject> cons = BPMNEditor.getInstance().getLdapConnections();
		String id = ((JSONObject) owner).optString("id");
		if (id.length() == 0) {
			id = "con_";
			int num = 1;
			while (cons.containsKey(id + num)) {
				num++;
			}
			id += num;
			try {
				((JSONObject) owner).put("id", id);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			cons.put(id, (JSONObject) owner);
		}
		LdapUtils.saveLdapConnection(id, (JSONObject) owner);

		super.saveObjects();
	}

}
