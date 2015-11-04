package org.yaoqiang.bpmn.editor.view;

import javax.swing.JOptionPane;

import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.swing.BPMNGraphComponent;
import org.yaoqiang.graph.model.GraphModel;
import org.yaoqiang.graph.util.GraphUtils;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;

/**
 * OrganizationManager
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class OrganizationManager extends mxEventSource {

	protected BPMNEditor editor;

	protected BPMNGraphComponent orgGraphComponent;

	protected BPMNGraph graph;

	protected GraphModel model;

	public OrganizationManager(BPMNEditor editor) {
		if (this.graph != null) {
			this.graph.removeListener(handler);
		}

		this.editor = editor;
		this.orgGraphComponent = editor.getOrgGraphComponent();
		this.graph = orgGraphComponent.getGraph();
		this.model = graph.getModel();

		if (this.graph != null) {
			this.orgGraphComponent.getConnectionHandler().addListener(mxEvent.CONNECT, handler);
			this.graph.addListener(mxEvent.CELLS_ADDED, handler);
			this.graph.addListener(mxEvent.CELLS_REMOVED, handler);
			this.graph.addListener(mxEvent.CHANGE, handler);
			this.graph.addListener(mxEvent.LABEL_CHANGED, handler);
			this.graph.addListener(mxEvent.CELLS_MOVED, handler);
		}
	}

	protected mxIEventListener handler = new mxIEventListener() {
		public void invoke(Object source, mxEventObject evt) {
			String eventName = evt.getName();
			mxCell cell = (mxCell) graph.getSelectionCell();
			if (cell == null) {
				Object[] cells = (Object[]) evt.getProperty("cells");
				if (cells != null && cells.length > 0) {
					cell = (mxCell) cells[0];
				}
			}
			try {
				if (eventName.equals(mxEvent.CONNECT)) {
					if (model.isEdge(cell) && model.getStyle(cell).length() == 0) {
						GraphUtils.setEdgeStyle(graph, cell);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane
						.showMessageDialog(null, e.getStackTrace(), "Please Capture This Error Screen Shots and Submit this BUG.", JOptionPane.ERROR_MESSAGE);
			}
		}
	};


	public final mxIEventListener getHandler() {
		return handler;
	}

}
