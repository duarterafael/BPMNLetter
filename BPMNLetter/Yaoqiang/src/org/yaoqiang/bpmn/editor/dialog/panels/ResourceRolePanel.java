package org.yaoqiang.bpmn.editor.dialog.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.yaoqiang.bpmn.editor.dialog.Panel;
import org.yaoqiang.bpmn.editor.dialog.PanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLChoosenPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTablePanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.activities.ResourceRole;
import org.yaoqiang.bpmn.model.elements.humaninteraction.UserTask;

import com.mxgraph.util.mxResources;


/**
 * ResourceRolePanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class ResourceRolePanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected XMLPanel namePanel;	
	protected XMLComboPanel typePanel;
	protected XMLComboPanel resourceRefPanel;
	protected XMLChoosenPanel expressionPanel;
	
	public ResourceRolePanel(PanelContainer pc, ResourceRole owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		List<String> choices = new ArrayList<String>();
//		if (owner.getParent().getParent() instanceof UserTask) {
//			choices.add("potentialOwner");
//			choices.add("humanPerformer");
//		} else {
//			choices.add("performer");
//		}
		choices.add("Pre Condition");
		choices.add("Post Condition");
		
		
		typePanel = new XMLComboPanel(pc, owner, "Condition", choices, false, false, true);
		JPanel idTypePanel = new JPanel();
		idTypePanel.setLayout(new BoxLayout(idTypePanel, BoxLayout.X_AXIS));
		idTypePanel.add(new XMLTextPanel(pc, owner.get("id"), false));
		idTypePanel.add(Box.createHorizontalGlue());
		idTypePanel.add(typePanel);
		this.add(idTypePanel);

		namePanel = new XMLTextPanel(pc, owner.get("name"));
		resourceRefPanel = new XMLComboPanel(pc, owner.getResourceAssignTypes(), "resourceRef", BPMNModelUtils.getDefinitions(owner).getResources(), true, false, true);
		JPanel nameRefPanel = new JPanel();
		nameRefPanel.setLayout(new BoxLayout(nameRefPanel, BoxLayout.X_AXIS));
		nameRefPanel.add(namePanel);
		nameRefPanel.add(Box.createHorizontalGlue());
//		nameRefPanel.add(resourceRefPanel);
		this.add(nameRefPanel);
		
		expressionPanel = new XMLChoosenPanel(pc, owner, owner.getResourceAssignTypes().getChoosen(), 360, 200);
		this.add(expressionPanel);
		Panel choosenPanel = expressionPanel.getChoosenPanel();
		if (choosenPanel instanceof XMLTablePanel) {
			if (owner.getResourceAssignTypes().getResourceParameterBindings().size() > 0) {
				resourceRefPanel.setEnabled(false);
			}
		}
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Object sel = resourceRefPanel.getSelectedItem();
				XMLElement fn = null;
				if (sel instanceof XMLElement) {
					fn = (XMLElement) sel;
				}
				expressionPanel.setChoosen(fn);
				expressionPanel.repaint();
			}
		};
		resourceRefPanel.addActionListener(al);
	}
	
	public final XMLComboPanel getResourceRefPanel() {
		return resourceRefPanel;
	}

	public void saveObjects() {
		namePanel.saveObjects();
		resourceRefPanel.saveObjects();
		expressionPanel.saveObjects();
//		String type = (String) typePanel.getSelectedItem();
//		if (type.equals(mxResources.get("potentialOwner"))) {
//			((ResourceRole) owner).setElementName("potentialOwner");
//		} else if (type.equals(mxResources.get("humanPerformer"))) {
//			((ResourceRole) owner).setElementName("humanPerformer");
//		} else if (type.equals(mxResources.get("performer"))) {
//			((ResourceRole) owner).setElementName("performer");
//		}
		String type = (String) typePanel.getSelectedItem();
		if (type.equals("Pre Condition")) {
			((ResourceRole) owner).setElementName("PreCondition");
		} else if (type.equals("Post Condition")) {
			((ResourceRole) owner).setElementName("PostCondition");
		} 
		super.saveObjects();
	}

}
