package org.yaoqiang.bpmn.editor.dialog.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.yaoqiang.bpmn.editor.dialog.PanelContainer;
import org.yaoqiang.bpmn.editor.dialog.SpinnerPanel;
import org.yaoqiang.bpmn.editor.dialog.XMLPanel;
import org.yaoqiang.bpmn.editor.util.Utils;
import org.yaoqiang.bpmn.model.elements.core.foundation.BaseElement;
import org.yaoqiang.graph.util.Constants;
import org.yaoqiang.graph.util.GraphUtils;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxStylesheet;

/**
 * ElementStylePanel
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class ElementStylePanel extends XMLPanel {

	private static final long serialVersionUID = 1L;

	protected SpinnerPanel sizePanel;

	protected SpinnerPanel widthPanel;

	protected SpinnerPanel heightPanel;

	protected JLabel colorButton;

	protected String style;

	public ElementStylePanel(final PanelContainer pc, BaseElement owner, final String style) {
		super(pc, owner);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.style = style;

		final mxGraphComponent graphComponent = getGraphComponent();
		mxStylesheet mxStylesheet = getGraphComponent().getGraph().getStylesheet();

		Notation notation = new Notation(style);

		JPanel stylePanel = new JPanel();
		if (style.endsWith("Event") || "gateway".equals(style) || "message".equals(style)) {
			String size = "32";
			int min = 25;
			int max = 60;
			if ("gateway".equals(style)) {
				size = "42";
				min = 40;
				max = 60;
			} else if ("message".equals(style)) {
				size = "35";
				min = 25;
				max = 60;
			}

			stylePanel.add(new JLabel(mxResources.get("size") + ": "));
			sizePanel = new SpinnerPanel(Integer.parseInt(Constants.SETTINGS.getProperty("style_" + style + "_size", size)), min, max, 52);
			sizePanel.getSpinner().addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					pc.panelChanged();
				}
			});
			stylePanel.add(sizePanel);
		} else {
			String width = "85";
			String height = "55";
			int min = 25;
			int max = 150;
			if ("subprocess".equals(style)) {
				min = 125;
				max = 600;
				width = "400";
				height = "250";
			} else if ("pool".equals(style)) {
				min = 125;
				max = 600;
				height = "200";
			} else if ("dataObject".equals(style)) {
				min = 25;
				max = 150;
				width = "29";
				height = "38";
			} else if ("dataStore".equals(style)) {
				min = 25;
				max = 150;
				width = "35";
				height = "30";
			} else if ("conversation".equals(style)) {
				min = 25;
				max = 60;
				width = "40";
				height = "35";
			} else if (style.startsWith("organization")) {
				min = 25;
				max = 300;
				width = "150";
				height = "35";
			}

			if ("pool".equals(style)) {
				stylePanel.add(new JLabel(mxResources.get("titleSize") + ": "));
				sizePanel = new SpinnerPanel(Integer.parseInt(Constants.SETTINGS.getProperty("style_swimlane_title_size", "25")), 15, 50, 52);
				sizePanel.getSpinner().addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						pc.panelChanged();
					}
				});
				stylePanel.add(sizePanel);
			} else if ("lane".equals(style)) {
				stylePanel.add(new JLabel(mxResources.get("opacity") + ": "));
				sizePanel = new SpinnerPanel(Integer.parseInt(Constants.SETTINGS.getProperty("style_lane_opacity", "30")), 0, 100, 52);
				sizePanel.getSpinner().addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						pc.panelChanged();
					}
				});
				stylePanel.add(sizePanel);
			}

			if (!"pool".equals(style) && !"lane".equals(style)) {
				stylePanel.add(new JLabel(mxResources.get("Width") + ": "));
				widthPanel = new SpinnerPanel(Integer.parseInt(Constants.SETTINGS.getProperty("style_" + style + "_" + Constants.WIDTH, width)), min, max, 52);
				widthPanel.getSpinner().addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						pc.panelChanged();
					}
				});
				stylePanel.add(widthPanel);
			}

			if (!"lane".equals(style)) {
				stylePanel.add(new JLabel(mxResources.get("height") + ": "));
				heightPanel = new SpinnerPanel(Integer.parseInt(Constants.SETTINGS.getProperty("style_" + style + "_" + Constants.HEIGHT, height)), min, max,
						52);
				heightPanel.getSpinner().addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						pc.panelChanged();
					}
				});
				stylePanel.add(heightPanel);
			}
		}

		if (!"message".equals(style)) {
			stylePanel.add(new JLabel(mxResources.get("background") + ": "));
			colorButton = new JLabel();
			Map<String, Object> elementStyle = mxStylesheet.getCellStyle(style, null);
			final Color color = mxUtils.getColor(elementStyle, mxConstants.STYLE_FILLCOLOR);
			colorButton.setOpaque(true);
			colorButton.setBackground(color);
			colorButton.setBorder(BorderFactory.createRaisedBevelBorder());
			Dimension textDim = new Dimension(26, 26);
			colorButton.setMinimumSize(new Dimension(textDim));
			colorButton.setMaximumSize(new Dimension(textDim));
			colorButton.setPreferredSize(new Dimension(textDim));
			colorButton.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					Color newColor = JColorChooser.showDialog(graphComponent, mxResources.get("background"), color);
					if (newColor != null) {
						colorButton.setBackground(newColor);
					}
				}
			});
			stylePanel.add(colorButton);
		}

		JButton resetButton = new JButton(mxResources.get("reset"));
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int o = JOptionPane.showConfirmDialog(null, mxResources.get("resetDefaultStyle"), mxResources.get("reset"), JOptionPane.YES_NO_CANCEL_OPTION);
				if (o == JOptionPane.YES_OPTION) {
					Set<Object> props = new HashSet<Object>(Constants.SETTINGS.keySet());
					for (Object prop : props) {
						if (prop.toString().startsWith("style_" + style)) {
							Constants.SETTINGS.remove(prop);
						}
					}
					if ("pool".equals(style) && mxConstants.DEFAULT_STARTSIZE != 25) {
						Constants.SETTINGS.remove("style_swimlane_title_size");
						mxConstants.DEFAULT_STARTSIZE = 25;
						GraphUtils.arrangeAllSwimlaneLength(getGraphComponent().getGraph(), true);
					}
					Utils.saveConfigureFile();
					getGraphComponent().getGraph().refresh();
					getDialog().close();
				}
			}
		});
		stylePanel.add(resetButton);

		JPanel elementPanel = new JPanel();
		elementPanel.setLayout(new BoxLayout(elementPanel, BoxLayout.X_AXIS));
		elementPanel.add(notation);
		elementPanel.add(stylePanel);

		this.add(elementPanel);

	}

	public void saveObjects() {
		if (sizePanel != null) {
			if ("pool".equals(style)) {
				mxConstants.DEFAULT_STARTSIZE = Integer.parseInt(sizePanel.getValue().toString());
				Constants.SWIMLANE_SIZE = Integer.parseInt(heightPanel.getValue().toString());
				Constants.SETTINGS.put("style_swimlane_title_size", sizePanel.getValue().toString());
			} else if ("lane".equals(style)) {
				Constants.SETTINGS.put("style_lane_opacity", sizePanel.getValue().toString());
			} else {
				Constants.SETTINGS.put("style_" + style + "_size", sizePanel.getValue().toString());
			}
		}
		if (heightPanel != null) {
			Constants.SETTINGS.put("style_" + style + "_" + Constants.HEIGHT, heightPanel.getValue().toString());
		}
		if (widthPanel != null) {
			Constants.SETTINGS.put("style_" + style + "_" + Constants.WIDTH, widthPanel.getValue().toString());
		}
		Constants.SETTINGS.put("style_" + style + "_" + mxConstants.STYLE_FILLCOLOR, "#" + mxUtils.getHexColorString(colorButton.getBackground()).substring(2));

		Utils.saveConfigureFile();
	}

	protected class Notation extends JLabel {
		private static final long serialVersionUID = 1L;

		protected String style;
		protected String value;

		public Notation(String style) {
			super();
			this.style = style;
			this.value = mxResources.get(style);
			Dimension textDim = new Dimension(110, 80);
			if (style.endsWith("Event")) {
				textDim.setSize(110, 60);
			} else if ("message".equals(style)) {
				textDim.setSize(60, 60);
			}
			setMinimumSize(new Dimension(textDim));
			setMaximumSize(new Dimension(textDim));
			setPreferredSize(new Dimension(textDim));
			setToolTipText(value);
		}

		protected void paintComponent(Graphics g) {
			mxGraphics2DCanvas canvas = new mxGraphics2DCanvas((Graphics2D) g);
			mxGraphComponent graphComponent = ElementStylePanel.this.getGraphComponent();
			mxCell cell = new mxCell(value, new mxGeometry(), style);
			cell.setVertex(true);
			mxCellState state = new mxCellState(graphComponent.getGraph().getView(), cell, graphComponent.getGraph().getCellStyle(cell));
			state.add(cell.getGeometry());
			if (style.endsWith("Event")) {
				state.setX(35);
				state.setY(5);
				state.setWidth(32);
				state.setHeight(32);
			} else if ("gateway".equals(style)) {
				state.setX(30);
				state.setY(5);
				state.setWidth(42);
				state.setHeight(42);
			} else if ("task".equals(style) || "subprocess".equals(style)) {
				state.setX(12);
				state.setY(5);
				state.setWidth(80);
				state.setHeight(50);
			} else if ("message".equals(style)) {
				state.setX(5);
				state.setY(5);
				state.setWidth(35);
				state.setHeight(30);
			} else if ("pool".equals(style) || "lane".equals(style)) {
				state.setX(12);
				state.setWidth(100);
				state.setHeight(60);
			} else if ("dataObject".equals(style)) {
				state.setX(38);
				state.setY(5);
				state.setWidth(29);
				state.setHeight(38);
			} else if ("dataStore".equals(style)) {
				state.setX(35);
				state.setY(5);
				state.setWidth(35);
				state.setHeight(30);
			} else if ("conversation".equals(style)) {
				state.setX(35);
				state.setY(5);
				state.setWidth(40);
				state.setHeight(35);
			} else if (style.startsWith("organization")) {
				state.setX(5);
				state.setY(5);
				state.setWidth(100);
				state.setHeight(35);
			}
			mxRectangle labelBounds = new mxRectangle(state);
			if (!"pool".equals(style) && !"lane".equals(style)) {
				if ("message".equals(style)) {
					labelBounds.setX(0);
				}
				labelBounds.setY(state.getY() + state.getHeight() + 5);
			}
			state.setLabelBounds(labelBounds);
			if ("message".equals(style)) {
				canvas.drawImage(state.getRectangle(), "/org/yaoqiang/graph/images/message_initiating.png", true, false, false);
			} else {
				canvas.drawCell(state);
			}
			canvas.drawLabel(value, state, false);

		}
	}
}
