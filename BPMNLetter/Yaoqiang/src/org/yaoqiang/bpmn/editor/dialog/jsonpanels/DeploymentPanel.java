package org.yaoqiang.bpmn.editor.dialog.jsonpanels;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.json.JSONException;
import org.json.JSONObject;
import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.dialog.JSONComboPanel;
import org.yaoqiang.bpmn.editor.dialog.JSONPanel;
import org.yaoqiang.bpmn.editor.dialog.JSONTextPanel;
import org.yaoqiang.bpmn.editor.dialog.PanelContainer;
import org.yaoqiang.bpmn.editor.util.BPMNEditorUtils;

import com.mxgraph.util.mxResources;



/**
 * DeploymentPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class DeploymentPanel extends JSONPanel {

	private static final long serialVersionUID = 1L;

	protected JSONTextPanel filePanel;
	
	protected JSONComboPanel connectionPanel;

	protected JSONTextPanel usernamePanel;

	protected JSONTextPanel urlPanel;
	
	protected JSONTextPanel passwordPanel;
	
	public DeploymentPanel(final PanelContainer pc, final BPMNEditor owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel resourcesPanel = new JPanel();
		resourcesPanel.setLayout(new BoxLayout(resourcesPanel, BoxLayout.Y_AXIS));
		resourcesPanel.setBorder(BorderFactory.createTitledBorder(mxResources.get("processDefinition")));

		filePanel = new JSONTextPanel(pc, null, "file", false, 300, 26, false);
		filePanel.setText(BPMNEditor.getCurrentFile().getName());
		
		resourcesPanel.add(filePanel);
		this.add(resourcesPanel);
		
		JPanel enginePanel = new JPanel();
		enginePanel.setLayout(new BoxLayout(enginePanel, BoxLayout.Y_AXIS));
		enginePanel.setBorder(BorderFactory.createTitledBorder(mxResources.get("engineSettings")));

		urlPanel = new JSONTextPanel(pc, null, "URL", false, 300, 26, false);

		connectionPanel = new JSONComboPanel(pc, owner, "connection", owner.getConnections().values(), false, false, true);
		connectionPanel.addActionListener(new AbstractAction("connection") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Object sel = connectionPanel.getSelectedItem();
				if (sel instanceof JSONObject) {
					JSONObject obj = (JSONObject) sel;
					urlPanel.setText("http://" + obj.optString("host", "localhost") + ":" + obj.optString("port", "8080") + obj.optString("baseURL", "/activiti-rest/service"));
					if (obj.optBoolean("useSecurityCredentials")) {
						usernamePanel.setEnabled(true);
						passwordPanel.setEnabled(true);
						usernamePanel.setText(obj.optString("username"));
						passwordPanel.setText(obj.optString("password"));
					} else {
						usernamePanel.setEnabled(false);
						passwordPanel.setEnabled(false);
						usernamePanel.setText("");
						passwordPanel.setText("");
					}
				}
			}
		});
		JButton editButton = new JButton(mxResources.get("Edit"));
		editButton.addActionListener(new AbstractAction("edit") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				owner.createDialog().initDialog().editObject(DeploymentPanel.this, owner, "engineConnections");
			}
			
		});
		usernamePanel = new JSONTextPanel(pc, null, "username", false, false);
		passwordPanel = new JSONTextPanel(pc, null, "password", true, false);
		
		JPanel a1Panel = new JPanel();
		a1Panel.setLayout(new BoxLayout(a1Panel, BoxLayout.X_AXIS));
		a1Panel.add(connectionPanel);;
		a1Panel.add(editButton);
		enginePanel.add(a1Panel);

		JPanel a2Panel = new JPanel();
		a2Panel.setLayout(new BoxLayout(a2Panel, BoxLayout.X_AXIS));
		a2Panel.add(urlPanel);
		a2Panel.add(Box.createHorizontalGlue());
		enginePanel.add(a2Panel);
		
		enginePanel.add(usernamePanel);
		enginePanel.add(passwordPanel);
		
		this.add(enginePanel);
		
		Object sel = connectionPanel.getSelectedItem();
		if (sel instanceof JSONObject) {
			JSONObject obj = (JSONObject) sel;
			urlPanel.setText("http://" + obj.optString("host", "localhost") + ":" + obj.optString("port", "8080") + obj.optString("baseURL", "/activiti-rest/service"));
			if (obj.optBoolean("useSecurityCredentials")) {
				usernamePanel.setEnabled(true);
				passwordPanel.setEnabled(true);
				usernamePanel.setText(obj.optString("username"));
				passwordPanel.setText(obj.optString("password"));
			} else {
				usernamePanel.setEnabled(false);
				passwordPanel.setEnabled(false);
				usernamePanel.setText("");
				passwordPanel.setText("");
			}
		}
	}

	public JSONComboPanel getConnectionPanel() {
		return connectionPanel;
	}

	public void saveObjects() {
		JSONObject con = (JSONObject) connectionPanel.getSelectedItem();
		if (con == null) {
			return;
		}

		boolean success = BPMNEditorUtils.testConnection(con.optString("vendor"), urlPanel.getText(), usernamePanel.getText(), passwordPanel.getText());
		if (success) {
			try {
				con.put("url", urlPanel.getText());
				con.put("username", usernamePanel.getText());
				con.put("password", passwordPanel.getText());
			} catch (JSONException e) {
				e.printStackTrace();
			}
									
			success = BPMNEditorUtils.deploy(con, BPMNEditorUtils.createBarFile(BPMNEditor.getInstance().getGraphComponent(), null));
			if (success) {
				BPMNEditor.getInstance().setCurrentConnection(con);
				BPMNEditor.getInstance().getConnectionTextPanel().setText(con.optString("name"));
				BPMNEditor.getInstance().refreshDefinitionsRepositoryPanel("engine");				
				JOptionPane.showMessageDialog(null, mxResources.get("deploySucceeded"), mxResources.get("deployProcessDefinition"), JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(null, mxResources.get("deployFailed"), mxResources.get("deployProcessDefinition"), JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

}
