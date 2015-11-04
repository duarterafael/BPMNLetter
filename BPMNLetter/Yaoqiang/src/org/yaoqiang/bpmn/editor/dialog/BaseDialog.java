package org.yaoqiang.bpmn.editor.dialog;

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.activities.CallActivity;
import org.yaoqiang.bpmn.model.elements.activities.ResourceParameterBinding;
import org.yaoqiang.bpmn.model.elements.activities.ResourceRole;
import org.yaoqiang.bpmn.model.elements.activities.ResourceRoles;
import org.yaoqiang.bpmn.model.elements.core.common.PartnerEntity;
import org.yaoqiang.bpmn.model.elements.core.common.PartnerRole;
import org.yaoqiang.bpmn.model.elements.data.Assignment;
import org.yaoqiang.bpmn.model.elements.data.DataAssociation;
import org.yaoqiang.bpmn.model.elements.data.ItemAwareElement;
import org.yaoqiang.bpmn.model.elements.events.EventDefinition;

import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUndoableEdit;

/**
 * BaseDialog
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class BaseDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	protected PanelContainer panelContainer;

	protected Panel parentPanel;

	protected JButton buttonOpt;

	protected boolean save = true;

	public BaseDialog() {
		super((Frame) null, true);
		init();
	}

	protected void init() {
		this.panelContainer = new PanelContainer(this);
	}

	public BaseDialog initDialog() {
		return initDialog(true, null, null);
	}

	public BaseDialog initDialog(String optButton) {
		return initDialog(save, null, optButton);
	}

	public BaseDialog initDialog(boolean save) {
		return initDialog(save, null, null);
	}

	public BaseDialog initDialog(boolean save, String okButton) {
		return initDialog(save, okButton, null);
	}

	public BaseDialog initDialog(boolean save, String okButton, String optButton) {
		this.save = save;
		buildDialog(okButton, optButton);
		return this;
	}

	public Panel getParentPanel() {
		return parentPanel;
	}

	public JButton getOptionButton() {
		return buttonOpt;
	}

	public void editBPMNElement(Object el) {
		editObject(null, el, "");
	}

	public void editBPMNElement(XMLPanel parentPanel, Object el) {
		editObject(parentPanel, el, "");
	}

	public void editBPMNElement(XMLPanel parentPanel, String type, Object el) {
		editObject(parentPanel, el, type);
	}

	public void editBPMNElement(Object el, String type) {
		editObject(null, el, type);
	}

	public void editObject(Object el, String type) {
		editObject(null, el, type);
	}

	public void editObject(Panel parentPanel, Object el, String type) {
		this.parentPanel = parentPanel;
		panelContainer.setActiveObject(el, type);
		String title = "";
		if (el instanceof Map.Entry<?, ?>) {
			title = mxResources.get("namespace");
		} else if (el instanceof ResourceRoles) {
//			title = mxResources.get("resourceAssignment");
			title = "Pre/Post condition";
		} else if (el instanceof ResourceRole) {
//			title = mxResources.get("resource");
			title = "New Pre/Post condition";
		} else if (el instanceof ResourceParameterBinding) {
			title = mxResources.get("resourceParameterBinding");
		} else if (el instanceof CallActivity) {
			title = mxResources.get("callActivityOrProcess");
		} else {
			if (el instanceof EventDefinition || el instanceof DataAssociation || el instanceof Assignment || el instanceof PartnerEntity
					|| el instanceof PartnerRole) {
				title = mxResources.get(((XMLElement) el).toName());
			} else {
				if ((type == null || type.length() == 0 || type.startsWith("addon")) && el instanceof XMLElement || !type.equals("predefinedProp")
						&& el instanceof ItemAwareElement) {
					title = mxResources.get(((XMLElement) el).toName());
				} else {
					if (type.equals("predefinedProp")) {
						title = mxResources.get(((XMLElement) el).toName()) + " " + mxResources.get(type);
					} else {
						title = mxResources.get(type);
					}
				}
			}
		}
		setTitle(title);

		setDialogVisible();
	}

	protected void setDialogVisible() {
		setSize(panelContainer.getDisplay().getSize());
		pack();
		if (!isVisible()) {
			setLocationRelativeTo(this.getParent());
			setVisible(true);
		}
	}

	protected void buildDialog(String okButton, String optButton) {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		JButton buttonOK = new JButton(mxResources.get(okButton == null ? "ok" : okButton));

		JButton buttonCancel = new JButton(mxResources.get("cancel"));
		if (optButton != null) {
			buttonOpt = new JButton(mxResources.get(optButton));
			buttonPanel.add(Box.createHorizontalStrut(4));
			buttonPanel.add(buttonOpt);
		}
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(buttonOK);
		buttonPanel.add(Box.createHorizontalStrut(4));
		buttonPanel.add(buttonCancel);
		buttonPanel.add(Box.createHorizontalStrut(4));

		Container cp = getContentPane();
		cp.removeAll();
		cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
		cp.add(panelContainer);
		cp.add(Box.createVerticalStrut(5));
		cp.add(buttonPanel);

		// action listener for confirming
		buttonOK.addActionListener(okl);

		// action listener for cancel
		buttonCancel.addActionListener(al);
		addWindowListener(wl);

		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "Cancel");
		getRootPane().getActionMap().put("Cancel", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				al.actionPerformed(e);
			}
		});

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setResizable(true);
		buttonOK.setDefaultCapable(true);
		getRootPane().setDefaultButton(buttonOK);
	}

	public PanelContainer getPanelContainer() {
		return panelContainer;
	}

	public void save() {
		panelContainer.apply();
		if (save) {
			BPMNEditor.getInstance().getGraphComponent().getGraph().getModel().fireEvent(new mxEventObject(mxEvent.CHANGE, "edit", new mxUndoableEdit(null)));
		}
		dispose();
	}

	public void close() {
		if (panelContainer.isModified() && save) {
			int sw = panelContainer.showModifiedWarning();
			if (sw == JOptionPane.CANCEL_OPTION) {
				return;
			} else if (sw == JOptionPane.YES_OPTION) {
				BPMNEditor.getInstance().getGraphComponent().getGraph().getModel()
						.fireEvent(new mxEventObject(mxEvent.CHANGE, "edit", new mxUndoableEdit(null)));
			}
		}
		setVisible(false);
		dispose();
	}

	protected WindowListener wl = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			close();
		}
	};

	protected ActionListener al = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			close();
		}
	};

	protected ActionListener okl = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			save();
		}
	};
}
