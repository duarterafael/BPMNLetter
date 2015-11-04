package org.yaoqiang.bpmn.editor.addon;

import javax.swing.ImageIcon;
import javax.swing.JMenu;

import org.yaoqiang.bpmn.editor.dialog.PanelFactory;
import org.yaoqiang.bpmn.editor.dialog.PanelContainer;

/**
 * Addon
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public abstract class Addon {

	protected AddonsManager addonsManager;
	
	public void setAddonsManager(AddonsManager addonsManager) {
		this.addonsManager = addonsManager;
	}

	public abstract String getName();

	public abstract String getVersion();

	public abstract ImageIcon getIcon();
	
	public abstract void activate();
	
	public abstract JMenu getPopupMenuItem();
	
	public abstract PanelFactory getPanelFactory(PanelContainer panelContainer);
	
}
