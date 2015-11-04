package org.yaoqiang.bpmn.editor.dialog.panels;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.yaoqiang.bpmn.editor.dialog.PanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLComboPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTextPanel;
import org.yaoqiang.bpmn.model.elements.core.common.SequenceFlow;
import org.yaoqiang.bpmn.model.elements.core.foundation.BaseElement;
import org.yaoqiang.bpmn.model.elements.data.Property;
import org.yaoqiang.bpmn.model.elements.events.BoundaryEvent;
import org.yaoqiang.bpmn.model.elements.gateways.EventBasedGateway;

/**
 * PredefinedPropertyPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class PredefinedPropertyPanel extends XMLPanel {

	private static final long serialVersionUID = 3660379499749972497L;

	protected XMLPanel valuePanel;

	public PredefinedPropertyPanel(PanelContainer pc, BaseElement owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel idNamePanel = new JPanel();
		idNamePanel.setLayout(new BoxLayout(idNamePanel, BoxLayout.X_AXIS));
		idNamePanel.add(new XMLTextPanel(pc, owner.get("id"), false));
		idNamePanel.add(new XMLTextPanel(pc, owner.get("name"), false));

		this.add(idNamePanel);

		JPanel valueTypePanel = new JPanel();
		valueTypePanel.setLayout(new BoxLayout(valueTypePanel, BoxLayout.X_AXIS));
		if (owner instanceof Property) {
			valueTypePanel.add(new XMLTextPanel(pc, owner.get("itemSubjectRef"), false));
		}
		if (owner instanceof SequenceFlow) {
			this.add(new ExpressionPanel(pc, ((SequenceFlow) owner).getConditionExpression(), null, 260, 100, false));
			List<String> choices = new ArrayList<String>();
			choices.add("True");
			choices.add("False");
			choices.add("Dynamic");
			valuePanel = new XMLComboPanel(pc, owner.get("yaoqiang:value"), "Result", choices, false, false, true);
		} else if (owner instanceof BoundaryEvent) {
			List<String> choices = new ArrayList<String>();
			choices.add("True");
			choices.add("False");
			valuePanel = new XMLComboPanel(pc, owner.get("yaoqiang:value"), "Triggered", choices, false, false, true);
		} else if (owner instanceof EventBasedGateway) {
			valuePanel = new XMLComboPanel(pc, owner.get("yaoqiang:value"), "Path", ((EventBasedGateway) owner).getOutgoingSequenceFlows(), false, false, true);
		} else {
			valuePanel = new XMLTextPanel(pc, owner.get("yaoqiang:value"), "Value");
		}
		valueTypePanel.add(valuePanel);
		this.add(valueTypePanel);
	}

	public void saveObjects() {
		valuePanel.saveObjects();
		super.saveObjects();
	}

}
