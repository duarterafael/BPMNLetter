package org.yaoqiang.bpmn.editor.dialog;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;

import org.yaoqiang.bpmn.editor.addon.Addon;
import org.yaoqiang.bpmn.model.elements.XMLExtensionElement;

import com.mxgraph.util.mxResources;

/**
 * PanelContainer
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class PanelContainer extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static Addon currentAddon;

	protected BaseDialog parentDialog;

	protected JScrollPane scrollPane;

	protected PanelFactory panelFactory;

	protected boolean isModified = false;

	public PanelContainer(BaseDialog parentDialog) {
		this.parentDialog = parentDialog;
		initPanelContainer();
	}
	
	public void initPanelContainer() {
		panelFactory = new PanelFactory(this);
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

		JViewport port = scrollPane.getViewport();
		port.setScrollMode(JViewport.BLIT_SCROLL_MODE);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(40);

		setLayout(new BorderLayout());

		JPanel wp = new JPanel();
		wp.setLayout(new BoxLayout(wp, BoxLayout.Y_AXIS));

		wp.add(Box.createVerticalStrut(5));
		wp.add(scrollPane);

		add(wp, BorderLayout.CENTER);
	}

	public JComponent getDisplay() {
		return this;
	}

	public boolean isModified() {
		return this.isModified;
	}

	public void panelChanged() {
		setModified(true);
	}

	public int showModifiedWarning() {
		int option = JOptionPane.showConfirmDialog(null, mxResources.get("saveChanges"), mxResources.get("optionTitle"), JOptionPane.YES_NO_CANCEL_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			apply();
			setModified(false);
		}
		return option;
	}

	public void apply() {
		Panel p = getViewPanel();
		if (p != null) {
			p.saveObjects();
		}
	}

	public void setModified(boolean isModified) {
		this.isModified = isModified;
	}

	public void setActiveObject(Object el, String type) {
		setModified(false);
		Panel p = null;
		if (type != null && type.startsWith("addon") || el instanceof XMLExtensionElement) {
			PanelFactory panelFactory = getCurrentAddon().getPanelFactory(this);
			p = panelFactory.getPanel(el, type);
		} else {
			p = getPanelFactory().getPanel(el, type);
		}

		setViewPanel(p);
	}
	
	public BaseDialog getParentDialog() {
		return parentDialog;
	}

	public PanelFactory getPanelFactory() {
		return panelFactory;
	}

	public Addon getCurrentAddon() {
		return currentAddon;
	}

	public void setCurrentAddon(Addon currentAddon) {
		PanelContainer.currentAddon = currentAddon;
	}
	
	public Panel getViewPanel() {
		return (Panel) scrollPane.getViewport().getView();
	}

	public void setViewPanel(Panel panel) {
		scrollPane.setViewportView(panel);
	}

}
