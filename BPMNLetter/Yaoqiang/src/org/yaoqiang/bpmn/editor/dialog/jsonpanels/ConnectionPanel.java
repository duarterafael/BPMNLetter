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
import org.yaoqiang.bpmn.editor.util.BPMNEditorUtils;

import com.mxgraph.util.mxResources;


/**
 * ConnectionPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class ConnectionPanel extends JSONPanel {

	private static final long serialVersionUID = 1L;

	protected JSONTextPanel namePanel;

	protected JSONComboPanel vendorPanel;

	protected JSONTextPanel hostPanel;

	protected JSONTextPanel portPanel;

	protected JSONTextPanel baseURLPanel;

	protected JSONCheckboxPanel useCredentialsPanel;

	protected JSONCheckboxPanel saveCredentialsPanel;

	protected JSONTextPanel usernamePanel;

	protected JSONTextPanel passwordPanel;

	public ConnectionPanel(final PanelContainer pc, final JSONObject owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		namePanel = new JSONTextPanel(pc, owner, "name");
		this.add(namePanel);

		List<String> choices = new ArrayList<String>();
		choices.add("Activiti");
		//choices.add("jBPM");
		vendorPanel = new JSONComboPanel(pc, owner, "vendor", choices, false, false, true);

		hostPanel = new JSONTextPanel(pc, owner, "host");
		portPanel = new JSONTextPanel(pc, owner, "port", false, 50, 26);
		baseURLPanel = new JSONTextPanel(pc, owner, "baseURL");

		useCredentialsPanel = new JSONCheckboxPanel(pc, owner, "useSecurityCredentials", true);
		useCredentialsPanel.addActionListener(new AbstractAction("useSecurityCredentials") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (useCredentialsPanel.isSelected()) {
					usernamePanel.setEnabled(true);
					passwordPanel.setEnabled(true);
					saveCredentialsPanel.setEnabled(true);
					usernamePanel.setText(owner.optString("username"));
					passwordPanel.setText(owner.optString("password"));
					saveCredentialsPanel.setSelected(true);
				} else {
					usernamePanel.setEnabled(false);
					passwordPanel.setEnabled(false);
					saveCredentialsPanel.setEnabled(false);
					usernamePanel.setText("");
					passwordPanel.setText("");
					saveCredentialsPanel.setSelected(false);
				}
			}
		});
		usernamePanel = new JSONTextPanel(pc, owner, "username", false, useCredentialsPanel.isSelected());
		passwordPanel = new JSONTextPanel(pc, owner, "password", true, useCredentialsPanel.isSelected());
		saveCredentialsPanel = new JSONCheckboxPanel(pc, owner, "saveSecurityCredentials", useCredentialsPanel.isSelected());

		this.add(vendorPanel);

		JPanel a3Panel = new JPanel();
		a3Panel.setLayout(new BoxLayout(a3Panel, BoxLayout.Y_AXIS));
		a3Panel.setBorder(BorderFactory.createTitledBorder(mxResources.get("location")));

		JPanel serverPanel = new JPanel();
		serverPanel.setLayout(new BoxLayout(serverPanel, BoxLayout.X_AXIS));
		serverPanel.add(hostPanel);
		serverPanel.add(portPanel);
		a3Panel.add(serverPanel);
		a3Panel.add(baseURLPanel);
		this.add(a3Panel);

		JPanel a2Panel = new JPanel();
		a2Panel.setLayout(new BoxLayout(a2Panel, BoxLayout.Y_AXIS));
		a2Panel.setBorder(BorderFactory.createTitledBorder(mxResources.get("authentication")));
		a2Panel.add(useCredentialsPanel);
		a2Panel.add(usernamePanel);
		a2Panel.add(passwordPanel);
		a2Panel.add(saveCredentialsPanel);
		this.add(a2Panel);
		
		JButton buttonOpt = getDialog().getOptionButton();
		if (buttonOpt != null) {
			buttonOpt.addActionListener(new AbstractAction("testConnection") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				String url = "http://" + hostPanel.getText() + ":" + portPanel.getText() + baseURLPanel.getText();
				boolean success = BPMNEditorUtils.testConnection((String) vendorPanel.getSelectedItem(), url, usernamePanel.getText(), passwordPanel.getText());
				if (success) {
					JOptionPane.showMessageDialog(null, mxResources.get("connectionSucceeded"), mxResources.get("testConnection"), JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		}
	}

	public void saveObjects() {
		namePanel.saveObjects();
		vendorPanel.saveObjects();
		hostPanel.saveObjects();
		portPanel.saveObjects();
		baseURLPanel.saveObjects();
		useCredentialsPanel.saveObjects();
		saveCredentialsPanel.saveObjects();

		if (saveCredentialsPanel.isSelected()) {
			usernamePanel.saveObjects();
			passwordPanel.saveObjects();
		} else {
			if (useCredentialsPanel.isSelected()) {
				usernamePanel.saveObjects();
			} else {
				try {
					((JSONObject) owner).put("username", (Object) null);
					((JSONObject) owner).put("password", (Object) null);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
		Map<String, JSONObject> cons = BPMNEditor.getInstance().getConnections();
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
		BPMNEditorUtils.saveConnection(id, (JSONObject) owner);

		super.saveObjects();
	}
	
}
