package org.yaoqiang.bpmn.editor.swing.handler;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.yaoqiang.bpmn.editor.action.ModelActions;
import org.yaoqiang.graph.swing.GraphComponent;

import com.mxgraph.swing.handler.mxKeyboardHandler;

/**
 * BPMNKeyboardHandler
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class BPMNKeyboardHandler extends mxKeyboardHandler {

	public BPMNKeyboardHandler(GraphComponent graphComponent) {
		super(graphComponent);
	}

	protected InputMap getInputMap(int condition) {
		InputMap map = super.getInputMap(condition);

		if (condition == JComponent.WHEN_FOCUSED && map != null) {
			map.put(KeyStroke.getKeyStroke("ENTER"), "addOU");
		}

		return map;
	}

	/**
	 * Return the mapping between JTree's input map and JGraph's actions.
	 */
	protected ActionMap createActionMap() {
		ActionMap map = super.createActionMap();

		map.put("addOU", ModelActions.getAddOrganizationAction("same"));

		return map;
	}

}
