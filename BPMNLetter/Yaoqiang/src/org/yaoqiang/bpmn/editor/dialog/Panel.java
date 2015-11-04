package org.yaoqiang.bpmn.editor.dialog;

import java.awt.Container;

import javax.swing.JPanel;

/**
 * Panel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class Panel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected PanelContainer panelContainer;
	protected Object owner;

	public Panel(PanelContainer pc, Object owner) {
		this.panelContainer = pc;
		this.owner = owner;
	}

	public void saveObjects() {
	}

	public PanelContainer getPanelContainer() {
		return panelContainer;
	}

	public void setOwner(Object owner) {
		this.owner = owner;
	}

	public Object getOwner() {
		return owner;
	}

	public Panel getParentPanel() {
		if (getDialog() != null) {
			return getDialog().getParentPanel();
		}
		return null;
	}

	public BaseDialog getDialog() {
		if (getPanelContainer() != null) {
			for (Container p = getPanelContainer().getParent(); p != null; p = p.getParent()) {
				if (p instanceof BaseDialog) {
					return (BaseDialog) p;
				}
			}
		}
		return null;
	}
	
	// For table panel only
	public boolean setSelectedElement(Object el) {
		return false;
	}
}
