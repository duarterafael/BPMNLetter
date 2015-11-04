package org.yaoqiang.bpmn.editor.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.json.JSONException;
import org.json.JSONObject;
import org.yaoqiang.bpmn.editor.BPMNEditor;

import com.mxgraph.util.mxResources;


/**
 * JSONComboPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class JSONComboPanel extends JSONPanel {

	private static final long serialVersionUID = 1L;

	Dimension textDim = new Dimension(120, 26);

	protected String key;

	protected JComboBox jcb;

	protected boolean hasEmpty;
	
	public JSONComboPanel(PanelContainer pc, Object owner, String key, Collection<?> choices, boolean hasEmpty, boolean isEditable, boolean isEnabled) {
		super(pc, owner);
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.key = key;
		this.hasEmpty = hasEmpty;
		
		JLabel jl = new JLabel(mxResources.get(key) + ": ");

		Dimension toSet = new Dimension(textDim);

		List<JSONObjectView> chs = convertToViewList(choices);
		jcb = new JComboBox(new Vector<Object>(chs));

		Object chsn = null;
		if (owner instanceof JSONObject) {
			if ("rdn".equals(key)) {
				String dn = ((JSONObject) owner).optString("dn");
				if (dn.startsWith("uid")) {
					chsn = mxResources.get("uid");
				} else if (dn.length() > 1) {
					chsn = mxResources.get(dn.substring(0, 2));
				}
			} else if ("pdn".equals(key)) {
				chsn = BPMNEditor.getInstance().getLdapSelectedEntry();
				if ("edit".equals(((JSONObject) owner).optString("action"))) {
					chsn = chsn.toString().substring(chsn.toString().indexOf(",") + 1);
				} else if (chsn.toString().length() == 0) {
					chsn = ((JSONObject) owner).optString("baseDN");
				}
			} else {
				chsn = ((JSONObject) owner).opt(key);
			}

			if (chsn != null) {
				chsn = new JSONObjectView((String) chsn);
			} else {
				chsn = new JSONObjectView("");
			}
		} else if (owner instanceof BPMNEditor) {
			if ("currentOrganization".equals(key)) {
				JSONObject org = ((BPMNEditor) owner).getCurrentOrganization();
				if (org != null) {
					chsn = new JSONObjectView(org);
				} else {
					chsn = new JSONObjectView(new JSONObject());
				}
			}
		}
		if (chsn != null) {
			jcb.setSelectedItem(chsn);
		}
		toSet = getComboDimension(chs);
		jcb.setEditable(isEditable);

		jcb.setMinimumSize(new Dimension(toSet));
		jcb.setMaximumSize(new Dimension(toSet));
		jcb.setPreferredSize(new Dimension(toSet));
		jcb.setEnabled(isEnabled);

		jcb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				getPanelContainer().panelChanged();
			}

		});

		if (isEditable) {
			jcb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					getPanelContainer().panelChanged();
				}
			});

			jcb.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					getPanelContainer().panelChanged();
				}
			});
		}

		this.add(jl, BorderLayout.WEST);
		this.add(Box.createHorizontalGlue(), BorderLayout.EAST);
		this.add(jcb, BorderLayout.CENTER);

	}

	public void saveObjects() {
		try {
			if (owner instanceof JSONObject) {
				((JSONObject) owner).put(this.key, getSelectedItem());
			} else if (owner instanceof BPMNEditor) {
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public List<JSONObjectView> convertToViewList(Collection<?> choices) {
		List<JSONObjectView> vec = new ArrayList<JSONObjectView>();
		if (hasEmpty) {
			vec.add(new JSONObjectView(new JSONObject()));
		}
		for (Object ch : choices) {
			if (ch instanceof JSONObject) {
				vec.add(new JSONObjectView((JSONObject) ch));
			} else {
				vec.add(new JSONObjectView((String) ch));
			}
		}
		return vec;
	}

	public Object getSelectedItem() {
		Object obj = null;
		if (jcb.isEditable()) {
			obj = jcb.getEditor().getItem();
		} else {
			obj = jcb.getSelectedItem();
		}
		if (obj instanceof JSONObjectView) {
			JSONObjectView ov = (JSONObjectView) jcb.getSelectedItem();
			if (ov != null) {
				if (ov.getJSONObject() != null) {
					return ov.getJSONObject();
				}
				return ov.getJSONObjectString();
			}
		} else {
			if (obj != null) {
				return obj.toString();
			}
		}
		return "";
	}

	public Dimension getComboDimension(Collection<?> choices) {
		double w = 0;
		if (choices != null) {
			double longest = 0;
			for (Object ch : choices) {
				w = getFontMetrics(getFont()).stringWidth(ch.toString());
				if (w > longest) {
					longest = w;
				}
			}
			w = longest + 25;
		}
		if (w < textDim.width)
			w = textDim.width;
		return new Dimension((int) w, textDim.height);

	}

	public JComboBox getComboBox() {
		return jcb;
	}

	public void setSelectedItem(Object item) {
		jcb.setSelectedItem(new JSONObjectView((JSONObject) item));
	}

	public void refreshItem(Collection<?> items) {
		Object sel = getSelectedItem();
		List<JSONObjectView> chs = convertToViewList(items);	
		jcb.setModel(new DefaultComboBoxModel(new Vector<Object>(chs)));
		if (!items.isEmpty()){
			if (sel instanceof JSONObject) {
				setSelectedItem(sel);
			} else {
				setSelectedItem(items.toArray()[0]);
			}
		} else {
			jcb.setSelectedItem(new JSONObjectView(""));
		}
	}

	public void setEnabled(boolean b) {
		super.setEnabled(b);
		jcb.setEnabled(b);
	}

	public void requestFocus() {
		jcb.requestFocus();
	}

	public void addActionListener(ActionListener l) {
		jcb.addActionListener(l);
	}

	public boolean isEmpty() {
		Object o = getSelectedItem();
		if (o == null) {
			return true;
		}
		if (o instanceof String) {
			return ((String) o).trim().equals("");
		}
		return false;
	}

}
