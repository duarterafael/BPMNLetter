package org.yaoqiang.bpmn.editor.swing;

import javax.swing.BorderFactory;
import javax.swing.JToolBar;

import org.yaoqiang.bpmn.editor.action.EditorActions;
import org.yaoqiang.graph.action.GraphActions;

import com.mxgraph.util.mxResources;


/**
 * AlignToolBar
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class AlignToolBar extends JToolBar {

	private static final long serialVersionUID = -8015443128436394471L;

	public AlignToolBar(final BaseEditor editor, int orientation) {
		super(orientation);
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3), getBorder()));
		setFloatable(false);

		add(editor.bind(mxResources.get("autolayout"), GraphActions.getAutoLayoutAction(), "/org/yaoqiang/bpmn/editor/images/auto_layout.png"));
		add(editor.bind(mxResources.get("rotateSwimlane"), GraphActions.getAction(GraphActions.ROTATE_SWIMLANE),
				"/org/yaoqiang/bpmn/editor/images/rotate_swimlane.png"));

		addSeparator();

		add(editor.bind(mxResources.get("alignleft"), GraphActions.getAction(GraphActions.ALIGN_LEFT), "/org/yaoqiang/bpmn/editor/images/alignleft.gif"));
		add(editor.bind(mxResources.get("aligncenter"), GraphActions.getAction(GraphActions.ALIGN_CENTER), "/org/yaoqiang/bpmn/editor/images/aligncenter.gif"));
		add(editor.bind(mxResources.get("alignright"), GraphActions.getAction(GraphActions.ALIGN_RIGHT), "/org/yaoqiang/bpmn/editor/images/alignright.gif"));

		addSeparator();

		add(editor.bind(mxResources.get("aligntop"), GraphActions.getAction(GraphActions.ALIGN_TOP), "/org/yaoqiang/bpmn/editor/images/aligntop.gif"));
		add(editor.bind(mxResources.get("alignmiddle"), GraphActions.getAction(GraphActions.ALIGN_MIDDLE), "/org/yaoqiang/bpmn/editor/images/alignmiddle.gif"));
		add(editor.bind(mxResources.get("alignbottom"), GraphActions.getAction(GraphActions.ALIGN_BOTTOM), "/org/yaoqiang/bpmn/editor/images/alignbottom.gif"));

		addSeparator();

		add(editor.bind(mxResources.get("sameheight"), GraphActions.getAction(GraphActions.SAME_HEIGHT), "/org/yaoqiang/bpmn/editor/images/sameheight.gif"));
		add(editor.bind(mxResources.get("samewidth"), GraphActions.getAction(GraphActions.SAME_WIDTH), "/org/yaoqiang/bpmn/editor/images/samewidth.gif"));
		add(editor.bind(mxResources.get("samesize"), GraphActions.getAction(GraphActions.SAME), "/org/yaoqiang/bpmn/editor/images/samesize.gif"));

		addSeparator();

		add(editor.bind(mxResources.get("distributeH"), GraphActions.getAction(GraphActions.DISTRIBUTE_HORIZONTALLY),
				"/org/yaoqiang/bpmn/editor/images/distribute_horizontally.png"));
		add(editor.bind(mxResources.get("distributeV"), GraphActions.getAction(GraphActions.DISTRIBUTE_VERTICALLY),
				"/org/yaoqiang/bpmn/editor/images/distribute_vertically.png"));

		addSeparator();

		add(editor.bind(mxResources.get("grid"), EditorActions.getAction(EditorActions.GRID), "/org/yaoqiang/bpmn/editor/images/grid.png"));
		add(editor.bind(mxResources.get("zoomIn"), GraphActions.getAction(GraphActions.ZOOM_IN), "/org/yaoqiang/bpmn/editor/images/zoom_in.png"));
		add(editor.bind(mxResources.get("actualSize"), GraphActions.getAction(GraphActions.ZOOM_ACTUAL), "/org/yaoqiang/bpmn/editor/images/zoomactual.png"));
		add(editor.bind(mxResources.get("zoomOut"), GraphActions.getAction(GraphActions.ZOOM_OUT), "/org/yaoqiang/bpmn/editor/images/zoom_out.png"));
		add(editor.bind(mxResources.get("page"), GraphActions.getAction(GraphActions.ZOOM_FIT_PAGE), "/org/yaoqiang/bpmn/editor/images/zoomfitpage.gif"));
		add(editor.bind(mxResources.get("width"), GraphActions.getAction(GraphActions.ZOOM_FIT_WIDTH), "/org/yaoqiang/bpmn/editor/images/zoomfitwidth.gif"));

	}
}
