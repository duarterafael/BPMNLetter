package org.yaoqiang.bpmn.editor.dialog.tree;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.dialog.PanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.view.BPMNGraph;

import com.mxgraph.model.mxCell;

/**
 * OrganizationTreePanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class OrganizationTreePanel extends XMLPanel {

	private static final long serialVersionUID = -7257047844739551708L;

	protected BPMNGraph graph;

	protected TreePath selectedPath;

	public OrganizationTreePanel(PanelContainer pc, BPMNEditor owner, String type) {
		super(pc, null);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		graph = owner.getOrgGraphComponent().getGraph();
		mxCell root = (mxCell) graph.getDefaultParent();
		final JTree tree = new JTree(new OrganizationTreeModel(new OrganizationTreeNode(root, graph, type)));
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setCellRenderer(new OrganizationTreeCellRenderer());
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				selectedPath = tree.getSelectionModel().getSelectionPath();
			}
		});
		tree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				int xClick = me.getX();
				int yClick = me.getY();
				TreePath path = tree.getPathForLocation(xClick, yClick);

				if (path != null) {
					tree.setAnchorSelectionPath(path);
					if (me.getClickCount() > 1 && !SwingUtilities.isRightMouseButton(me)) {
						getDialog().getPanelContainer().apply();
						getDialog().dispose();
					} else if (SwingUtilities.isRightMouseButton(me)) {
						if (!tree.isPathSelected(path)) {
							tree.setSelectionPath(path);
						}
					}
				} else {
					TreePath close = tree.getClosestPathForLocation(xClick, yClick);
					Rectangle rect = tree.getPathBounds(close);
					if (tree.getSelectionPath() != close || rect == null || !(rect.y < yClick && rect.y + rect.height > yClick)) {
						tree.clearSelection();
					}
				}

				tree.getParent().requestFocus();
			}
		});
		JScrollPane scrollPane = new JScrollPane(tree);
		scrollPane.setPreferredSize(new Dimension(300, 300));
		this.add(scrollPane);

	}

}
