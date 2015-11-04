package org.yaoqiang.bpmn.editor.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.action.ModelActions;
import org.yaoqiang.bpmn.editor.swing.BaseEditor;
import org.yaoqiang.bpmn.editor.swing.Palettes;
import org.yaoqiang.bpmn.model.elements.XMLAttribute;
import org.yaoqiang.bpmn.model.elements.artifacts.TextAnnotation;
import org.yaoqiang.graph.util.Constants;

import com.mxgraph.util.mxBase64;
import com.mxgraph.util.mxImageBundle;

public class EditorUtils {

	public static void writeGzipFile(String contents, String filename) throws IOException {
		GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(filename));
		byte[] data = contents.getBytes("UTF-8");
		out.write(data, 0, data.length);
		out.flush();
		out.close();
	}

	public static Document parseGzipFile(String filename) throws IOException {
		GZIPInputStream in = new GZIPInputStream(new FileInputStream(filename));
		return parseXml(in);
	}

	public static Document parseXml(String parentPath, String location) {
		Document document = null;
		if (location.startsWith("http") || location.startsWith("ftp")) {
			try {
				document = parseXml(new URL(location).openStream());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
			}
		} else {
			try {
				document = parseXml(new FileInputStream(getFilePath(parentPath, location)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return document;
	}

	public static Document parseXml(InputStream in) {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setNamespaceAware(true);
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			return docBuilder.parse(in);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Not a valid XML file!", "Error", JOptionPane.ERROR_MESSAGE);
		}

		return null;
	}

	public static String getFilePath(String parentPath, String location) {
		String filepath = location;
		File file = new File(location);
		if (file.exists() && file.isFile()) {
			return filepath;
		} else {
			if (parentPath == null && BaseEditor.getCurrentFile() != null) {
				parentPath = BaseEditor.getCurrentFile().getParent();
			}
			if (parentPath != null) {
				filepath = parentPath + File.separator + location;
				file = new File(filepath);
				if (file.exists() && file.isFile()) {
					return filepath;
				}
			}
		}
		return filepath;
	}

	public static void addArtifact(Palettes palettes, String filename, String path) {
		String imagePath = path + filename;
		ImageIcon icon = new ImageIcon(imagePath);
		String name = filename.substring(0, filename.lastIndexOf("."));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			Image image = icon.getImage();
			BufferedImage buffImg = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = buffImg.createGraphics();
			g2.drawImage(image, null, null);
			g2.dispose();
			ImageIO.write(buffImg, "png", bos);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mxImageBundle bundle = new mxImageBundle();
		bundle.putImage(name, "data:image/png," + mxBase64.encodeToString(bos.toByteArray(), false));
		BPMNEditor.getArtifacts().add(bundle);
		BPMNEditor.getGraph().addImageBundle(bundle);
		TextAnnotation artifact = new TextAnnotation(name);
		artifact.setTextFormat("image/png");
		artifact.add(new XMLAttribute(artifact, "yaoqiang:name", name));
		palettes.addTemplate(name, icon, "image;image=" + name, icon.getIconWidth(), icon.getIconHeight(), artifact);
	}

	public static void initRecentFileList(BaseEditor editor) {
		JMenu menu = editor.getRecentFilesmenu();
		List<String> fileList = getRecentFileList();
		if (!fileList.isEmpty() && fileList.size() > 0) {
			int num = 0;
			for (String filename : fileList) {
				menu.add(editor.bind(num++ + ": " + filename, ModelActions.getOpenAction(filename)));
			}
		}
	}

	public static void addRecentFiletoList(BaseEditor editor, String filename) {
		JMenu menu = editor.getRecentFilesmenu();
		JMenuItem mItem;
		for (int i = 0; i < menu.getItemCount(); ++i) {
			mItem = (JMenuItem) menu.getMenuComponent(i);
			if (filename.equals(mItem.getText().substring(3))) {
				menu.remove(i);
			}
		}

		int recentFileListSize = 10;
		if (getRecentFileList().size() == recentFileListSize) {
			menu.remove(menu.getItemCount() - 1);
		}

		menu.insert(editor.bind("0: " + filename, ModelActions.getOpenAction(filename)), 0);

		for (int i = 0; i < menu.getItemCount(); ++i) {
			mItem = (JMenuItem) menu.getMenuComponent(i);
			mItem.setText(i + ": " + mItem.getText().substring(3));

		}
		saveRecentFiles(menu);
	}

	public static void removeRecentFiletoList(BaseEditor editor, String filename) {
		JMenu menu = editor.getRecentFilesmenu();
		JMenuItem mItem;
		for (int i = 0; i < menu.getItemCount(); ++i) {
			mItem = (JMenuItem) menu.getMenuComponent(i);
			if (filename.equals(mItem.getText().substring(3))) {
				menu.remove(i);
			}
		}

		for (int i = 0; i < menu.getItemCount(); ++i) {
			mItem = (JMenuItem) menu.getMenuComponent(i);
			mItem.setText(i + ": " + mItem.getText().substring(3));

		}
		saveRecentFiles(menu);
	}

	public static void saveRecentFiles(JMenu menu) {
		try {
			String fileList = "";
			for (int i = 0; i < menu.getItemCount(); i++) {
				JMenuItem mItem = (JMenuItem) menu.getMenuComponent(i);
				fileList += mItem.getText().substring(3);
				if (i < menu.getItemCount() - 1)
					fileList += "\n";
			}
			FileOutputStream fos = new FileOutputStream(Constants.YAOQIANG_USER_HOME + File.separator + EditorConstants.YAOQIANG_RECENT_FILES);
			fos.write(fileList.getBytes("UTF-8"));
			// Write to file
			fos.flush();
			fos.close();
		} catch (Exception ex) {
		}
	}

	public static List<String> getRecentFileList() {
		List<String> fileList = new ArrayList<String>();
		String sFile = getRecentFileString();
		if (sFile != null && sFile.length() != 0) {
			for (StringTokenizer st = new StringTokenizer(sFile, "\n"); st.hasMoreTokens();) {
				String filename = st.nextToken();
				fileList.add(filename);
			}
		}
		return fileList;
	}

	public static String getRecentFileString() {
		byte[] utf8Bytes;
		String sFile = new String("");
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(Constants.YAOQIANG_USER_HOME + File.separator + EditorConstants.YAOQIANG_RECENT_FILES);
			int noOfBytes = fis.available();
			if (noOfBytes > 0) {
				utf8Bytes = new byte[noOfBytes];
				fis.read(utf8Bytes);
				sFile = new String(utf8Bytes, "UTF8");
			}
		} catch (Exception ex) {
			return null;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception ex) {
				}
			}
		}

		return sFile;
	}

}
