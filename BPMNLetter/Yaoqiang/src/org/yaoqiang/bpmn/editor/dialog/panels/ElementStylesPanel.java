package org.yaoqiang.bpmn.editor.dialog.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.yaoqiang.bpmn.editor.dialog.CheckboxPanel;
import org.yaoqiang.bpmn.editor.dialog.Panel;
import org.yaoqiang.bpmn.editor.dialog.PanelContainer;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.util.Utils;
import org.yaoqiang.bpmn.model.elements.core.foundation.BaseElement;
import org.yaoqiang.graph.util.Constants;
import org.yaoqiang.graph.util.GraphUtils;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxStylesheet;

/**
 * ElementStylesPanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class ElementStylesPanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected CheckboxPanel shadowPanel;
	protected CheckboxPanel gradientPanel;
	protected JLabel strokeButton;

	protected JLabel fontColorButton;
	protected JComboBox fontCombo;
	protected JComboBox sizeCombo;

	protected Panel startEventStylePanel;
	protected Panel endEventStylePanel;
	protected Panel intermediateEventStylePanel;
	protected Panel gatewayStylePanel;
	protected Panel taskStylePanel;
	protected Panel subprocessStylePanel;
	protected Panel dataobjectStylePanel;
	protected Panel datastoreStylePanel;
	protected Panel conversationStylePanel;
	protected Panel poolStylePanel;
	protected Panel laneStylePanel;

	public ElementStylesPanel(final PanelContainer pc, BaseElement owner) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel comPanel = new JPanel();
		comPanel.setLayout(new BoxLayout(comPanel, BoxLayout.Y_AXIS));
		comPanel.setBorder(BorderFactory.createTitledBorder(mxResources.get("commonAttributes")));
		shadowPanel = new CheckboxPanel(pc, owner, "style_" + mxConstants.STYLE_SHADOW, true, true);
		gradientPanel = new CheckboxPanel(pc, owner, "style_" + mxConstants.STYLE_GRADIENTCOLOR, true, true);

		strokeButton = new JLabel();
		final mxGraphComponent graphComponent = getGraphComponent();
		mxStylesheet mxStylesheet = graphComponent.getGraph().getStylesheet();
		Map<String, Object> defaultStyle = mxStylesheet.getCellStyle("defaultVertex", null);
		final Color initialColor = mxUtils.getColor(defaultStyle, mxConstants.STYLE_STROKECOLOR);
		strokeButton.setOpaque(true);
		strokeButton.setBackground(initialColor);
		strokeButton.setBorder(BorderFactory.createRaisedBevelBorder());
		Dimension textDim = new Dimension(26, 26);
		strokeButton.setMinimumSize(new Dimension(textDim));
		strokeButton.setMaximumSize(new Dimension(textDim));
		strokeButton.setPreferredSize(new Dimension(textDim));
		strokeButton.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				Color newColor = JColorChooser.showDialog(graphComponent, mxResources.get("stroke"), initialColor);
				if (newColor != null) {
					strokeButton.setBackground(newColor);
				}
			}
		});

		fontColorButton = new JLabel();
		final Color fontColor = mxUtils.getColor(defaultStyle, mxConstants.STYLE_FONTCOLOR);
		fontColorButton.setOpaque(true);
		fontColorButton.setBackground(fontColor);
		fontColorButton.setBorder(BorderFactory.createRaisedBevelBorder());
		fontColorButton.setMinimumSize(new Dimension(textDim));
		fontColorButton.setMaximumSize(new Dimension(textDim));
		fontColorButton.setPreferredSize(new Dimension(textDim));
		fontColorButton.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				Color newColor = JColorChooser.showDialog(graphComponent, mxResources.get("fontColor"), fontColor);
				if (newColor != null) {
					fontColorButton.setBackground(newColor);
				}
			}
		});

		JPanel r1Panel = new JPanel();
		r1Panel.add(shadowPanel);
		r1Panel.add(gradientPanel);
		r1Panel.add(new JLabel(mxResources.get("stroke") + ": "));
		r1Panel.add(strokeButton);
		comPanel.add(r1Panel);

		JPanel r2Panel = new JPanel();
		r2Panel.add(new JLabel(mxResources.get("font") + ": "));
		r2Panel.add(fontColorButton);

		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		List<String> fonts = new ArrayList<String>();
		fonts.addAll(Arrays.asList(new String[] { this.getFont().getFamily(), "Arial", "Helvetica", "Verdana", "Times New Roman", "Courier New", "-" }));
		fonts.addAll(Arrays.asList(env.getAvailableFontFamilyNames()));

		fontCombo = new JComboBox(fonts.toArray());
		fontCombo.setSelectedItem(Constants.SETTINGS.getProperty("style_" + mxConstants.STYLE_FONTFAMILY, ""));
		fontCombo.setMinimumSize(new Dimension(150, 0));
		fontCombo.setPreferredSize(new Dimension(150, 26));
		fontCombo.setMaximumSize(new Dimension(150, 100));
		r2Panel.add(fontCombo);

		sizeCombo = new JComboBox(new Object[] { "6pt", "8pt", "9pt", "10pt", "11pt", "12pt", "14pt", "18pt", "24pt", "30pt", "36pt", "48pt", "60pt" });
		sizeCombo.setSelectedItem(Constants.SETTINGS.getProperty("style_" + mxConstants.STYLE_FONTSIZE, "11") + "pt");
		sizeCombo.setMinimumSize(new Dimension(65, 0));
		sizeCombo.setPreferredSize(new Dimension(65, 26));
		sizeCombo.setMaximumSize(new Dimension(65, 100));
		r2Panel.add(sizeCombo);

		comPanel.add(r2Panel);

		this.add(comPanel);

		JPanel elementsPanel = new JPanel();
		elementsPanel.setLayout(new BoxLayout(elementsPanel, BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(elementsPanel);
		scrollPane.setBorder(BorderFactory.createTitledBorder(mxResources.get("elements")));
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(600, 350));
		startEventStylePanel = new ElementStylePanel(pc, owner, "startEvent");
		intermediateEventStylePanel = new ElementStylePanel(pc, owner, "intermediateEvent");
		endEventStylePanel = new ElementStylePanel(pc, owner, "endEvent");
		gatewayStylePanel = new ElementStylePanel(pc, owner, "gateway");
		taskStylePanel = new ElementStylePanel(pc, owner, "task");
		subprocessStylePanel = new ElementStylePanel(pc, owner, "subprocess");
		dataobjectStylePanel = new ElementStylePanel(pc, owner, "dataObject");
		datastoreStylePanel = new ElementStylePanel(pc, owner, "dataStore");
		conversationStylePanel = new ElementStylePanel(pc, owner, "conversation");
		poolStylePanel = new ElementStylePanel(pc, owner, "pool");
		laneStylePanel = new ElementStylePanel(pc, owner, "lane");

		elementsPanel.add(startEventStylePanel);
		elementsPanel.add(intermediateEventStylePanel);
		elementsPanel.add(endEventStylePanel);
		elementsPanel.add(gatewayStylePanel);
		elementsPanel.add(taskStylePanel);
		elementsPanel.add(subprocessStylePanel);
		elementsPanel.add(dataobjectStylePanel);
		elementsPanel.add(datastoreStylePanel);
		elementsPanel.add(conversationStylePanel);
		elementsPanel.add(poolStylePanel);
		elementsPanel.add(laneStylePanel);

		this.add(scrollPane);

		JButton buttonOpt = getDialog().getOptionButton();
		if (buttonOpt != null) {
			buttonOpt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int o = JOptionPane.showConfirmDialog(null, mxResources.get("resetDefaultStyle"), mxResources.get("reset"), JOptionPane.YES_NO_CANCEL_OPTION);
					if (o == JOptionPane.YES_OPTION) {
						Set<Object> props = new HashSet<Object>(Constants.SETTINGS.keySet());
						for (Object prop : props) {
							if (prop.toString().startsWith("style_")) {
								Constants.SETTINGS.remove(prop);
							}
						}
						Utils.saveConfigureFile();
						if (mxConstants.DEFAULT_STARTSIZE != 25) {
							mxConstants.DEFAULT_STARTSIZE = 25;
							GraphUtils.arrangeAllSwimlaneLength(getGraphComponent().getGraph(), true);
						}
						getGraphComponent().getGraph().refresh();
						getDialog().close();
					}
				}
			});
		}
	}

	public void saveObjects() {
		shadowPanel.saveObjects();
		gradientPanel.saveObjects();

		Constants.SETTINGS.put("style_" + mxConstants.STYLE_STROKECOLOR, "#" + mxUtils.getHexColorString(strokeButton.getBackground()).substring(2));
		Constants.SETTINGS.put("style_" + mxConstants.STYLE_FONTCOLOR, "#" + mxUtils.getHexColorString(fontColorButton.getBackground()).substring(2));
		if (fontCombo != null) {
			String font = fontCombo.getSelectedItem().toString();
			if (font != null && !font.equals("-")) {
				Constants.SETTINGS.put("style_" + mxConstants.STYLE_FONTFAMILY, font);
			}
		}
		if (sizeCombo != null) {
			Constants.SETTINGS.put("style_" + mxConstants.STYLE_FONTSIZE, sizeCombo.getSelectedItem().toString().replace("pt", ""));
		}

		startEventStylePanel.saveObjects();
		intermediateEventStylePanel.saveObjects();
		endEventStylePanel.saveObjects();
		gatewayStylePanel.saveObjects();
		taskStylePanel.saveObjects();
		subprocessStylePanel.saveObjects();
		dataobjectStylePanel.saveObjects();
		datastoreStylePanel.saveObjects();
		conversationStylePanel.saveObjects();
		int oldValue = mxConstants.DEFAULT_STARTSIZE;
		poolStylePanel.saveObjects();
		laneStylePanel.saveObjects();
		if (mxConstants.DEFAULT_STARTSIZE != oldValue) {
			GraphUtils.arrangeAllSwimlaneLength(getGraphComponent().getGraph(), true);
		}

		getGraphComponent().getGraph().refresh();
		Utils.saveConfigureFile();
	}

}
