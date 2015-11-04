package org.yaoqiang.bpmn.editor.swing;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;

import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.action.EditorActions;
import org.yaoqiang.bpmn.editor.action.ModelActions;
import org.yaoqiang.graph.action.GraphActions;
import org.yaoqiang.graph.swing.GraphComponent;
import org.yaoqiang.graph.view.Graph;

import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxResources;

/**
 * OrganizationPopupMenu
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class OrganizationPopupMenu extends JPopupMenu {

	private static final long serialVersionUID = -3132749140550242191L;

	protected BPMNEditor editor;

	protected Graph graph;

	protected mxGraphModel model;

	protected Object cell;

	protected void populateAlignMenu() {
		addSeparator();

		JMenu alignmenu = (JMenu) add(new JMenu(mxResources.get("align")));
		alignmenu.add(editor.bind(mxResources.get("alignleft"), GraphActions.getAction(GraphActions.ALIGN_LEFT),
				"/org/yaoqiang/bpmn/editor/images/alignleft.gif"));
		alignmenu.add(editor.bind(mxResources.get("aligncenter"), GraphActions.getAction(GraphActions.ALIGN_CENTER),
				"/org/yaoqiang/bpmn/editor/images/aligncenter.gif"));
		alignmenu.add(editor.bind(mxResources.get("alignright"), GraphActions.getAction(GraphActions.ALIGN_RIGHT),
				"/org/yaoqiang/bpmn/editor/images/alignright.gif"));

		alignmenu.addSeparator();

		alignmenu
				.add(editor.bind(mxResources.get("aligntop"), GraphActions.getAction(GraphActions.ALIGN_TOP), "/org/yaoqiang/bpmn/editor/images/aligntop.gif"));
		alignmenu.add(editor.bind(mxResources.get("alignmiddle"), GraphActions.getAction(GraphActions.ALIGN_MIDDLE),
				"/org/yaoqiang/bpmn/editor/images/alignmiddle.gif"));
		alignmenu.add(editor.bind(mxResources.get("alignbottom"), GraphActions.getAction(GraphActions.ALIGN_BOTTOM),
				"/org/yaoqiang/bpmn/editor/images/alignbottom.gif"));

		alignmenu.addSeparator();

		alignmenu.add(editor.bind(mxResources.get("sameheight"), GraphActions.getAction(GraphActions.SAME_HEIGHT),
				"/org/yaoqiang/bpmn/editor/images/sameheight.gif"));
		alignmenu.add(editor.bind(mxResources.get("samewidth"), GraphActions.getAction(GraphActions.SAME_WIDTH),
				"/org/yaoqiang/bpmn/editor/images/samewidth.gif"));
		alignmenu.add(editor.bind(mxResources.get("samesize"), GraphActions.getAction(GraphActions.SAME), "/org/yaoqiang/bpmn/editor/images/samesize.gif"));

		alignmenu.addSeparator();

		alignmenu.add(editor.bind(mxResources.get("distributeH"), GraphActions.getAction(GraphActions.DISTRIBUTE_HORIZONTALLY),
				"/org/yaoqiang/bpmn/editor/images/distribute_horizontally.png"));
		alignmenu.add(editor.bind(mxResources.get("distributeV"), GraphActions.getAction(GraphActions.DISTRIBUTE_VERTICALLY),
				"/org/yaoqiang/bpmn/editor/images/distribute_vertically.png"));
	}

	protected void populateViewMenu() {
		JMenu submenu = (JMenu) add(new JMenu(mxResources.get("zoom")));
		submenu.setIcon(new ImageIcon(OrganizationPopupMenu.class.getResource("/org/yaoqiang/bpmn/editor/images/zoom.png")));

		submenu.add(editor.bind("400%", GraphActions.getScaleAction(4)));
		submenu.add(editor.bind("200%", GraphActions.getScaleAction(2)));
		submenu.add(editor.bind("150%", GraphActions.getScaleAction(1.5)));
		submenu.add(editor.bind("100%", GraphActions.getScaleAction(1)));
		submenu.add(editor.bind("75%", GraphActions.getScaleAction(0.75)));
		submenu.add(editor.bind("50%", GraphActions.getScaleAction(0.5)));

		submenu.addSeparator();

		submenu.add(editor.bind(mxResources.get("page"), GraphActions.getAction(GraphActions.ZOOM_FIT_PAGE)));
		submenu.add(editor.bind(mxResources.get("width"), GraphActions.getAction(GraphActions.ZOOM_FIT_WIDTH)));
		submenu.add(editor.bind(mxResources.get("custom"), GraphActions.getAction(GraphActions.ZOOM_CUSTOM)));

		addSeparator();

		add(editor.bind(mxResources.get("zoomIn"), GraphActions.getAction(GraphActions.ZOOM_IN), "/org/yaoqiang/bpmn/editor/images/zoom_in.png"));
		add(editor.bind(mxResources.get("actualSize"), GraphActions.getAction(GraphActions.ZOOM_ACTUAL), "/org/yaoqiang/bpmn/editor/images/zoomactual.png"));
		add(editor.bind(mxResources.get("zoomOut"), GraphActions.getAction(GraphActions.ZOOM_OUT), "/org/yaoqiang/bpmn/editor/images/zoom_out.png"));

		if (cell == null) {
			addSeparator();
			JMenu addmenu = (JMenu) add(new JMenu(mxResources.get("addPage")));
			addmenu.add(editor.bind(mxResources.get("horizontal"), GraphActions.getAddPageAction(true)));
			addmenu.add(editor.bind(mxResources.get("vertical"), GraphActions.getAddPageAction(false)));
			JMenu removemenu = (JMenu) add(new JMenu(mxResources.get("removePage")));
			removemenu.add(editor.bind(mxResources.get("horizontal"), GraphActions.getRemovePageAction(true)));
			removemenu.add(editor.bind(mxResources.get("vertical"), GraphActions.getRemovePageAction(false)));
			addSeparator();
			add(editor.bind(mxResources.get("selectAll"), GraphActions.getAction(GraphActions.SELECT_ALL), "/org/yaoqiang/bpmn/editor/images/select_all.png"));
		}
	}

	protected void populateEditMenu() {
		add(editor.bind(mxResources.get("editLabel"), GraphActions.getAction(GraphActions.EDIT), "/org/yaoqiang/bpmn/editor/images/edit.png"));
		if (graph.getModel().isEdge(cell)) {
			add(editor.bind(mxResources.get("delete"), GraphActions.getAction(GraphActions.DELETE), "/org/yaoqiang/bpmn/editor/images/delete.png"));
		} else {
			add(editor.bind(mxResources.get("cut"), TransferHandler.getCutAction(), "/org/yaoqiang/bpmn/editor/images/cut.png"));
			add(editor.bind(mxResources.get("copy"), TransferHandler.getCopyAction(), "/org/yaoqiang/bpmn/editor/images/copy.png"));
			add(editor.bind(mxResources.get("paste"), GraphActions.getAction(GraphActions.PASTE), "/org/yaoqiang/bpmn/editor/images/paste.png"));
			add(editor.bind(mxResources.get("delete"), GraphActions.getAction(GraphActions.DELETE), "/org/yaoqiang/bpmn/editor/images/delete.png"));
		}
	}

	protected void populateStyleMenu() {
		if (graph.getModel().isEdge(cell)) {
			addSeparator();
			JMenu edgemenu = (JMenu) add(new JMenu(mxResources.get("edgeStyle")));
			edgemenu.add(editor.bind(mxResources.get("straight"), GraphActions.getStyleAction("straight"), "/org/yaoqiang/bpmn/editor/images/straight.gif"));
			edgemenu.add(editor.bind(mxResources.get("curve"), GraphActions.getStyleAction("curve"), "/org/yaoqiang/bpmn/editor/images/curve.gif"));
			edgemenu.add(editor.bind(mxResources.get("horizontal"), GraphActions.getStyleAction("horizontal"), "/org/yaoqiang/bpmn/editor/images/connect.gif"));
			edgemenu.add(editor.bind(mxResources.get("vertical"), GraphActions.getStyleAction("vertical"), "/org/yaoqiang/bpmn/editor/images/vertical.gif"));
			edgemenu.addSeparator();
			edgemenu.add(editor.bind(mxResources.get("resetEdgeStyle"), GraphActions.getStyleAction("resetEdgeStyle")));
			addSeparator();
		} else {
			if (graph.getSelectionCount() > 1) {
				populateAlignMenu();
			}
		}

		addSeparator();
	}

	protected void populateWhitespaceMenu() {
		addSeparator();
		add(editor.bind(mxResources.get("save"), ModelActions.getSaveOrganizationAction(), "/org/yaoqiang/bpmn/editor/images/save.png"));
		add(editor.bind(mxResources.get("Export"), ModelActions.getExportOrganizationAction(), "/org/yaoqiang/bpmn/editor/images/export.png"));
		add(editor.bind(mxResources.get("paste"), GraphActions.getAction(GraphActions.PASTE), "/org/yaoqiang/bpmn/editor/images/paste.png"));
		addSeparator();
		add(editor.bind(mxResources.get("backgroundColor"), GraphActions.getAction(GraphActions.BACKGROUND), "/org/yaoqiang/bpmn/editor/images/fillcolor.gif"));
		addSeparator();
	}

	public OrganizationPopupMenu(BPMNEditor editor, GraphComponent graphComponent) {
		this.editor = editor;
		this.graph = graphComponent.getGraph();
		this.model = graph.getModel();
		this.cell = graph.getSelectionCell();

		add(editor.bind(mxResources.get("undo"), EditorActions.getAction(EditorActions.UNDO), "/org/yaoqiang/bpmn/editor/images/undo.png"));

		if (cell != null) {

			addSeparator();
			
			populateEditMenu();

			populateStyleMenu();

		} else {
			populateWhitespaceMenu();
		}

		populateViewMenu();
	}
}
