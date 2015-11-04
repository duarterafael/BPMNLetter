package org.yaoqiang.bpmn.editor.view;

import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.graph.view.Graph;

import com.mxgraph.model.mxGraphModel;

/**
 * BPMNGraph
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class BPMNGraph extends Graph {

	public BPMNGraph(mxGraphModel model) {
		super(model);
	}

	public boolean isCellEditable(Object cell) {
		return BPMNEditor.isSimulationStopped() && super.isCellEditable(cell);
	}

	public boolean isCellSelectable(Object cell) {
		return BPMNEditor.isSimulationStopped() && super.isCellSelectable(cell);
	}

	public boolean isCellConnectable(Object cell) {
		return BPMNEditor.isSimulationStopped() && super.isCellConnectable(cell);
	}

	public boolean isCellMovable(Object cell) {
		return BPMNEditor.isSimulationStopped() && super.isCellMovable(cell);
	}

}
