package org.yaoqiang.bpmn.editor.simulation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.LinkedHashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.action.ModelActions;
import org.yaoqiang.bpmn.editor.dialog.JSONTablePanel;
import org.yaoqiang.bpmn.editor.dialog.XMLTablePanel;

import com.mxgraph.util.mxResources;

/**
 * MainPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class MainPanel extends JPanel {

	private static final long serialVersionUID = 6443111628904448679L;

	protected BPMNEditor editor;

	protected JPopupMenu popup = new JPopupMenu();

	protected LinkedHashMap<String, String> choiceMap = new LinkedHashMap<String, String>();

	protected XMLTablePanel properties = new XMLTablePanel(null, null, "predefinedProp", "predefinedProps", Arrays.asList("scope", "type", "name", "value"),
			null, 380, 500, false, false);

	protected JSONTablePanel worklist = new JSONTablePanel(null, this, "worklist", "Worklist (Not Yet Implemented)", Arrays.asList("workitem"), null, 380, 150,
			false, false);

	public MainPanel(BPMNEditor editor) {
		this.editor = editor;
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder(mxResources.get("simulation")));
		setMinimumSize(new Dimension(320, 500));
		setPreferredSize(new Dimension(320, 500));
		setMaximumSize(new Dimension(320, 500));

		add(createToolbar(), BorderLayout.NORTH);

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.add(Box.createVerticalStrut(10));
		add(centerPanel, BorderLayout.CENTER);

		final JScrollPane propertiesPane = new JScrollPane(properties);
		propertiesPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		propertiesPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		centerPanel.add(propertiesPane);
		// centerPanel.add(worklist);
	}

	public XMLTablePanel getPropertiesTable() {
		return properties;
	}

	private JToolBar createToolbar() {
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.add(editor.bind(mxResources.get("run"), ModelActions.getAction(ModelActions.RUN_SIMULATION), "/org/yaoqiang/bpmn/editor/images/run.png"));
		toolbar.add(editor.bind(mxResources.get("pause"), ModelActions.getAction(ModelActions.PAUSE_SIMULATION), "/org/yaoqiang/bpmn/editor/images/pause.png"));
		toolbar.add(editor.bind(mxResources.get("stop"), ModelActions.getAction(ModelActions.STOP_SIMULATION), "/org/yaoqiang/bpmn/editor/images/stop.png"));
		toolbar.add(editor.bind(mxResources.get("Speed"), ModelActions.getAction(ModelActions.SIMULATION_SPEED), "/org/yaoqiang/bpmn/editor/images/timer.png"));
		toolbar.addSeparator();
		toolbar.add(editor.bind(mxResources.get("close"), ModelActions.getAction(ModelActions.CLOSE_SIMULATION), "/org/yaoqiang/bpmn/editor/images/exit.png"));
		return toolbar;
	}

}
