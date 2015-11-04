package org.yaoqiang.bpmn.editor.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONUtils;
import org.w3c.dom.Document;
import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.action.ModelActions;
import org.yaoqiang.bpmn.editor.dialog.jsonpanels.OrganizationsPanel;
import org.yaoqiang.bpmn.editor.swing.DefaultFileFilter;
import org.yaoqiang.bpmn.editor.util.BPMNEditorConstants;
import org.yaoqiang.bpmn.editor.util.BPMNEditorUtils;
import org.yaoqiang.bpmn.editor.util.EditorUtils;
import org.yaoqiang.bpmn.editor.util.LdapUtils;
import org.yaoqiang.bpmn.editor.util.Utils;
import org.yaoqiang.graph.util.Constants;

import com.mxgraph.io.mxCodec;
import com.mxgraph.util.mxResources;

/**
 * JSONTablePanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class JSONTablePanel extends JSONPanel {

	private static final long serialVersionUID = 1L;

	private JSONPanel source = this;

	protected String type;

	protected boolean hasToolbar = true;

	protected boolean readonly = false;

	protected Vector<String> columnNames;

	protected List<String> columnsToShow;

	protected JPanel toolbox;

	protected JTable table;

	protected JPopupMenu popup = new JPopupMenu();

	protected LinkedHashMap<String, String> choiceMap = new LinkedHashMap<String, String>();

	public JSONTablePanel(PanelContainer pc, Object owner, String type, String title, List<String> columnsToShow, Collection<JSONObject> objectsToShow,
			int width, int height, boolean hasToolbar, boolean readonly) {
		super(pc, owner);
		this.setLayout(new BorderLayout());
		if (title != null) {
			this.setBorder(BorderFactory.createTitledBorder(mxResources.get(title)));
		} else {
			this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		}

		this.type = type;
		this.hasToolbar = hasToolbar;
		this.readonly = readonly;
		this.columnNames = getColumnNames(columnsToShow);
		this.columnsToShow = columnsToShow;
		this.table = createTable();
		setupTable(width, height);
		fillTableContent(objectsToShow);

		if (hasToolbar) {
			add(createToolbar(), BorderLayout.NORTH);
		}
		add(createTablePane(), BorderLayout.CENTER);
	}

	protected JToolBar createToolbar() {
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		if ("organizations".equals(type)) {
			toolbar.add(createToolbarButton(importAction));
		}
		if ("definitionsRepository".equals(type)) {
			List<String> choices = new ArrayList<String>();
			choices.add("bpmnEngine");
			choices.add("nativeRepository");
			toolbar.add(createToolbarPopupButton(choices, popupNewElementAction));
		} else {
			toolbar.add(createToolbarButton(newElementAction));
		}
		toolbar.add(createToolbarButton(editElementAction));
		toolbar.add(createToolbarButton(deleteElementAction));
		return toolbar;
	}

	protected JScrollPane createTablePane() {
		JScrollPane allItemsPane = new JScrollPane();
		allItemsPane.setViewportView(table);
		return allItemsPane;
	}

	protected JTable createTable() {
		return new SortingTable(this, new Vector<Object>(), columnNames) {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int col) {
				return getColumnName(col).length() == 0;
			}

		};
	}

	public boolean setSelectedElement(Object el) {
		int rc = table.getRowCount();
		if (rc > 0) {
			for (int i = 0; i < rc; i++) {
				if (el == table.getValueAt(i, 0)) {
					table.setRowSelectionInterval(i, i);
					JViewport viewport = (JViewport) table.getParent();
					Rectangle rect = table.getCellRect(i, 0, true);
					Point pt = viewport.getViewPosition();
					rect.setLocation(rect.x - pt.x, rect.y - pt.y);
					viewport.scrollRectToVisible(rect);
					return true;
				}
			}
		}
		return false;
	}

	public JSONObject getSelectedObject() {
		int row = table.getSelectedRow();
		if (row >= 0) {
			return (JSONObject) table.getValueAt(row, 0);
		}
		return null;
	}

	protected void setupTable(int width, int height) {
		TableColumn column;
		int count = table.getColumnModel().getColumnCount();
		column = table.getColumnModel().getColumn(0);
		column.setMinWidth(0);
		column.setMaxWidth(0);
		column.setPreferredWidth(0);
		column.setResizable(false);

		for (int i = 1; i < count; i++) {
			column = table.getColumnModel().getColumn(i);
			if (column.getHeaderValue().toString().length() == 0) {
				column.setMinWidth(20);
				column.setMaxWidth(20);
				column.setPreferredWidth(20);
				column.setResizable(false);
			} else if (column.getHeaderValue().toString().equals(mxResources.get("id"))) {
				column.setMinWidth(50);
				column.setPreferredWidth(50);
			} else if (column.getHeaderValue().toString().equals(mxResources.get("version")) || column.getHeaderValue().toString().equals(mxResources.get("revision"))) {
				column.setMaxWidth(60);
				column.setPreferredWidth(60);
			} else if (column.getHeaderValue().toString().equals(mxResources.get("port"))) {
				column.setMinWidth(50);
				column.setMaxWidth(50);
				column.setPreferredWidth(50);
			} else if (column.getHeaderValue().toString().equals(mxResources.get("username"))) {
				column.setMinWidth(50);
				column.setMaxWidth(80);
				column.setPreferredWidth(80);
			} else if (column.getHeaderValue().toString().equals(mxResources.get("host")) || column.getHeaderValue().toString().equals(mxResources.get("author"))) {
				column.setMinWidth(60);
				column.setMaxWidth(150);
				column.setPreferredWidth(100);
			}else if (column.getHeaderValue().toString().equals(mxResources.get("modDate"))) {
				column.setMinWidth(80);
				column.setMaxWidth(150);
				column.setPreferredWidth(150);
			} else if (column.getHeaderValue().toString().equals(mxResources.get("description"))) {
				column.setMinWidth(200);
				column.setPreferredWidth(200);
			}
		}

		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);

		Dimension tDim = new Dimension(width, height);

		table.setPreferredScrollableViewportSize(new Dimension(tDim));

		table.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "edit");
		table.getActionMap().put("edit", editElementAction);

		table.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false), "delete");
		table.getActionMap().put("delete", deleteElementAction);

		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				if (me.getClickCount() > 1) {
					editElementAction.actionPerformed(null);
				}
			}
		});
	}

	protected Action editElementAction = new AbstractAction("edit") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			JSONObject editObject = getSelectedObject();
			if (editObject != null) {
				if ("engineConnections".equals(type)) {
					BPMNEditor.getInstance().createDialog().initDialog("testConnection").editObject(source, editObject, "connection");
				} else if ("ldapConnections".equals(type)) {
					BPMNEditor.getInstance().createDialog().initDialog("testConnection").editObject(source, editObject, "ldapConnection");
				} else if ("organizations".equals(type)) {
					BPMNEditor.getInstance().createDialog().initDialog().editObject(source, editObject, "organization");
				} else if ("definitionsRepository".equals(type) || "versionHistory".equals(type)) {
					InputStream inputStream = null;
					if ("engine".equals(editObject.optString("type"))) {
						inputStream = BPMNEditorUtils.getDeploymentResource(((BPMNEditor) owner).getCurrentConnection(),
								editObject.optString("deploymentId"), editObject.optString("resourceName"));
					} else {
						try {
							PreparedStatement ps = BPMNEditor.dbConn.prepareStatement("SELECT DEF_CONTENT FROM YAO_DEFINITIONS WHERE ID = ?");
							ps.setInt(1, editObject.optInt("id"));
							ResultSet rs = ps.executeQuery();
							while (rs.next()) {
								inputStream = new ByteArrayInputStream(rs.getBytes(1));
								break;
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					if (inputStream != null) {
						ModelActions.getOpenAction(inputStream).actionPerformed(new ActionEvent(((BPMNEditor) owner).getGraphComponent(), 0, ""));												
						if (!((BPMNEditor) owner).isModified()) {
							if ("versionHistory".equals(type)) {
								((BPMNEditor) owner).setRevision(editObject.optInt("revision"));
							} else {
								((BPMNEditor) owner).setRevision(editObject.optInt("version"));
							}
						}
					}
				}
			}
		}
	};

	protected Action deleteElementAction = new AbstractAction("delete") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			JSONObject deleteObject = getSelectedObject();
			if (deleteObject != null) {
				if ("engineConnections".equals(type)) {
					String item = deleteObject.optString("id");
					int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete Item '" + item + "'?", "Confirm Item Delete",
							JOptionPane.YES_NO_OPTION);
					if (option == JOptionPane.YES_OPTION) {
						Map<String, JSONObject> cons = ((BPMNEditor) owner).getConnections();
						cons.remove(item);
						BPMNEditorUtils.removeConnection(item);
						int row = getObjectRow(deleteObject);
						if (row != -1) {
							removeRow(row);
						}
					}
				} else if ("ldapConnections".equals(type)) {
					String item = deleteObject.optString("id");
					int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete Item '" + item + "'?", "Confirm Item Delete",
							JOptionPane.YES_NO_OPTION);
					if (option == JOptionPane.YES_OPTION) {
						Map<String, JSONObject> cons = ((BPMNEditor) owner).getLdapConnections();
						cons.remove(item);
						LdapUtils.removeLdapConnection(item);
						int row = getObjectRow(deleteObject);
						if (row != -1) {
							removeRow(row);
						}
					}
				} else if ("definitionsRepository".equals(type)) {
					if ("engine".equals(deleteObject.optString("type"))) {
						String item = deleteObject.optString("name");
						int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the deployment \nthat contains process definition '"
								+ item + "'?", "Confirm Item Delete", JOptionPane.YES_NO_OPTION);
						if (option == JOptionPane.YES_OPTION) {
							BPMNEditorUtils.deleteDeployment(((BPMNEditor) owner).getCurrentConnection(), deleteObject.optString("deploymentId"));
							((BPMNEditor) owner).refreshDefinitionsRepositoryPanel("engine");
						}
					} else {
						String item = deleteObject.optString("name");
						int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the process definition '"
								+ item + "' from Native Repository?", "Confirm Item Delete", JOptionPane.YES_NO_OPTION);
						if (option == JOptionPane.YES_OPTION) {
							try {
								PreparedStatement ps = BPMNEditor.dbConn.prepareStatement("DELETE FROM YAO_DEFINITIONS WHERE DEF_ID = ?");
								ps.setString(1, deleteObject.optString("def_id"));
								ps.executeUpdate();
							} catch (SQLException e) {
								e.printStackTrace();
							}
							((BPMNEditor) owner).refreshDefinitionsRepositoryPanel("native");
						}						
					}
				} else if ("organizations".equals(type)) {
					if (((BPMNEditor) owner).getOrganizations().size() == 1) {
						return;
					}
					String orgId = deleteObject.optString("id");
					int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete Item '" + orgId + "'?", "Confirm Item Delete",
							JOptionPane.YES_NO_OPTION);
					if (option == JOptionPane.YES_OPTION) {
						List<JSONObject> orgs = ((BPMNEditor) owner).getOrganizations();
						orgs.remove(deleteObject);
						if (deleteObject.equals(((BPMNEditor) owner).getCurrentOrganization())) {
							OrganizationsPanel parent = (OrganizationsPanel) getParent();
							parent.getOrganizationPanel().refreshItem(BPMNEditor.getInstance().getOrganizations());
							Object sel = parent.getOrganizationPanel().getSelectedItem();
							if (sel instanceof JSONObject) {
								try {
									String filepath = Constants.YAOQIANG_USER_HOME + File.separator + BPMNEditorConstants.YAOQIANG_ORGS_DIR
											+ File.separator + ((JSONObject) sel).optString("id") + ".org";
									Document document = EditorUtils.parseXml(new FileInputStream(filepath));
									mxCodec codec = new mxCodec(document);
									codec.decode(document.getDocumentElement(), BPMNEditor.getInstance().getOrgGraphComponent().getGraph().getModel());
								} catch (IOException e1) {
									e1.printStackTrace();
								}
								BPMNEditor.getInstance().setOrgModified(false);
								BPMNEditor.getInstance().getOrgGraphComponent().zoomAndCenter();
								((BPMNEditor) owner).setCurrentOrganization((JSONObject) sel);
								Constants.SETTINGS.put("organization", ((JSONObject) sel).optString("id"));
								Utils.saveConfigureFile();
							}
						}

						String orgfilename = Constants.YAOQIANG_USER_HOME + File.separator + BPMNEditorConstants.YAOQIANG_ORGS_DIR + File.separator
								+ orgId + ".org";
						new File(orgfilename).delete();

						int row = getObjectRow(deleteObject);
						if (row != -1) {
							removeRow(row);
						}

					}
				}
			}
		}
	};

	protected Action importAction = new AbstractAction("import") {

		private static final long serialVersionUID = 3103209859966474668L;
		protected String lastDir;

		public void actionPerformed(ActionEvent e) {
			String wd = (lastDir != null) ? lastDir : System.getProperty("user.dir");
			JFileChooser fc = new JFileChooser(wd);

			FileFilter defaultFilter = new DefaultFileFilter(".ldif", "LDIF " + mxResources.get("file") + " (.ldif)");
			fc.addChoosableFileFilter(defaultFilter);
			fc.setFileFilter(defaultFilter);
			int rc = fc.showDialog(null, mxResources.get("importLdif"));

			if (rc == JFileChooser.APPROVE_OPTION) {
				lastDir = fc.getSelectedFile().getParent();
				String filename = fc.getSelectedFile().getAbsolutePath();
				JSONObject org = LdapUtils.importLDIFtoDiagram((BPMNEditor) owner, filename);
				if (org != null) {
					Component component = (Component) e.getSource();
					while (component != null && !(component instanceof OrganizationsPanel)) {
						component = component.getParent();
					}
					OrganizationsPanel orgPanel = (OrganizationsPanel) component;
					orgPanel.getOrganizationPanel().refreshItem(((BPMNEditor) owner).getOrganizations());
					orgPanel.getOrganizationPanel().setSelectedItem(org);
					JSONTablePanel table = orgPanel.getOrganizationsPanel();
					table.removeAllRows();
					table.fillTableContent(((BPMNEditor) owner).getOrganizations());
				}
			}
		}

	};

	protected Action popupNewElementAction = new AbstractAction("connect") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource() instanceof JButton) {
				JButton btn = (JButton) ae.getSource();
				popup.show(btn.getParent(), btn.getX(), btn.getY() + btn.getHeight());
			} else {
				JMenuItem selected = (JMenuItem) ae.getSource();
				String sel = selected.getText();
				String type = choiceMap.get(sel);
				if (type.equals("bpmnEngine")) {
					((BPMNEditor) owner).createDialog().initDialog(false, "retrieve").editObject(owner, "retrieveProcessDefinitionList");
				} else if (type.equals("nativeRepository")) {
					((BPMNEditor) owner).getConnectionTextPanel().setText(mxResources.get("nativeRepository"));
					((BPMNEditor) owner).refreshDefinitionsRepositoryPanel("native");
				}
			}
		}
	};
	
	protected Action newElementAction = new AbstractAction("new") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			BaseDialog parent = getPanelContainer().getParentDialog();
			if (parent.getParentPanel() != null) {
				getPanelContainer().setModified(true);
			}
			if ("engineConnections".equals(type)) {
				JSONObject obj = new JSONObject(BPMNEditorConstants.ACTIVITI_CONNECTION);
				BPMNEditor.getInstance().createDialog().initDialog("testConnection").editObject(source, obj, "connection");
			} else if ("organizations".equals(type)) {
				JSONObject obj = new JSONObject();
				try {
					obj.putOpt("id", BPMNEditorUtils.generateOrganizationId());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				BPMNEditor.getInstance().createDialog().initDialog().editObject(source, obj, "organization");
			} else if ("ldapConnections".equals(type)) {
				JSONObject obj = new JSONObject(BPMNEditorConstants.LDAP_CONNECTION);
				BPMNEditor.getInstance().createDialog().initDialog("testConnection").editObject(source, obj, "ldapConnection");
			}
		}
	};

	public void fillTableContent(Collection<JSONObject> objectsToShow) {
		removeAllRows();
		if (objectsToShow instanceof Collection) {
			for (JSONObject obj : objectsToShow) {
				((DefaultTableModel) table.getModel()).addRow(getRow(obj));
			}
		}
	}

	public void selectAllElements(boolean select) {
		DefaultTableModel dtm = (DefaultTableModel) table.getModel();
		int count = dtm.getRowCount();
		if (count == 0) {
			return;
		}
		for (int i = 0; i < count; i++) {
			dtm.setValueAt(new Boolean(select), i, 1);
		}
	}

	public List<JSONObject> getAllElements() {
		List<JSONObject> rowElements = new ArrayList<JSONObject>();
		DefaultTableModel dtm = (DefaultTableModel) table.getModel();
		int count = dtm.getRowCount();
		if (count == 0) {
			return rowElements;
		}
		Vector<?> data = dtm.getDataVector();
		for (int i = 0; i < count; i++) {
			Vector<?> row = (Vector<?>) data.elementAt(i);
			rowElements.add((JSONObject) row.elementAt(0));
		}
		return rowElements;
	}

	public List<JSONObject> getCheckedElements() {
		List<JSONObject> rowElements = new ArrayList<JSONObject>();
		DefaultTableModel dtm = (DefaultTableModel) table.getModel();
		int count = dtm.getRowCount();
		if (count == 0) {
			return rowElements;
		}
		Vector<?> data = dtm.getDataVector();
		for (int i = 0; i < count; i++) {
			Vector<?> row = (Vector<?>) data.elementAt(i);
			Object obj = row.elementAt(1);
			if (obj instanceof Boolean) {
				if (((Boolean) obj).booleanValue()) {
					rowElements.add((JSONObject) row.elementAt(0));
				}
			}
		}
		return rowElements;
	}

	protected Vector<?> getRow(JSONObject object) {
		Vector<Object> v = new Vector<Object>();
		for (String key : columnsToShow) {
			if (key.length() == 0) {
				Panel targetPanel = getPanelContainer().getParentDialog().getParentPanel();
				boolean contain = false;
				if ("members".equals(type)) {
					contain = JSONUtils.contains(((JSONTablePanel) targetPanel).getAllElements(), object, "dn");
				}
				v.add(new Boolean(contain));
			} else {
				Object obj = object.opt(key);
				if (obj != null) {
					v.add(obj);
				} else {
					v.add("");
				}
			}
		}
		v.add(0, object);
		return v;
	}

	public int getObjectRow(Object obj) {
		int row = -1;
		for (int i = 0; i < table.getRowCount(); i++) {
			Object toCompare = table.getValueAt(i, 0);
			if (obj == toCompare) {
				row = i;
				break;
			}
		}
		return row;
	}

	public void addRow(JSONObject object) {
		int pos = getObjectRow(object);
		if (pos == -1) {
			pos = table.getRowCount();
		}
		DefaultTableModel dtm = (DefaultTableModel) table.getModel();
		Vector<?> v = getRow(object);
		if (pos != table.getRowCount()) {
			dtm.removeRow(pos);
		}
		dtm.insertRow(pos, v);
	}

	public void addRow(JSONObject object, int pos) {
		DefaultTableModel dtm = (DefaultTableModel) table.getModel();
		Vector<?> v = getRow(object);
		if (pos != table.getRowCount()) {
			dtm.removeRow(pos);
		}
		dtm.insertRow(pos, v);
	}

	public void removeRow(int row) {
		DefaultTableModel dtm = (DefaultTableModel) table.getModel();
		dtm.removeRow(row);
	}

	public void removeAllRows() {
		DefaultTableModel dtm = (DefaultTableModel) table.getModel();
		while (table.getRowCount() > 0) {
			dtm.removeRow(0);
		}
	}

	protected Vector<String> getColumnNames(List<String> columnsToShow) {
		Vector<String> cnames = new Vector<String>();
		cnames.add("Element");
		for (String column : columnsToShow) {
			cnames.add(column.length() == 0 ? "" : mxResources.get(column));
		}
		return cnames;
	}

	public JButton createToolbarButton(Action a) {
		String actionName = (String) a.getValue(Action.NAME);
		String toolTip = mxResources.get(actionName);
		ImageIcon curIc = null;
		if ("new".equals(actionName)) {
			if ("definitionsRepository".equals(type)) {
				toolTip = mxResources.get("retrieveProcessDefinitionList");
				curIc = new ImageIcon(JSONTablePanel.class.getResource("/org/yaoqiang/bpmn/editor/images/connect.png"));
			} else {
				curIc = new ImageIcon(JSONTablePanel.class.getResource("/org/yaoqiang/bpmn/editor/images/new.png"));
			}
		} else if ("edit".equals(actionName)) {
			curIc = new ImageIcon(JSONTablePanel.class.getResource("/org/yaoqiang/bpmn/editor/images/edit.png"));
			if ("definitionsRepository".equals(type)) {
				toolTip = mxResources.get("downloadProcessDefinition");
			}
		} else if ("delete".equals(actionName)) {
			curIc = new ImageIcon(JSONTablePanel.class.getResource("/org/yaoqiang/bpmn/editor/images/delete.png"));
		} else if ("import".equals(actionName)) {
			toolTip = mxResources.get("ImportLdif");
			curIc = new ImageIcon(JSONTablePanel.class.getResource("/org/yaoqiang/bpmn/editor/images/import.png"));
		}
		JButton b = new JButton(curIc);

		b.setName(actionName);
		b.addActionListener(a);
		b.setToolTipText(toolTip);
		return b;
	}
	
	public JButton createToolbarPopupButton(List<String> choices, Action a) {
		for (String choice : choices) {
			choiceMap.put(mxResources.get(choice), choice);
			JMenuItem mi = popup.add(mxResources.get(choice));
			mi.addActionListener(popupNewElementAction);
		}

		String actionName = (String) a.getValue(Action.NAME);
		JButton b = new JButton(new ImageIcon(XMLTablePanel.class.getResource("/org/yaoqiang/bpmn/editor/images/connect.png")));
		b.setName(actionName);
		b.addActionListener(a);
		b.setToolTipText(mxResources.get(actionName));

		return b;
	}
	
}
