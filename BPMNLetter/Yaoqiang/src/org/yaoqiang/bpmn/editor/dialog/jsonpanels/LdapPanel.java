package org.yaoqiang.bpmn.editor.dialog.jsonpanels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.action.ModelActions;
import org.yaoqiang.bpmn.editor.dialog.JSONPanel;
import org.yaoqiang.bpmn.editor.dialog.JSONTablePanel;
import org.yaoqiang.bpmn.editor.dialog.JSONTextPanel;
import org.yaoqiang.bpmn.editor.dialog.PanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLTablePanel;
import org.yaoqiang.bpmn.editor.dialog.ldaptree.LdapTreeCellRenderer;
import org.yaoqiang.bpmn.editor.dialog.ldaptree.LdapTreeModel;
import org.yaoqiang.bpmn.editor.dialog.ldaptree.LdapTreeNode;
import org.yaoqiang.bpmn.editor.util.BPMNEditorConstants;
import org.yaoqiang.bpmn.editor.util.LdapUtils;

import com.mxgraph.util.mxResources;

/**
 * LdapPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class LdapPanel extends JSONPanel {

	private static final long serialVersionUID = -1141237370112119150L;

	protected JSONTextPanel connectionPanel;

	protected JTree tree;

	protected LdapTreeModel treeModel;

	protected LdapTreeNode rootNode;

	protected TreePath selectedPath;

	protected JToolBar toolbar;

	protected JPopupMenu popup = new JPopupMenu();

	protected LinkedHashMap<String, String> choiceMap = new LinkedHashMap<String, String>();

	public LdapPanel(final PanelContainer pc, final BPMNEditor owner) {
		super(pc, owner);
		this.setLayout(new BorderLayout());

		connectionPanel = new JSONTextPanel(null, null, "connection", false, 100, 26, false);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		topPanel.add(connectionPanel);
		topPanel.add(createToolbar());
		this.add(topPanel, BorderLayout.NORTH);

		treeModel = new LdapTreeModel(new LdapTreeNode("", null));
		tree = new JTree(treeModel);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setCellRenderer(new LdapTreeCellRenderer());
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
					if (me.getClickCount() > 1 && !SwingUtilities.isRightMouseButton(me) && ((LdapTreeNode) path.getLastPathComponent()).isLeaf()) {
						editAction.actionPerformed(null);
					} else if (SwingUtilities.isRightMouseButton(me)) {
						if (!tree.isPathSelected(path)) {
							tree.setSelectionPath(path);
						}
						JPopupMenu popup = createTreePopupMenu();
						popup.show(tree, me.getX(), me.getY());
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
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.add(scrollPane);
	}

	public void resetTreeModel(LdapTreeNode[] nodes) {
		rootNode = nodes[0];
		tree.setModel(new LdapTreeModel(rootNode));
		if (tree.getModel().getRoot().toString() != null) {
			tree.setRootVisible(true);
			if (nodes[1] != null) {
				if (nodes[1].isLeaf()) {
					tree.scrollPathToVisible(new TreePath(nodes[1].getPath()));
				} else {
					tree.expandPath(new TreePath(nodes[1].getPath()));
				}
			}
		}
	}

	public JSONTextPanel getConnectionPanel() {
		return connectionPanel;
	}

	public LdapTreeNode getRootNode() {
		return rootNode;
	}

	public LdapTreeNode getSelectedNode() {
		if (selectedPath != null) {
			Object[] nodes = selectedPath.getPath();
			return (LdapTreeNode) nodes[nodes.length - 1];
		}
		return null;
	}

	public String getNodeDN(TreePath nodePath) {
		if (nodePath == null) {
			nodePath = selectedPath;
		}
		if (nodePath != null) {
			String dn = null;
			for (Object path : nodePath.getPath()) {
				dn = path + (dn == null ? "" : "," + dn);
			}
			return dn;
		}
		return "";
	}

	public JTree getTree() {
		return tree;
	}

	protected JToolBar createToolbar() {
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.add(createToolbarButton(connectAction));
		toolbar.addSeparator();
		toolbar.add(createToolbarButton(importAction));
		List<String> choices = new ArrayList<String>();
		choices.add("o");
		choices.add("ou");
		choices.add("role");
		choices.add("person");
		choices.add("group");
		toolbar.add(createToolbarPopupButton(choices, newAction));
		toolbar.add(createToolbarButton(editAction));
		toolbar.add(createToolbarButton(deleteAction));
		toolbar.addSeparator();
		toolbar.add(createToolbarButton(searchAction));
		toolbar.add(createToolbarButton(cacheAction));
		toolbar.add(createToolbarButton(exportAction));
		return toolbar;
	}

	protected Action connectAction = new AbstractAction("connect") {

		private static final long serialVersionUID = 1792018887878433986L;

		public void actionPerformed(ActionEvent e) {
			((BPMNEditor) owner).createDialog().initDialog(false, "connect").editObject(owner, "connectLdap");
		}

	};

	protected Action deleteAction = new AbstractAction("delete") {

		private static final long serialVersionUID = 1792018887878433986L;

		public void actionPerformed(ActionEvent e) {
			if (selectedPath != null) {
				String dn = null;
				for (Object path : selectedPath.getPath()) {
					dn = path + (dn == null ? "" : "," + dn);
				}
				int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete Entry '" + dn + "'?", "Confirm Entry Delete",
						JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION) {
					JSONObject con = ((BPMNEditor) owner).getCurrentLdapConnection();
					LdapUtils.deleteLdapEntry(con, dn);

					Map<String, Attributes> entries = LdapUtils.searchLdap(con);
					BPMNEditor.setLdapEntries(entries);
					Map<String, LdapTreeNode> nodes = LdapUtils.buildLdapTreeNodes(con.optString("baseDN"), entries);
					String pdn = dn.substring(dn.indexOf(",") + 1);
					((BPMNEditor) owner).resetLdapTree(new LdapTreeNode[] { nodes.get(con.optString("baseDN")), nodes.get(pdn) });
				}
			}
		}

	};

	protected Action importAction = new AbstractAction("import") {

		private static final long serialVersionUID = 8893423204749184048L;

		public void actionPerformed(ActionEvent e) {
			ModelActions.getImportLdifAction().actionPerformed(e);
		}

	};

	protected Action exportAction = new AbstractAction("export") {

		private static final long serialVersionUID = 8893423204749184048L;

		public void actionPerformed(ActionEvent e) {
			ModelActions.getExportLdifAction().actionPerformed(e);
		}

	};

	protected Action searchAction = new AbstractAction("search") {

		private static final long serialVersionUID = 1792018887878433986L;

		public void actionPerformed(ActionEvent e) {
			((BPMNEditor) owner).createDialog().initDialog(false, "search").editObject(owner, "searchLdap");
		}

	};

	protected Action cacheAction = new AbstractAction("cache") {

		private static final long serialVersionUID = 8893423204749184048L;

		public void actionPerformed(ActionEvent e) {
			ModelActions.getCacheLdapAction().actionPerformed(e);
		}

	};

	protected Action newAction = new AbstractAction("new") {

		private static final long serialVersionUID = 1290796318136400480L;

		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource() instanceof JButton) {
				JButton btn = (JButton) ae.getSource();
				popup.show(btn.getParent(), btn.getX(), btn.getY() + btn.getHeight());
			} else {
				JMenuItem selected = (JMenuItem) ae.getSource();
				String sel = selected.getText();
				String type = choiceMap.get(sel);
				JSONObject obj = new JSONObject();
				try {
					obj.putOpt("action", "new");
					obj.putOpt("type", type);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				((BPMNEditor) owner).createDialog().initDialog().editObject(obj, "ldapEntry");
			}
		}

	};

	protected Action editAction = new AbstractAction("edit") {

		private static final long serialVersionUID = -6897615642201665039L;

		public void actionPerformed(ActionEvent ae) {
			String dn = ((BPMNEditor) owner).getLdapSelectedEntry();
			Attributes entry = BPMNEditor.getLdapEntries().get(dn);
			if (entry != null) {
				JSONObject obj = new JSONObject();
				try {
					obj.putOpt("action", "edit");
					obj.putOpt("dn", dn);
					NamingEnumeration<? extends Attribute> attrs = entry.getAll();
					while (attrs.hasMore()) {
						Attribute attr = attrs.next();
						if ("objectClass".equals(attr.getID()) || "member".equals(attr.getID()) || "roleOccupant".equals(attr.getID())) {
							Enumeration<?> values = attr.getAll();
							while (values.hasMoreElements()) {
								obj.append(attr.getID(), values.nextElement());
							}
						} else {
							obj.putOpt(attr.getID(), attr.get());
						}
					}
					String type = null;
					String objectClass = obj.optString("objectClass").toLowerCase();
					if (objectClass.indexOf("person") > -1) {
						type = "person";
					} else if (objectClass.indexOf("organizationalunit") > -1) {
						type = "ou";
					} else if (objectClass.indexOf("organizationalrole") > -1) {
						type = "role";
					} else if (objectClass.indexOf("organization") > -1) {
						type = "o";
					} else if (objectClass.indexOf("groupofnames") > -1) {
						type = "group";
					}
					obj.putOpt("type", type);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (NamingException e) {
					e.printStackTrace();
				}
				((BPMNEditor) owner).createDialog().initDialog().editObject(obj, "ldapEntry");
			}
		}

	};

	protected Action newPopupAction = new AbstractAction("new") {

		private static final long serialVersionUID = 1290796318136400480L;

		public void actionPerformed(ActionEvent ae) {
			JMenuItem selected = (JMenuItem) ae.getSource();
			String sel = selected.getText();
			String type = choiceMap.get(sel);
			JSONObject obj = new JSONObject();
			try {
				obj.putOpt("action", "new");
				obj.putOpt("type", type);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			((BPMNEditor) owner).createDialog().initDialog().editObject(obj, "ldapEntry");
		}

	};

	protected JPopupMenu createTreePopupMenu() {
		JPopupMenu popup = new JPopupMenu();
		JMenuItem mi = null;

		if (!getSelectedNode().isPerson()) {
			JMenu newMenu = (JMenu) popup.add(new JMenu(mxResources.get("new")));
			newMenu.setIcon(new ImageIcon(XMLTablePanel.class.getResource("/org/yaoqiang/bpmn/editor/images/new.png")));
			mi = newMenu.add(mxResources.get("o"));
			mi.setIcon(new ImageIcon(BPMNEditorConstants.COMPANY_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
			mi.addActionListener(newPopupAction);
			mi = newMenu.add(mxResources.get("ou"));
			mi.setIcon(new ImageIcon(BPMNEditorConstants.OU_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
			mi.addActionListener(newPopupAction);
			mi = newMenu.add(mxResources.get("role"));
			mi.setIcon(new ImageIcon(BPMNEditorConstants.ROLE_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
			mi.addActionListener(newPopupAction);
			mi = newMenu.add(mxResources.get("person"));
			mi.setIcon(new ImageIcon(BPMNEditorConstants.USER_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
			mi.addActionListener(newPopupAction);
			mi = newMenu.add(mxResources.get("group"));
			mi.setIcon(new ImageIcon(BPMNEditorConstants.GROUP_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
			mi.addActionListener(newPopupAction);
		}

		mi = popup.add(mxResources.get("edit"));
		mi.setIcon(new ImageIcon(XMLTablePanel.class.getResource("/org/yaoqiang/bpmn/editor/images/edit.png")));
		mi.addActionListener(editAction);

		mi = popup.add(mxResources.get("delete"));
		mi.setIcon(new ImageIcon(XMLTablePanel.class.getResource("/org/yaoqiang/bpmn/editor/images/delete.png")));
		mi.addActionListener(deleteAction);

		return popup;
	}

	protected JButton createToolbarButton(Action a) {
		String actionName = (String) a.getValue(Action.NAME);
		String toolTip = mxResources.get(actionName);
		ImageIcon curIc = null;
		if ("search".equals(actionName)) {
			toolTip = mxResources.get("SearchLdap");
			curIc = new ImageIcon(JSONTablePanel.class.getResource("/org/yaoqiang/bpmn/editor/images/searchldap.png"));
		} else if ("edit".equals(actionName)) {
			curIc = new ImageIcon(JSONTablePanel.class.getResource("/org/yaoqiang/bpmn/editor/images/edit.png"));
		} else if ("delete".equals(actionName)) {
			curIc = new ImageIcon(JSONTablePanel.class.getResource("/org/yaoqiang/bpmn/editor/images/delete.png"));
		} else if ("connect".equals(actionName)) {
			toolTip = mxResources.get("ConnectLdap");
			curIc = new ImageIcon(JSONTablePanel.class.getResource("/org/yaoqiang/bpmn/editor/images/connect.png"));
		} else if ("import".equals(actionName)) {
			toolTip = mxResources.get("ImportLdif");
			curIc = new ImageIcon(JSONTablePanel.class.getResource("/org/yaoqiang/bpmn/editor/images/import.png"));
		} else if ("export".equals(actionName)) {
			toolTip = mxResources.get("ExportLdif");
			curIc = new ImageIcon(JSONTablePanel.class.getResource("/org/yaoqiang/bpmn/editor/images/export.png"));
		} else if ("cache".equals(actionName)) {
			toolTip = mxResources.get("cacheLdap");
			curIc = new ImageIcon(JSONTablePanel.class.getResource("/org/yaoqiang/bpmn/editor/images/cache.png"));
		}
		JButton b = new JButton(curIc);

		if (!"connect".equals(actionName)) {
			b.setEnabled(false);
		}
		b.setName(actionName);
		b.addActionListener(a);
		b.setToolTipText(toolTip);
		return b;
	}

	public JButton createToolbarPopupButton(List<String> choices, Action a) {
		for (String choice : choices) {
			choiceMap.put(mxResources.get(choice), choice);
			JMenuItem mi = popup.add(mxResources.get(choice));
			if ("ou".equals(choice)) {
				mi.setIcon(new ImageIcon(BPMNEditorConstants.OU_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
			} else if ("person".equals(choice)) {
				mi.setIcon(new ImageIcon(BPMNEditorConstants.USER_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
			} else if ("group".equals(choice)) {
				mi.setIcon(new ImageIcon(BPMNEditorConstants.GROUP_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
			} else if ("role".equals(choice)) {
				mi.setIcon(new ImageIcon(BPMNEditorConstants.ROLE_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
			} else if ("o".equals(choice)) {
				mi.setIcon(new ImageIcon(BPMNEditorConstants.COMPANY_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
			}
			mi.addActionListener(newAction);
		}

		String actionName = (String) a.getValue(Action.NAME);
		JButton b = new JButton(new ImageIcon(XMLTablePanel.class.getResource("/org/yaoqiang/bpmn/editor/images/new.png")));
		b.setEnabled(false);
		b.setName(actionName);
		b.addActionListener(a);
		b.setToolTipText(mxResources.get(actionName));

		return b;
	}

	public void enableToolbar(boolean enable) {
		for (Component com : toolbar.getComponents()) {
			if (!"connect".equals(com.getName())) {
				com.setEnabled(enable);
			}
		}
	}

}
