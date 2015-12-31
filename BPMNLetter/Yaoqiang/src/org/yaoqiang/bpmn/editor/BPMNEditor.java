package org.yaoqiang.bpmn.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.SystemTray;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.naming.directory.Attributes;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.yaoqiang.bpmn.editor.addon.Addon;
import org.yaoqiang.bpmn.editor.addon.AddonsManager;
import org.yaoqiang.bpmn.editor.dialog.JSONTablePanel;
import org.yaoqiang.bpmn.editor.dialog.JSONTextPanel;
import org.yaoqiang.bpmn.editor.dialog.WarningDialog;
import org.yaoqiang.bpmn.editor.dialog.jsonpanels.LdapPanel;
import org.yaoqiang.bpmn.editor.dialog.ldaptree.LdapTreeNode;
import org.yaoqiang.bpmn.editor.simulation.MainPanel;
import org.yaoqiang.bpmn.editor.simulation.SimulationAdmin;
import org.yaoqiang.bpmn.editor.swing.AlignToolBar;
import org.yaoqiang.bpmn.editor.swing.BPMNGraphComponent;
import org.yaoqiang.bpmn.editor.swing.BPMNViewComponent;
import org.yaoqiang.bpmn.editor.swing.BaseEditor;
import org.yaoqiang.bpmn.editor.swing.MainToolBar;
import org.yaoqiang.bpmn.editor.swing.MenuBar;
import org.yaoqiang.bpmn.editor.swing.OrganizationPopupMenu;
import org.yaoqiang.bpmn.editor.swing.Palettes;
import org.yaoqiang.bpmn.editor.swing.PopupMenu;
import org.yaoqiang.bpmn.editor.swing.SplashWindow;
import org.yaoqiang.bpmn.editor.swing.TabbedPane;
import org.yaoqiang.bpmn.editor.swing.handler.BPMNKeyboardHandler;
import org.yaoqiang.bpmn.editor.util.BPMNEditorConstants;
import org.yaoqiang.bpmn.editor.util.BPMNEditorUtils;
import org.yaoqiang.bpmn.editor.util.EditorConstants;
import org.yaoqiang.bpmn.editor.util.EditorUtils;
import org.yaoqiang.bpmn.editor.util.Utils;
import org.yaoqiang.bpmn.editor.view.BPMNEditorManager;
import org.yaoqiang.bpmn.editor.view.BPMNGraph;
import org.yaoqiang.bpmn.editor.view.OrganizationManager;
import org.yaoqiang.bpmn.model.BPMNModelParsingErrors.ErrorMessage;
import org.yaoqiang.bpmn.model.elements.activities.CallActivity;
import org.yaoqiang.bpmn.model.elements.core.common.FlowNode;
import org.yaoqiang.bpmn.model.elements.process.BPMNProcess;
import org.yaoqiang.graph.model.GraphModel;
import org.yaoqiang.graph.model.OrganizationGraphModel;
import org.yaoqiang.graph.swing.GraphComponent;
import org.yaoqiang.graph.util.Constants;

import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUndoManager;

/**
 * BPMNEditor
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class BPMNEditor extends BaseEditor {

	private static final long serialVersionUID = -6561623072112577140L;

	static {
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			spellChecker = Class.forName("com.inet.jortho.SpellChecker");
		} catch (ClassNotFoundException e) {
		}
	}

	protected static Class<?> spellChecker;

	private static BPMNEditor editor;

	protected static int revision = -1;

	public static Timer simulation;

	public static int simulationInterval = 1000;

	public static boolean configSimulation = false;

	protected static String availableLocales = "";

	protected JSONObject currentConnection;

	protected JSONObject currentLdapConnection;

	protected JSONObject currentOrganization;

	protected static Map<String, Attributes> ldapEntries = new TreeMap<String, Attributes>();

	protected Map<String, JSONObject> ldapConnections = new HashMap<String, JSONObject>();

	protected Map<String, JSONObject> connections = new HashMap<String, JSONObject>();

	protected List<JSONObject> organizations = new ArrayList<JSONObject>();

	protected Palettes fragmentsPalette;

	protected JPanel definitionsRepository;

	protected LdapPanel ldapPanel;

	protected BPMNViewComponent bpmnView;

	protected JMenu diagramsMenu;

	protected MainToolBar mainToolBar;

	protected AddonsManager addonsManager;

	protected OrganizationManager organizationManager;

	protected mxUndoManager orgUndoManager;

	public static BPMNEditor getInstance() {
		if (editor == null) {
			editor = new BPMNEditor();
		}
		return editor;
	}

	public BPMNEditor() {
		this(true);
	}

	public BPMNEditor(boolean createFrame) {
		initEditor();
		this.appTitle = mxResources.get("title") + " " + VERSION;

		BPMNEditorManager bpmnEditorManager = new BPMNEditorManager(this);
		undoManager.addListener(mxEvent.UNDO, bpmnEditorManager.getHandler());
		undoManager.addListener(mxEvent.REDO, bpmnEditorManager.getHandler());

		initOrganizationComponent();

		addonsManager = new AddonsManager(this);
		addonsManager.initAddons();
		activateAddons();

		bpmnView = new BPMNViewComponent((BPMNGraph) graph);

		installPanel();
		initFragmentsPalette();
		initDefinitionsRepositoryPanel();
		initLdapPanel();

		if (createFrame) {
			frame = createFrame(new MenuBar(this));
		}
	}

	protected void initOrganizationComponent() {
		OrganizationGraphModel orgModel = new OrganizationGraphModel();
		orgModel.addListener(mxEvent.CHANGE, new mxIEventListener() {
			public void invoke(Object source, mxEventObject evt) {
				setOrgModified(true);
			}
		});
		orgGraphComponent = new BPMNGraphComponent(new BPMNGraph(orgModel), false);
		orgGraphComponent.setName("org");
		// orgGraphComponent.setHorizontalPageCount(2);
		// orgModel.setHorizontalPageCount(2);
		orgGraphComponent.getGraph().refresh();
		installGraphComponentHandlers(orgGraphComponent);
		installGraphComponentListeners(orgGraphComponent);

		orgUndoManager = new mxUndoManager(orgGraphComponent.getGraph());
		organizationManager = new OrganizationManager(this);
		orgUndoManager.addListener(mxEvent.UNDO, organizationManager.getHandler());
		orgUndoManager.addListener(mxEvent.REDO, organizationManager.getHandler());

		loadOrganizations();
		JSONObject org = getCurrentOrganization();
		if (org == null && getOrganizations().size() == 0) {
			String orgId = "dc=example,dc=com";
			org = new JSONObject();
			try {
				org.putOpt("id", orgId);
				org.putOpt("name", "Example");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			orgModel.addSampleCells("Example");
			BPMNEditorUtils.saveOrganization(orgId, orgModel);
			setCurrentOrganization(org);
			getOrganizations().add(org);
			Constants.SETTINGS.put("organization", orgId);
			Utils.saveConfigureFile();
		} else {
			try {
				if (org == null) {
					org = getOrganizations().get(0);
					setCurrentOrganization(org);
				}
				String filepath = Constants.YAOQIANG_USER_HOME + File.separator + BPMNEditorConstants.YAOQIANG_ORGS_DIR + File.separator + org.optString("id")
						+ ".org";
				Document document = EditorUtils.parseXml(new FileInputStream(filepath));
				mxCodec codec = new mxCodec(document);
				codec.decode(document.getDocumentElement(), getOrgGraphComponent().getGraph().getModel());
				getOrgGraphComponent().setVerticalPageCount(getOrgGraphComponent().getGraph().getModel().getPageCount());
				getOrgGraphComponent().setHorizontalPageCount(getOrgGraphComponent().getGraph().getModel().getHorizontalPageCount());
				getOrgGraphComponent().getViewport().setOpaque(false);
				getOrgGraphComponent().setBackground(getOrgGraphComponent().getGraph().getModel().getBackgroundColor());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			setOrgModified(false);
			getOrgGraphComponent().zoomAndCenter();
		}
	}

	public List<JSONObject> getOrganizations() {
		return organizations;
	}

	public Map<String, JSONObject> getConnections() {
		return connections;
	}

	public Map<String, JSONObject> getLdapConnections() {
		return ldapConnections;
	}

	protected GraphComponent createGraphComponent(String name) {
		GraphComponent graphComponent = null;
		if (graph != null) {
			graphComponent = new BPMNGraphComponent((BPMNGraph) graph);
		} else {
			graphComponent = new BPMNGraphComponent(new BPMNGraph(new GraphModel(VERSION)));
		}
		if (name != null) {
			graphComponent.setName(name);
		}
		return graphComponent;
	}

	protected void installGraphComponentHandlers(GraphComponent graphComponent) {
		super.installGraphComponentHandlers(graphComponent);
		new BPMNKeyboardHandler(graphComponent);
	}

	protected void initFragmentsPalette() {
		fragmentsPalette = insertPalette(mxResources.get("fragments"));
		fragmentsPalette.addListener(mxEvent.SELECT, eventListener);
		if (Constants.SETTINGS.getProperty("showPatterns", "1").equals("1")) {
			int[] nums = { 1, 2, 3, 4, 5, 6, 7 };
			for (int n : nums) {
				fragmentsPalette.addPatternTemplate(n);
			}
		}

		File fragmentsDir = new File(Constants.YAOQIANG_USER_HOME + File.separator + BPMNEditorConstants.YAOQIANG_FRAGMENTS_DIR);

		for (File imgFile : fragmentsDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".png");
			}
		})) {
			fragmentsPalette.addFragmentTemplate(imgFile);
		}
	}

	protected void initDefinitionsRepositoryPanel() {
		definitionsRepository = new JPanel();
		definitionsRepository.setLayout(new BorderLayout());

		JSONTextPanel connectionPanel = new JSONTextPanel(null, null, "connection", false, 100, 26, false);
		definitionsRepository.add(connectionPanel, BorderLayout.NORTH);

		List<String> columnsToShow = new ArrayList<String>();
		columnsToShow.add("name");
		columnsToShow.add("version");
		definitionsRepository.add(new JSONTablePanel(null, this, "definitionsRepository", null, columnsToShow, null, 200, 150, true, false));

		final JScrollPane scrollPane = new JScrollPane(definitionsRepository);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		palettesPane.add(mxResources.get("definitionsRepository"), scrollPane);
	}

	public void refreshDefinitionsRepositoryPanel(String type) {
		List<JSONObject> definitions = null;
		if ("engine".equals(type)) {
			definitions = BPMNEditorUtils.retrieveDefinitionsFromEngine(getCurrentConnection());
		} else {
			definitions = BPMNEditorUtils.retrieveDefinitionsFromRepository();
		}
		JSONTablePanel table = getDefinitionsRepositoryTable();
		table.removeAllRows();
		table.fillTableContent(definitions);
	}

	protected void initLdapPanel() {
		ldapPanel = (LdapPanel) createDialog().getPanelContainer().getPanelFactory().getPanel(this, "ldap");
		palettesPane.add(mxResources.get("ldap"), ldapPanel);
	}

	protected void installPanel() {
		centerPane = new TabbedPane();

		centerPane.add(mxResources.get("newDiagram"), graphComponent);

		centerPane.add(mxResources.get("bpmn"), bpmnView);

		centerPane.add(mxResources.get("organization"), orgGraphComponent);

		JPanel rightPane = new JPanel(new BorderLayout());
		rightPane.add(new AlignToolBar(this, JToolBar.VERTICAL), BorderLayout.WEST);
		rightPane.add(centerPane, BorderLayout.CENTER);
		simulationPane = new MainPanel(this);

		JComponent leftPane = palettesPane;
		if (Constants.SETTINGS.getProperty("showFloatingOutline", Constants.OS.startsWith("Windows") ? "1" : "0").equals("0")) {
			leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, graphOverview, palettesPane);
			if (Constants.SETTINGS.getProperty("showOutline", "1").equals("1")) {
				((JSplitPane) leftPane).setDividerLocation(180);
			} else {
				((JSplitPane) leftPane).setDividerLocation(0);
			}
			((JSplitPane) leftPane).setContinuousLayout(true);
			((JSplitPane) leftPane).setDividerSize(6);
			((JSplitPane) leftPane).setBorder(null);
		}

		mainToolBar = new MainToolBar(this, JToolBar.HORIZONTAL);
		createMainPane(mainToolBar, leftPane, rightPane);
	}

	public BPMNGraphComponent getGraphComponent() {
		return (BPMNGraphComponent) graphComponent;
	}

	public static BPMNGraph getGraph() {
		return (BPMNGraph) graph;
	}

	public BPMNViewComponent getBpmnView() {
		return bpmnView;
	}

	public Palettes getFragmentsPalette() {
		return fragmentsPalette;
	}

	public mxUndoManager getUndoManager() {
		if ("org".equals(getCurrentGraphComponent().getName())) {
			return orgUndoManager;
		} else {
			return undoManager;
		}
	}

	public void resetOrgUndoManager() {
		orgUndoManager = new mxUndoManager(orgGraphComponent.getGraph());
		orgUndoManager.addListener(mxEvent.UNDO, organizationManager.getHandler());
		orgUndoManager.addListener(mxEvent.REDO, organizationManager.getHandler());
	}

	public AddonsManager getAddonsManager() {
		return addonsManager;
	}

	public JMenu getDiagramsMenu() {
		return diagramsMenu;
	}

	public void setDiagramsMenu(JMenu diagramsMenu) {
		this.diagramsMenu = diagramsMenu;
	}

	public MainToolBar getMainToolBar() {
		return mainToolBar;
	}

	public JSONTablePanel getDefinitionsRepositoryTable() {
		return (JSONTablePanel) definitionsRepository.getComponent(1);
	}

	public JSONTextPanel getConnectionTextPanel() {
		return (JSONTextPanel) definitionsRepository.getComponent(0);
	}

	public JSONObject getCurrentConnection() {
		return currentConnection;
	}

	public void setCurrentConnection(JSONObject connection) {
		currentConnection = connection;
	}

	public LdapPanel getLdapPanel() {
		return ldapPanel;
	}

	public JSONTextPanel getLdapConnectionTextPanel() {
		return ldapPanel.getConnectionPanel();
	}

	public JSONObject getCurrentLdapConnection() {
		return currentLdapConnection;
	}

	public void setCurrentLdapConnection(JSONObject connection) {
		currentLdapConnection = connection;
	}

	public static Map<String, Attributes> getLdapEntries() {
		return ldapEntries;
	}

	public static void setLdapEntries(Map<String, Attributes> entries) {
		ldapEntries = entries;
	}

	public String getLdapSelectedEntry() {
		return ldapPanel.getNodeDN(null);
	}

	public void resetLdapTree(LdapTreeNode[] nodes) {
		ldapPanel.resetTreeModel(nodes);
	}

	public JSONObject getCurrentOrganization() {
		return currentOrganization;
	}

	public void setCurrentOrganization(JSONObject currentOrganization) {
		this.currentOrganization = currentOrganization;
	}

	public void startSimulation() {
		getGraphComponent().getGraph().setSelectionCell(null);
		final SimulationAdmin simAdmin = new SimulationAdmin(getGraphComponent());
		simAdmin.start();
		statusBar.setText("Simulation is running!");
		simulation = new Timer(simulationInterval, new ActionListener() {
			private Set<FlowNode> nodes = new HashSet<FlowNode>();

			public void actionPerformed(ActionEvent e) {
				if (simAdmin.getNodeList().isEmpty()) {
					simulation.stop();
					simulation = null;
					simAdmin.unHighLightNodes(nodes);
					graph.refresh();
					statusBar.setText("Simulation is stopped!");
				} else {
					simAdmin.unHighLightNodes(nodes);
					nodes = simAdmin.getNodeList().remove(0);
					simAdmin.highLightNodes(nodes);
					graph.refresh();
				}
			}

		});
		simulation.start();
	}

	public void stopSimulation() {
		if (simulation != null) {
			simulation.stop();
			simulation = null;
			SimulationAdmin.reset();
			statusBar.setText("Simulation is stopped!");
		}
	}

	public void pauseSimulation() {
		if (simulation != null) {
			getStatusBar().setText("Simulation is pausing!");
			simulation.stop();
		}
	}

	public void restartSimulation() {
		getStatusBar().setText("Simulation is running!");
		simulation.restart();
	}

	public static boolean isSimulationRunning() {
		return simulation != null && simulation.isRunning();
	}

	public static boolean isSimulationPausing() {
		return simulation != null && !simulation.isRunning();
	}

	public static boolean isSimulationStopped() {
		return simulation == null && !configSimulation;
	}

	public void status(String msg) {
		if (isSimulationStopped()) {
			statusBar.setText(msg);
		}
	}

	public void insertGraphComponent(mxCell cell) {
		String calledElement = ((CallActivity) cell.getValue()).getCalledElement();
		if (calledElement.equals("callProcess")) {
			BPMNProcess process = graph.getModel().getBpmnModel().createProcess(false);
			calledElement = process.getId();
			((CallActivity) cell.getValue()).setCalledElement(calledElement);
		}
		insertGraphComponent(calledElement, ((CallActivity) cell.getValue()).getName());
		BPMNEditorUtils.refreshDiagramList(this, graph.getModel().getCell(calledElement));
	}

	public void insertGraphComponent(String id, String value) {
		mxCell diagramCell = (mxCell) graph.getModel().getCell(id);
		GraphComponent com = graphComponents.get(id);
		if (com == null) {
			if (diagramCell == graph.getModel().getChildAt(graph.getModel().getRoot(), 0)) {
				com = graphComponent;
			} else {
				com = createGraphComponent(id);
				graphComponents.put(id, com);
				if (diagramCell == null) {
					diagramCell = new mxCell();
					diagramCell.setId(id);
					mxCell root = (mxCell) graph.getModel().getRoot();
					root.insert(diagramCell, root.getChildCount());
					graph.getModel().getCells().put(id, diagramCell);
				}
				com.setLastViewRoot(diagramCell);
			}
		}
		diagramCell.setValue(value);
		currentGraphComponent = com;
		graph.getView().setCurrentRoot(com.getLastViewRoot());
		graph.clearSelection();

		if (com != graphComponent) {
			centerPane.add(value, com);
			installGraphComponentHandlers(currentGraphComponent);
			installGraphComponentListeners(currentGraphComponent);
		}

		centerPane.setSelectedComponent(com);
	}

	public void removeGraphComponent(String name) {
		super.removeGraphComponent(name);
		((JRadioButtonMenuItem) this.getDiagramsMenu().getMenuComponent(0)).setSelected(true);
	}

	public void resetDiagramName() {
		String name = null;
		if ("BPMNEditor".equals(currentGraphComponent.getName())) {
			if (((BPMNGraph) graph).getBpmnModel().getFirstBPMNDiagram() != null) {
				name = ((BPMNGraph) graph).getBpmnModel().getFirstBPMNDiagram().getName();
			}
			if (name == null || name.length() == 0) {
				name = mxResources.get("newDiagram");
			}
			centerPane.setTitleAt(0, name);
		} else {
			name = graph.getCellValue(currentGraphComponent.getName());
			centerPane.setTabTitle(currentGraphComponent.getName(), name);
		}
	}

	public void activateAddons() {
		for (Addon addon : addonsManager.getAddons()) {
			try {
				addon.activate();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "Please use the latest editor and add-ons.\n Addon: " + addon.getName() + " [" + addon.getVersion()
						+ "] may not work correctly.");
			}
		}
	}

	protected JPopupMenu createGraphPopupMenu(GraphComponent graphComponent, MouseEvent e) {
		JPopupMenu menu = null;
		if ("org".equals(graphComponent.getName())) {
			menu = new OrganizationPopupMenu(this, graphComponent);
		} else {
			menu = new PopupMenu(this, graphComponent, e);
		}
		return menu;
	}

	public void error(List<ErrorMessage> errorMessages) {
		Window window = SwingUtilities.windowForComponent(this);
		if (window != null && window instanceof Frame) {
			Object[][] data = new Object[errorMessages.size()][3];
			for (int i = 0; i < errorMessages.size(); i++) {
				ErrorMessage err = errorMessages.get(i);
				data[i][0] = err.getType();
				data[i][1] = err.getLineNumber();
				data[i][2] = err.getMessage();
			}
			String[] columnNames = { mxResources.get("type"), mxResources.get("lineNumber"), mxResources.get("description") };

			JTable table = new JTable(data, columnNames);
			JScrollPane scrollPane = new JScrollPane(table);
			table.setPreferredScrollableViewportSize(new Dimension(300, 200));
			table.getColumnModel().getColumn(0).setMinWidth(60);
			table.getColumnModel().getColumn(0).setPreferredWidth(60);
			table.getColumnModel().getColumn(0).setMaxWidth(60);
			table.getColumnModel().getColumn(1).setMinWidth(60);
			table.getColumnModel().getColumn(1).setPreferredWidth(60);
			table.getColumnModel().getColumn(1).setMaxWidth(0);
			table.getColumnModel().getColumn(2).setPreferredWidth(200);

			JDialog dialog = new JDialog((Frame) window);
			dialog.setPreferredSize(new Dimension(500, 150));
			dialog.setTitle(mxResources.get("schemaError"));
			dialog.add(scrollPane);
			dialog.pack();
			dialog.setModal(false);
			dialog.setLocationRelativeTo(window);
			dialog.setVisible(true);
		}
	}

	public void configure() {
		super.configure();
		File fdir = new File(Constants.YAOQIANG_USER_HOME + File.separator + BPMNEditorConstants.YAOQIANG_FRAGMENTS_DIR);
		if (!fdir.exists()) {
			try {
				fdir.mkdir();
			} catch (Exception exc) {
			}
		}
		File aodir = new File(Constants.YAOQIANG_USER_HOME + File.separator + BPMNEditorConstants.YAOQIANG_ADDONS_DIR);
		if (!aodir.exists()) {
			try {
				aodir.mkdir();
			} catch (Exception exc) {
			}
		}
		File orgdir = new File(Constants.YAOQIANG_USER_HOME + File.separator + BPMNEditorConstants.YAOQIANG_ORGS_DIR);
		if (!orgdir.exists()) {
			try {
				orgdir.mkdir();
			} catch (Exception exc) {
			}
		}
		File ddir = new File(Constants.YAOQIANG_USER_HOME + File.separator + BPMNEditorConstants.YAOQIANG_DICTIONARY_DIR);
		if (ddir.exists()) {
			for (File dictFile : ddir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".dict");
				}
			})) {
				availableLocales += dictFile.getName().substring(11, 13) + ",";
			}
			if (availableLocales.endsWith(",")) {
				availableLocales = availableLocales.substring(0, availableLocales.length() - 1);
			}
		} else {
			try {
				ddir.mkdir();
			} catch (Exception exc) {
			}
		}

		if (spellChecker != null) {
			try {
				Class<?> fudClass = Class.forName("com.inet.jortho.FileUserDictionary");
				Constructor<?> c = fudClass.getConstructor(String.class);
				Object fudObject = c.newInstance(ddir.getPath());
				spellChecker.getMethod("setUserDictionaryProvider", fudClass.getInterfaces()[0]).invoke(null, fudObject);
				spellChecker.getMethod("registerDictionaries", URL.class, String.class, String.class, String.class).invoke(null, ddir.toURI().toURL(),
						availableLocales, locale.getLanguage(), ".dict");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		initDatabase();

		loadConnections();
		loadLdapConnections();

		String intervalString = Constants.SETTINGS.getProperty("interval", "1000");
		simulationInterval = Integer.parseInt(intervalString);

		mxResources.add(EditorConstants.RESOURCES_URI + "language/editor", locale);
		mxResources.add(EditorConstants.RESOURCES_URI + "language/element", locale);
		mxResources.add(EditorConstants.RESOURCES_URI + "language/warning", locale);

		if (Constants.JAVA_VERSION >= 1.6 && SystemTray.isSupported() && trayIcon == null) {
			initSystemTray();
		}

	}

	public void loadOrganizations() {
		organizations.clear();
		BPMNEditorUtils.loadOrganizations(this, organizations);

		String orgId = Constants.SETTINGS.getProperty("organization", "");
		for (JSONObject org : organizations) {
			if (org.optString("id").equals(orgId)) {
				setCurrentOrganization(org);
				return;
			}
		}
	}

	public void loadConnections() {
		connections.clear();
		BPMNEditorUtils.loadConnections(connections);
	}

	public void loadLdapConnections() {
		ldapConnections.clear();
		BPMNEditorUtils.loadLdapConnections(ldapConnections);
	}

	public static int getRevision() {
		return revision;
	}

	public void setCurrentFile(File file) {
		currentFile = file;
		setRevision(-1);
	}

	public void setRevision(int revision) {
		BPMNEditor.revision = revision;
		updateTitle();
	}

	public void updateTitle() {
		Window window = SwingUtilities.windowForComponent(this);
		if (window != null && window instanceof JFrame) {
			String title = mxResources.get("newDiagram");
			if (revision > 0) {
				title = getDiagramName();
			} else if (currentFile != null) {
				title = currentFile.getAbsolutePath();
			}
			if (modified) {
				title += "*";
			}
			if (revision > 0) {
				title += " (Rev. " + revision + ")";
			}
			((JFrame) window).setTitle(title + " - " + appTitle);
		}
	}

	public void setLookAndFeel(String clazz) {
		super.setLookAndFeel(clazz);
		new BPMNKeyboardHandler(orgGraphComponent);
	}

	public void restart() {
		super.restart();
		Component left = getMainPane().getLeftComponent();
		if (left instanceof JSplitPane && Constants.SETTINGS.getProperty("showOutline", "1").equals("1")) {
			((JSplitPane) left).setDividerLocation(180);
		}
		createFrame(new MenuBar(this)).setVisible(true);
	}

	public static void main(final String[] args) {
		if (invalidJRE()) {
			System.exit(1);
			return;
		}

		if (args.length > 1) {
			List<String> argList = Arrays.asList(args);
			if (argList.contains("--export")) {
				String file = "";
				if (args.length == 3) {
					file = args[2];
				}
				BPMNEditorUtils.export(args[0], file);
				System.exit(1);
				return;
			}
		}

		splash = new SplashWindow();
		editor = new BPMNEditor();
		editor.showFrame(true);

		if (args.length == 1) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					editor.openFile(args[0]);
				}
			});
		}

		if (splash != null) {
			splash.dispose();
			splash = null;
		}

		if (spellChecker != null) {
			if (availableLocales.length() == 0 && Constants.SETTINGS.getProperty("warningNoDictionary", "1").equals("1")) {
				new WarningDialog("warningNoDictionary").setVisible(true);
			}
		}
		
		
		//-------------------
//		SimpleDateFormat dateFormatter = new SimpleDateFormat("y-M-d-h-m-s");
//		String date = dateFormatter.format(Calendar.getInstance().getTime());
//		File file =  new File("BPMNLetter_"+date+".log");
//		System.out.println("Log file path: "+file.getAbsolutePath());
//		File file1 =  new File("BPMNLetter_erro"+date+".log");
//		System.out.println("Log error file path: "+file1.getAbsolutePath());
//		try {
//			System.setOut(new PrintStream(new FileOutputStream(file.getAbsolutePath())));
//			System.setErr(new PrintStream(new FileOutputStream(file1.getAbsolutePath())));
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		//--------------

	}
}
