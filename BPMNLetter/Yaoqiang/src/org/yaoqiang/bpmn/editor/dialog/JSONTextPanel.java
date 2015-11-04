package org.yaoqiang.bpmn.editor.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.json.JSONException;
import org.json.JSONObject;

import com.mxgraph.util.mxResources;


/**
 * JSONTextPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class JSONTextPanel extends JSONPanel {

	private static final long serialVersionUID = 1L;

	protected JTextField jtf;

	protected JLabel label;

	protected String key;

	public JSONTextPanel(PanelContainer pc, JSONObject owner, String key) {
		this(pc, owner, key, false, null, null, 150, 26, true);
	}

	public JSONTextPanel(PanelContainer pc, JSONObject owner, String key, String value) {
		this(pc, owner, key, false, null, value, 150, 26, true);
	}

	public JSONTextPanel(PanelContainer pc, JSONObject owner, String key, boolean password) {
		this(pc, owner, key, password, null, null, 150, 26, true);
	}

	public JSONTextPanel(PanelContainer pc, JSONObject owner, String key, boolean password, boolean enabled) {
		this(pc, owner, key, password, null, null, 150, 26, enabled);
	}

	public JSONTextPanel(PanelContainer pc, JSONObject owner, String key, boolean password, String value) {
		this(pc, owner, key, password, null, value, 150, 26, true);
	}

	public JSONTextPanel(PanelContainer pc, JSONObject owner, String key, boolean password, int width, int height) {
		this(pc, owner, key, password, null, null, width, height, true);
	}

	public JSONTextPanel(PanelContainer pc, JSONObject owner, String key, boolean password, int width, int height, boolean enabled) {
		this(pc, owner, key, password, null, null, width, height, enabled);
	}

	public JSONTextPanel(PanelContainer pc, JSONObject owner, String key, boolean password, int width, int height, String value) {
		this(pc, owner, key, password, null, value, width, height, true);
	}

	public JSONTextPanel(PanelContainer pc, JSONObject owner, String key, String title, String value) {
		this(pc, owner, key, false, title, value, 150, 26, true);
	}

	public JSONTextPanel(final PanelContainer pc, JSONObject owner, String key, boolean password, String title, String value, int width, int height, boolean enabled) {
		super(pc, owner);
		this.key = key;
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		label = new JLabel(mxResources.get(title == null ? key : title) + ": ");

		if (password) {
			jtf = new JPasswordField();
		} else {
			jtf = new JTextField();
		}
		jtf.setText((value == null && owner != null) ? owner.optString(key) : value);

		Dimension textDim = new Dimension(width, height);
		jtf.setMinimumSize(new Dimension(textDim));
		jtf.setMaximumSize(new Dimension(textDim));
		jtf.setPreferredSize(new Dimension(textDim));
		jtf.setEnabled(enabled);

		jtf.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				if (pc != null) {
					pc.panelChanged();
				}
			}

			public void removeUpdate(DocumentEvent e) {
				changedUpdate(e);
			}

			public void insertUpdate(DocumentEvent e) {
				changedUpdate(e);
			}
		});

		this.add(label, BorderLayout.WEST);
		this.add(Box.createHorizontalGlue(), BorderLayout.EAST);
		this.add(jtf, BorderLayout.CENTER);
	}

	public void saveObjects() {
		try {
			if (jtf.isEnabled()) {
				((JSONObject)owner).put(this.key, getText());
			} else {
				((JSONObject)owner).put(this.key, (Object) null);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public JTextField getTextField() {
		return jtf;
	}

	public JLabel getLabel() {
		return label;
	}

	public String getText() {
		return jtf.getText().trim();
	}

	public void setText(String text) {
		jtf.setText(text);
	}

	public void setEnabled(boolean b) {
		super.setEnabled(b);
		jtf.setEnabled(b);
	}
	
	public boolean isEnabled() {
		return jtf.isEnabled();
	}
	
}
