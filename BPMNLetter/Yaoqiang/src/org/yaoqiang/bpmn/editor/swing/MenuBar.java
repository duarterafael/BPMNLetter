package org.yaoqiang.bpmn.editor.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.TransferHandler;
import javax.swing.UIManager;

import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.action.CheckBoxMenuItem;
import org.yaoqiang.bpmn.editor.action.EditorActions;
import org.yaoqiang.bpmn.editor.action.ModelActions;
import org.yaoqiang.bpmn.editor.util.BPMNEditorUtils;
import org.yaoqiang.graph.action.GraphActions;
import org.yaoqiang.graph.util.Constants;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxResources;

/**
 * BaseMenuBar
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class MenuBar extends JMenuBar {

	private static final long serialVersionUID = 4060203894740766714L;

	private static CheckBoxMenuItem warningMenuItem;
	private static CheckBoxMenuItem outlineMenuItem;
	protected static JCheckBoxMenuItem rulersMenuItem;
	protected static JCheckBoxMenuItem gridMenuItem;
	private static CheckBoxMenuItem auxiliaryMenuItem;

	public MenuBar(BPMNEditor editor) {

		populateFileMenu(editor);

		populateEditMenu(editor);

		populateViewMenu(editor);

		populateModelMenu(editor);

		populateRepositoryMenu(editor);

		populateDeploymentMenu(editor);

		populateSimulationMenu(editor);

		populateOrganizationsMenu(editor);

		populateSettingsMenu(editor);

		populateDiagramMenu(editor);

		populateHelpMenu(editor);

	}

	protected void populateFileMenu(BaseEditor editor) {
		JMenu menu = add(new JMenu(mxResources.get("file")));

		menu.add(editor.bind(mxResources.get("new"), ModelActions.getAction(ModelActions.NEW), "/org/yaoqiang/bpmn/editor/images/new.png"));
		menu.add(editor.bind(mxResources.get("OpenFile"), ModelActions.getAction(ModelActions.OPEN), "/org/yaoqiang/bpmn/editor/images/open.png"));
		menu.add(editor.bind(mxResources.get("OpenURL"), ModelActions.getAction(ModelActions.OPEN_URL), "/org/yaoqiang/bpmn/editor/images/openurl.png"));
		menu.add(editor.bind(mxResources.get("reload"), ModelActions.getAction(ModelActions.RELOAD), "/org/yaoqiang/bpmn/editor/images/reload.png"));

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("save"), ModelActions.getSaveAction(), "/org/yaoqiang/bpmn/editor/images/save.png"));
		menu.add(editor.bind(mxResources.get("saveAs"), ModelActions.getSaveAsAction(), "/org/yaoqiang/bpmn/editor/images/save_as.png"));
		menu.add(editor.bind(mxResources.get("saveAsPNG"), ModelActions.getSaveAsPNG(), "/org/yaoqiang/bpmn/editor/images/png.png"));
		menu.add(editor.bind(mxResources.get("saveAsODT"), ModelActions.getSaveAsODT(), "/org/yaoqiang/bpmn/editor/images/odt.png"));
		menu.add(editor.bind(mxResources.get("saveAsFragment"), ModelActions.getSaveAsFragment(), "/org/yaoqiang/bpmn/editor/images/save_as_fragment.png"));

		menu.addSeparator();

		populateFile2Menu(editor, menu);
	}

	protected void populateFile2Menu(BaseEditor editor, JMenu menu) {
		menu.add(editor.bind(mxResources.get("pageSetup"), GraphActions.getAction(GraphActions.PAGE_SETUP), "/org/yaoqiang/bpmn/editor/images/page_setup.png"));
		menu.add(editor.bind(mxResources.get("print"), GraphActions.getAction(GraphActions.PRINT), "/org/yaoqiang/bpmn/editor/images/printer.png"));

		menu.addSeparator();

		JMenu recentFilesmenu = (JMenu) menu.add(new JMenu(mxResources.get("recentFiles")));
		recentFilesmenu.setIcon(new ImageIcon(MenuBar.class.getResource("/org/yaoqiang/bpmn/editor/images/recent.png")));
		editor.setRecentFilesmenu(recentFilesmenu);
		populateRecentFileMenu(editor);

		menu.addSeparator();
		menu.add(editor.bind(mxResources.get("exit"), ModelActions.getAction(ModelActions.EXIT), "/org/yaoqiang/bpmn/editor/images/exit.png"));

	}

	protected void populateRecentFileMenu(BaseEditor editor) {
		BPMNEditorUtils.initRecentFileList(editor);
	}

	protected void populateEditMenu(BaseEditor editor) {
		JMenu menu = add(new JMenu(mxResources.get("edit")));

		menu.add(editor.bind(mxResources.get("undo"), EditorActions.getAction(EditorActions.UNDO), "/org/yaoqiang/bpmn/editor/images/undo.png"));
		menu.add(editor.bind(mxResources.get("redo"), EditorActions.getAction(EditorActions.REDO), "/org/yaoqiang/bpmn/editor/images/redo.png"));

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("cut"), TransferHandler.getCutAction(), "/org/yaoqiang/bpmn/editor/images/cut.png"));
		menu.add(editor.bind(mxResources.get("copy"), TransferHandler.getCopyAction(), "/org/yaoqiang/bpmn/editor/images/copy.png"));
		menu.add(editor.bind(mxResources.get("paste"), TransferHandler.getPasteAction(), "/org/yaoqiang/bpmn/editor/images/paste.png"));

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("delete"), GraphActions.getAction(GraphActions.DELETE), "/org/yaoqiang/bpmn/editor/images/delete.png"));

		menu.addSeparator();

		JMenu addmenu = (JMenu) menu.add(new JMenu(mxResources.get("addPage")));
		addmenu.add(editor.bind(mxResources.get("dirHorizontal"), GraphActions.getAddPageAction(true)));
		addmenu.add(editor.bind(mxResources.get("dirVertical"), GraphActions.getAddPageAction(false)));
		JMenu removemenu = (JMenu) menu.add(new JMenu(mxResources.get("removePage")));
		removemenu.add(editor.bind(mxResources.get("dirHorizontal"), GraphActions.getRemovePageAction(true)));
		removemenu.add(editor.bind(mxResources.get("dirVertical"), GraphActions.getRemovePageAction(false)));

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("selectAll"), GraphActions.getAction(GraphActions.SELECT_ALL), "/org/yaoqiang/bpmn/editor/images/select_all.png"));
		menu.add(editor.bind(mxResources.get("selectNone"), GraphActions.getAction(GraphActions.SELECT_NONE), "/org/yaoqiang/bpmn/editor/images/select_none.png"));
	}

	protected void populateViewMenu(BPMNEditor editor) {
		JMenu menu = add(new JMenu(mxResources.get("view")));
		warningMenuItem = new CheckBoxMenuItem(editor, mxResources.get("warning"), "showWarning");
		menu.add(warningMenuItem);
		outlineMenuItem = new CheckBoxMenuItem(editor, mxResources.get("outline"), "showOutline");
		menu.add(outlineMenuItem);
		setRulerMenuItem(editor);
		menu.add(rulersMenuItem);
		setGridMenuItem(editor);
		menu.add(gridMenuItem);

		auxiliaryMenuItem = new CheckBoxMenuItem(editor, mxResources.get("auxiliary"), "showAuxiliary");
		menu.add(auxiliaryMenuItem);

		menu.addSeparator();

		populateGridMenu(editor, menu);

		menu.add(editor.bind(mxResources.get("backgroundColor"), GraphActions.getAction(GraphActions.BACKGROUND),
				"/org/yaoqiang/bpmn/editor/images/fillcolor.gif"));

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("autolayout"), GraphActions.getAutoLayoutAction(), "/org/yaoqiang/bpmn/editor/images/auto_layout.png"));
		menu.add(editor.bind(mxResources.get("rotateSwimlane"), GraphActions.getAction(GraphActions.ROTATE_SWIMLANE),
				"/org/yaoqiang/bpmn/editor/images/rotate_swimlane.png"));

		menu.addSeparator();

		JMenu submenu = (JMenu) menu.add(new JMenu(mxResources.get("zoom")));
		submenu.setIcon(new ImageIcon(MenuBar.class.getResource("/org/yaoqiang/bpmn/editor/images/zoom.png")));

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

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("zoomIn"), GraphActions.getAction(GraphActions.ZOOM_IN), "/org/yaoqiang/bpmn/editor/images/zoom_in.png"));
		menu.add(editor.bind(mxResources.get("actualSize"), GraphActions.getAction(GraphActions.ZOOM_ACTUAL), "/org/yaoqiang/bpmn/editor/images/zoomactual.png"));
		menu.add(editor.bind(mxResources.get("zoomOut"), GraphActions.getAction(GraphActions.ZOOM_OUT), "/org/yaoqiang/bpmn/editor/images/zoom_out.png"));
	}

	protected void populateGridMenu(BaseEditor editor, JMenu menu) {
		JMenu submenu = (JMenu) menu.add(new JMenu(mxResources.get("gridstyle")));
		submenu.setIcon(new ImageIcon(MenuBar.class.getResource("/org/yaoqiang/bpmn/editor/images/grid.png")));

		submenu.add(editor.bind(mxResources.get("gridSize"), EditorActions.getAction(EditorActions.GRID_SIZE)));
		submenu.add(editor.bind(mxResources.get("gridColor"), EditorActions.getAction(EditorActions.GRID_COLOR)));

		submenu.addSeparator();

		submenu.add(editor.bind(mxResources.get("dot"), EditorActions.getGridStyleAction(mxGraphComponent.GRID_STYLE_DOT)));
		submenu.add(editor.bind(mxResources.get("cross"), EditorActions.getGridStyleAction(mxGraphComponent.GRID_STYLE_CROSS)));
		submenu.add(editor.bind(mxResources.get("line"), EditorActions.getGridStyleAction(mxGraphComponent.GRID_STYLE_LINE)));
		submenu.add(editor.bind(mxResources.get("dashed"), EditorActions.getGridStyleAction(mxGraphComponent.GRID_STYLE_DASHED)));

		menu.addSeparator();
	}

	protected void populateModelMenu(BaseEditor editor) {
		JMenu menu = add(new JMenu(mxResources.get("model")));
		menu.add(editor.bind(mxResources.get("definitions"), ModelActions.getAction(ModelActions.DEFINITIONS)));
		menu.add(editor.bind(mxResources.get("namespaces"), ModelActions.getAction(ModelActions.NAMESPACES)));
		menu.add(editor.bind(mxResources.get("imports"), ModelActions.getAction(ModelActions.IMPORTS)));
		menu.add(editor.bind(mxResources.get("itemDefinitions"), ModelActions.getAction(ModelActions.ITEM_DEFINITIONS)));
		menu.add(editor.bind(mxResources.get("resources"), ModelActions.getAction(ModelActions.RESOURCES)));
		menu.addSeparator();
		menu.add(editor.bind(mxResources.get("globalTasks"), ModelActions.getAction(ModelActions.GLOBAL_TASKS)));
		menu.addSeparator();
		menu.add(editor.bind(mxResources.get("messages"), ModelActions.getAction(ModelActions.MESSAGES)));
		menu.add(editor.bind(mxResources.get("errors"), ModelActions.getAction(ModelActions.ERRORS)));
		menu.add(editor.bind(mxResources.get("signals"), ModelActions.getAction(ModelActions.SIGNALS)));
		menu.add(editor.bind(mxResources.get("escalations"), ModelActions.getAction(ModelActions.ESCALATIONS)));
		menu.add(editor.bind(mxResources.get("interfaces"), ModelActions.getAction(ModelActions.INTERFACES)));
		menu.add(editor.bind(mxResources.get("eventDefinitions"), ModelActions.getAction(ModelActions.DEF_EVENT_DEFINITIONS)));
		menu.add(editor.bind(mxResources.get("partners"), ModelActions.getAction(ModelActions.PARTNERS)));
		menu.addSeparator();
		menu.add(editor.bind(mxResources.get("dataStores"), ModelActions.getAction(ModelActions.DATASTORES)));
		menu.add(editor.bind(mxResources.get("categories"), ModelActions.getAction(ModelActions.CATEGORIES)));

	}

	protected void populateRepositoryMenu(BaseEditor editor) {
		JMenu menu = add(new JMenu(mxResources.get("repository")));
		menu.add(editor.bind(mxResources.get("SaveToNativeRepo"), ModelActions.getAction(ModelActions.SAVE_TO_REPO),
				"/org/yaoqiang/bpmn/editor/images/save_to_repo.png"));
		menu.add(editor.bind(mxResources.get("versionHistory"), ModelActions.getAction(ModelActions.VERSION_HISTORY),
				"/org/yaoqiang/bpmn/editor/images/version_history.png"));

	}

	protected void populateDeploymentMenu(BaseEditor editor) {
		JMenu menu = add(new JMenu(mxResources.get("deployment")));
		menu.add(editor.bind(mxResources.get("DeployProcessDefinition"), ModelActions.getAction(ModelActions.DEPLOY),
				"/org/yaoqiang/bpmn/editor/images/deploy.png"));
		menu.add(editor.bind(mxResources.get("engineConnections"), ModelActions.getAction(ModelActions.ENGINE_CONNECTIONS),
				"/org/yaoqiang/bpmn/editor/images/engine_connections.png"));
	}

	protected void populateSimulationMenu(BaseEditor editor) {
		JMenu menu = add(new JMenu(mxResources.get("simulation")));
		menu.add(editor.bind(mxResources.get("run"), ModelActions.getAction(ModelActions.RUN_SIMULATION), "/org/yaoqiang/bpmn/editor/images/run.png"));
		menu.add(editor.bind(mxResources.get("pause"), ModelActions.getAction(ModelActions.PAUSE_SIMULATION), "/org/yaoqiang/bpmn/editor/images/pause.png"));
		menu.add(editor.bind(mxResources.get("stop"), ModelActions.getAction(ModelActions.STOP_SIMULATION), "/org/yaoqiang/bpmn/editor/images/stop.png"));
		menu.add(editor.bind(mxResources.get("Speed"), ModelActions.getAction(ModelActions.SIMULATION_SPEED), "/org/yaoqiang/bpmn/editor/images/timer.png"));
		menu.addSeparator();
		menu.add(editor.bind(mxResources.get("Config"), ModelActions.getAction(ModelActions.CONFIG_SIMULATION), "/org/yaoqiang/bpmn/editor/images/sim_config.png"));
	}

	protected void populateOrganizationsMenu(BaseEditor editor) {
		JMenu menu = add(new JMenu(mxResources.get("organization")));
		menu.add(editor.bind(mxResources.get("organizations"), ModelActions.getAction(ModelActions.ORGANIZATIONS),
				"/org/yaoqiang/bpmn/editor/images/organization.png"));
		menu.add(editor.bind(mxResources.get("ldapConnections"), ModelActions.getAction(ModelActions.LDAP_CONNECTIONS),
				"/org/yaoqiang/bpmn/editor/images/ldap_connections.png"));
	}

	protected void populateAddonsMenu(BaseEditor editor, JMenu menu) {
		List<String> names = ((BPMNEditor) editor).getAddonsManager().getAddonInfos();
		if (names.isEmpty()) {
			menu.add(new JMenuItem(mxResources.get("none")));
		} else {
			for (String addon : names) {
				menu.add(new JMenuItem(addon));
			}
		}
	}

	protected void populateDiagramMenu(BaseEditor editor) {
		JMenu menu = add(new JMenu(mxResources.get("diagram")));
		((BPMNEditor) editor).setDiagramsMenu(menu);
		BPMNEditorUtils.refreshDiagramList((BPMNEditor) editor, null);
	}

	protected void populateSettingsMenu(BaseEditor editor) {
		JMenu menu = add(new JMenu(mxResources.get("settings")));
		populateDefaultSettingsMenu(editor, menu);

		JMenu submenu = (JMenu) menu.add(new JMenu(mxResources.get("addons")));
		submenu.setIcon(new ImageIcon(MenuBar.class.getResource("/org/yaoqiang/bpmn/editor/images/addons.png")));

		populateAddonsMenu(editor, submenu);
		populateLanguageMenu(editor, menu);
		populateThemeMenu(editor, menu);
	}

	protected void populateDefaultSettingsMenu(BaseEditor editor, JMenu menu) {
		menu.add(editor.bind(mxResources.get("Preferences"), ModelActions.getAction(ModelActions.PREFERENCES), "/org/yaoqiang/bpmn/editor/images/config.png"));
		menu.add(editor.bind(mxResources.get("ElementStyles"), ModelActions.getAction(ModelActions.ELEMENT_STYLES), "/org/yaoqiang/bpmn/editor/images/colors.png"));
	}

	protected void populateThemeMenu(final BaseEditor editor, JMenu menu) {
		JMenu submenu = (JMenu) menu.add(new JMenu(mxResources.get("theme")));
		submenu.setIcon(new ImageIcon(MenuBar.class.getResource("/org/yaoqiang/bpmn/editor/images/theme.png")));
		ButtonGroup themeGroup = new ButtonGroup();
		UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
		for (int i = lafs.length - 1; i >= 0; i--) {
			final String clazz = lafs[i].getClassName();
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(new AbstractAction(lafs[i].getName()) {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					editor.setLookAndFeel(clazz);
				}
			});
			if (clazz.equals(Constants.SETTINGS.getProperty("Theme", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"))) {
				item.setSelected(true);
			}
			themeGroup.add(item);
			submenu.add(item);
		}
	}

	protected void populateHelpMenu(final BaseEditor editor) {
		JMenu menu = add(new JMenu(mxResources.get("help")));

		JMenuItem item = menu.add(new JMenuItem(mxResources.get("aboutBPMNEditor")));
		item.setIcon(new ImageIcon(MenuBar.class.getResource("/org/yaoqiang/bpmn/editor/images/help.png")));
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editor.about();
			}
		});
	}

	protected void populateLanguageMenu(BaseEditor editor, JMenu menu) {
		JMenu submenu = (JMenu) menu.add(new JMenu(mxResources.get("language")));
		submenu.setIcon(new ImageIcon(MenuBar.class.getResource("/org/yaoqiang/bpmn/editor/images/language.png")));

		submenu.add(new JRadioButtonMenuItem(editor.bind(new Locale("de").getDisplayName(), EditorActions.getLocaleAction("de"))));
		submenu.add(new JRadioButtonMenuItem(editor.bind(new Locale("ru").getDisplayName(), EditorActions.getLocaleAction("ru"))));
		submenu.add(new JRadioButtonMenuItem(editor.bind(new Locale("es").getDisplayName(), EditorActions.getLocaleAction("es"))));
		submenu.add(new JRadioButtonMenuItem(editor.bind(new Locale("fr").getDisplayName(), EditorActions.getLocaleAction("fr"))));
		submenu.add(new JRadioButtonMenuItem(editor.bind(new Locale("it").getDisplayName(), EditorActions.getLocaleAction("it"))));
		submenu.add(new JRadioButtonMenuItem(editor.bind(new Locale("nl").getDisplayName(), EditorActions.getLocaleAction("nl"))));
		submenu.add(new JRadioButtonMenuItem(editor.bind(new Locale("pl").getDisplayName(), EditorActions.getLocaleAction("pl"))));
		submenu.add(new JRadioButtonMenuItem(editor.bind(new Locale("pt").getDisplayName(), EditorActions.getLocaleAction("pt"))));
		submenu.add(new JRadioButtonMenuItem(editor.bind(new Locale("en").getDisplayName(), EditorActions.getLocaleAction("en"))));
		submenu.add(new JRadioButtonMenuItem(editor.bind(new Locale("zh").getDisplayName(), EditorActions.getLocaleAction("zh_CN"))));

		for (Component com : submenu.getMenuComponents()) {
			JRadioButtonMenuItem item = (JRadioButtonMenuItem) com;
			if (item.getText().equals(new Locale(Constants.SETTINGS.getProperty("Locale", "en")).getDisplayName())) {
				item.setSelected(true);
			} else if (Constants.SETTINGS.getProperty("Locale", "en").equals("zh_CN") && item.getText().equals(new Locale("zh").getDisplayName())) {
				item.setSelected(true);
			}
		}
	}

	protected void setRulerMenuItem(BaseEditor editor) {
		rulersMenuItem = new CheckBoxMenuItem((BPMNEditor) editor, mxResources.get("rulers"), "showRulers");
	}

	protected void setGridMenuItem(BaseEditor editor) {
		gridMenuItem = new CheckBoxMenuItem((BPMNEditor) editor, mxResources.get("grid"), "showGrid");
	}

	public static CheckBoxMenuItem getWarningMenuItem() {
		return warningMenuItem;
	}

	public static CheckBoxMenuItem getOutlineMenuItem() {
		return outlineMenuItem;
	}

	public static JCheckBoxMenuItem getRulersMenuItem() {
		return rulersMenuItem;
	}

	public static JCheckBoxMenuItem getGridMenuItem() {
		return gridMenuItem;
	}

	public static CheckBoxMenuItem getAuxiliaryMenuItem() {
		return auxiliaryMenuItem;
	}

}
