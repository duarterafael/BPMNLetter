package org.yaoqiang.bpmn.editor.dialog;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import org.yaoqiang.bpmn.editor.util.Utils;
import org.yaoqiang.graph.util.Constants;

import com.mxgraph.util.mxResources;


/**
 * WarningDialog
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class WarningDialog extends JDialog {

	private static final long serialVersionUID = -3378029138434324390L;

	public WarningDialog(final String type) {
		super((Frame)null);
		setTitle(mxResources.get("optionTitle"));
		setModal(true);
		setLayout(new BorderLayout());

		JPanel option = new JPanel();
		option.setLayout(new BoxLayout(option, BoxLayout.X_AXIS));
		getContentPane().add(option, BorderLayout.CENTER);

		option.add(Box.createHorizontalStrut(5));
		final JCheckBox jcb = new JCheckBox(mxResources.get("notShowAgain"));
		jcb.setSelected(false);
		option.add(jcb);

		String text = mxResources.get(type);
		if (type.startsWith("jidremoved:")) {
			text = mxResources.get("contact") + type.substring(11) + " " + mxResources.get("infoRemovedYouFromContactList");
		} else if (type.startsWith("jidadded:")) {
			text = mxResources.get("contact") + type.substring(9) + " " + mxResources.get("infoAddedYouToContactList");
		}
		JLabel content = new JLabel(text);
		content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		getContentPane().add(content, BorderLayout.NORTH);


		JPanel buttonPanel = new JPanel();
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		JButton okButton = new JButton(mxResources.get("ok"));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String key = type;
				if (type.startsWith("jidremoved:")) {
					key = "infoContactItemRemoved";
				} else if (type.startsWith("jidadded:")) {
					key = "infoContactItemAdded";
				}
				Constants.SETTINGS.put(key, jcb.isSelected() ? "0" : "1");
				Utils.saveConfigureFile();
				setVisible(false);
			}
		});
		buttonPanel.add(okButton);

		getRootPane().setDefaultButton(okButton);

		setResizable(false);
		pack();
		setLocationRelativeTo(null);
	}

	protected JRootPane createRootPane() {
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		JRootPane rootPane = new JRootPane();
		rootPane.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				setVisible(false);
			}
		}, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		return rootPane;
	}

}
