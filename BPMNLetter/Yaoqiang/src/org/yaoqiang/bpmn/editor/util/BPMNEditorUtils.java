package org.yaoqiang.bpmn.editor.util;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

import org.h2.tools.RunScript;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.action.ModelActions;
import org.yaoqiang.bpmn.editor.dialog.tree.OrganizationTreeNode;
import org.yaoqiang.bpmn.editor.swing.BPMNGraphComponent;
import org.yaoqiang.bpmn.editor.swing.BaseEditor;
import org.yaoqiang.bpmn.editor.view.BPMNGraph;
import org.yaoqiang.bpmn.model.BPMNModelCodec;
import org.yaoqiang.bpmn.model.BPMNModelParsingErrors.ErrorMessage;
import org.yaoqiang.bpmn.model.BPMNModelUtils;
import org.yaoqiang.bpmn.model.elements.XMLAttribute;
import org.yaoqiang.bpmn.model.elements.XMLElement;
import org.yaoqiang.bpmn.model.elements.activities.SubProcess;
import org.yaoqiang.bpmn.model.elements.artifacts.TextAnnotation;
import org.yaoqiang.bpmn.model.elements.bpmndi.BPMNDiagram;
import org.yaoqiang.bpmn.model.elements.core.common.FlowElement;
import org.yaoqiang.bpmn.model.elements.core.infrastructure.Definitions;
import org.yaoqiang.bpmn.model.elements.core.infrastructure.Import;
import org.yaoqiang.graph.canvas.SvgCanvas;
import org.yaoqiang.graph.io.bpmn.BPMNCodec;
import org.yaoqiang.graph.io.graphml.GraphMLCodec;
import org.yaoqiang.graph.io.odt.OdtConstants;
import org.yaoqiang.graph.io.vdx.VdxCodec;
import org.yaoqiang.graph.model.GraphModel;
import org.yaoqiang.graph.model.OrganizationGraphModel;
import org.yaoqiang.graph.util.Constants;
import org.yaoqiang.graph.util.GraphUtils;

import sun.misc.BASE64Encoder;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxCellRenderer.CanvasFactory;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxDomUtils;
import com.mxgraph.util.mxImageBundle;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;

/**
 * BPMNEditorUtils
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class BPMNEditorUtils {

	public static boolean isXSDFile(String importType) {
		if (importType.equals("http://www.w3.org/2001/XMLSchema")) {
			return true;
		}
		return false;
	}

	public static String getRelativeFilePath(String location) {
		String filepath = location;
		File file = new File(location);
		if (file.exists() && file.isFile()) {
			File tmp = BPMNEditor.getCurrentFile();
			if (tmp != null) {
				if (tmp.getParent().equals(file.getParent())) {
					filepath = file.getName();
				}
			}
		} else {
			return filepath;
		}
		return filepath;
	}

	public static List<String> getWSDLMessages(Document document) {
		List<String> messages = new ArrayList<String>();
		NodeList childNodes = document.getDocumentElement().getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getLocalName() != null && node.getLocalName().equals("message")) {
				messages.add(((Element) node).getAttribute("name"));
			}
		}
		return messages;
	}

	public static List<String> getWSDLTypes(Document document) {
		List<String> elements = new ArrayList<String>();
		NodeList childNodes = document.getDocumentElement().getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getLocalName() != null && node.getLocalName().equals("types")) {
				Node schema = node.getFirstChild().getNextSibling();
				if (schema != null && schema.hasChildNodes()) {
					NodeList elementList = schema.getChildNodes();
					for (int j = 0; j < elementList.getLength(); j++) {
						Node element = elementList.item(j);
						if (element.getLocalName() != null && element.getLocalName().equals("element")) {
							elements.add(((Element) element).getAttribute("name"));
						}
					}
				}
				break;
			}
		}
		return elements;
	}

	public static List<String> getXSDElements(Document document) {
		List<String> elements = new ArrayList<String>();
		NodeList childNodes = document.getDocumentElement().getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getLocalName() != null && node.getLocalName().equals("element")) {
				elements.add(((Element) node).getAttribute("name"));
			}
		}
		return elements;
	}

	public static String getXmlFileType(Document document) {
		String type = "http://www.w3.org/2001/XMLSchema";
		NamedNodeMap attribs = document.getDocumentElement().getAttributes();
		for (int i = 0; i < attribs.getLength(); i++) {
			String uri = attribs.item(i).getNodeValue();
			if ("http://schemas.xmlsoap.org/wsdl/".equals(uri)) {
				type = uri;
				break;
			} else if ("http://www.w3.org/ns/wsdl".equals(uri)) {
				type = "http://www.w3.org/TR/wsdl20/";
				break;
			} else if ("http://www.omg.org/spec/BPMN/20100524/MODEL".equals(uri)) {
				type = uri;
				break;
			}
		}
		return type;
	}

	public static String getMediaType(String suffix) {
		String format = "";
		if (suffix.endsWith(".doc")) {
			format = "application/msword";
		} else if (suffix.endsWith(".docx") || suffix.endsWith(".pptx") || suffix.endsWith(".xlsx")) {
			format = "application/vnd.openxmlformats";
		} else if (suffix.endsWith(".gif")) {
			format = "image/gif";
		} else if (suffix.endsWith(".gz")) {
			format = "application/x-gzip";
		} else if (suffix.endsWith(".jpg") || suffix.endsWith(".jpeg")) {
			format = "image/jpeg";
		} else if (suffix.endsWith(".pdf")) {
			format = "application/pdf";
		} else if (suffix.endsWith(".png")) {
			format = "image/png";
		} else if (suffix.endsWith(".ppt")) {
			format = "application/mspowerpoint";
		} else if (suffix.endsWith(".ps")) {
			format = "application/postscript";
		} else if (suffix.endsWith(".vsd")) {
			format = "application/x-visio";
		} else if (suffix.endsWith(".xls")) {
			format = "application/excel";
		} else if (suffix.endsWith(".zip")) {
			format = "application/zip";
		} else {
			format = "application/octet-stream";
		}
		return format;
	}

	public static Map<String, Definitions> getExternalDefinitions(String parentPath, Definitions definitions) {
		Map<String, Definitions> externalDefinitions = new HashMap<String, Definitions>();
		for (XMLElement i : definitions.getImportList()) {
			Document doc = EditorUtils.parseXml(parentPath, ((Import) i).getLocation());
			if (doc != null) {
				String type = BPMNEditorUtils.getXmlFileType(doc);
				if ("http://www.omg.org/spec/BPMN/20100524/MODEL".equals(type)) {
					Definitions defs = new Definitions();
					new BPMNModelCodec().decode(doc.getDocumentElement(), defs);
					externalDefinitions.put(defs.getTargetNamespace(), defs);
					externalDefinitions.putAll(getExternalDefinitions(new File(EditorUtils.getFilePath(parentPath, ((Import) i).getLocation())).getParent(),
							defs));
				}
			}

		}
		return externalDefinitions;
	}

	public static void refreshDiagramList(BPMNEditor editor, Object diagramCell) {
		JMenu menu = editor.getDiagramsMenu();
		menu.removeAll();
		mxGraphModel model = BPMNEditor.getGraph().getModel();
		ButtonGroup diagramGroup = new ButtonGroup();
		if (diagramCell == null) {
			diagramCell = model.getChildAt(model.getRoot(), 0);
		}
		int num = 1;
		for (Object r : mxGraphModel.getChildren(model, model.getRoot())) {
			mxCell root = (mxCell) r;
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(editor.bind(num++ + ": " + (String) root.getValue(),
					ModelActions.getOpenDiagramAction(root.getId())));
			item.setName(root.getId());
			if (root == diagramCell) {
				item.setSelected(true);
			}
			diagramGroup.add(item);
			menu.add(item);
		}
	}

	public static void initRecentFileList(BaseEditor editor) {
		JMenu menu = editor.getRecentFilesmenu();
		List<String> fileList = EditorUtils.getRecentFileList();
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
		if (EditorUtils.getRecentFileList().size() == recentFileListSize) {
			menu.remove(menu.getItemCount() - 1);
		}

		menu.insert(editor.bind("0: " + filename, ModelActions.getOpenAction(filename)), 0);

		for (int i = 0; i < menu.getItemCount(); ++i) {
			mItem = (JMenuItem) menu.getMenuComponent(i);
			mItem.setText(i + ": " + mItem.getText().substring(3));

		}
		EditorUtils.saveRecentFiles(menu);
	}

	public static void insertAdditionalParticipant(BPMNGraph graph, String id, String value, boolean toTop, Object parent) {
		GraphModel model = graph.getModel();
		if (model.getCell(id) != null) {
			return;
		}
		double yOffset = 0;
		String style = "";

		mxCell subprocess = GraphUtils.getChoreographyActivity(model, parent);

		mxGeometry subgeo = model.getGeometry(subprocess);
		if (toTop) {
			yOffset = subgeo.getY() - 1;
			style = "participantAdditionalTop";
		} else {
			yOffset = subgeo.getY() + subgeo.getHeight() - 1;
			style = "participantAdditionalBottom";
		}

		mxCell participantCell = new mxCell(value, new mxGeometry(0, yOffset, Constants.ACTIVITY_WIDTH, Constants.PARTICIPANT_HEIGHT), style);
		participantCell.setId(id);
		participantCell.setVertex(true);
		graph.addCell(participantCell, (mxICell) parent);

		GraphUtils.arrangeChoreography(model, parent, false);

		graph.refresh();

	}

	public static Object getParentOrganization(BPMNGraph graph, Object child) {
		for (Object edge : graph.getIncomingEdges(child)) {
			Object parent = ((mxCell) edge).getSource();
			if (graph.isOrganizationElement(parent)) {
				return parent;
			}
		}
		return child;
	}

	public static void getChildOrganizations(BPMNGraph graph, Object parent, String type, Vector<OrganizationTreeNode> children) {
		if (parent == graph.getDefaultParent()) {
			List<Object> roots = graph.findTreeRoots(parent, true, false);
			if (roots.isEmpty()) {
				roots.addAll(Arrays.asList(graph.getChildVertices(parent)));
			}
			for (Object root : roots) {
				children.add(new OrganizationTreeNode((mxCell) root, graph, type));
			}
		} else {
			for (Object edge : graph.getOutgoingEdges(parent)) {
				Object child = ((mxCell) edge).getTarget();
				children.add(new OrganizationTreeNode((mxCell) child, graph, type));
			}
		}
	}

	public static void loadConnections(Map<String, JSONObject> connections) {
		Properties props = Utils.loadProperties(BPMNEditorConstants.YAOQIANG_CONNECTION_FILE);
		for (Entry<Object, Object> entry : props.entrySet()) {
			try {
				connections.put((String) entry.getKey(), new JSONObject(new JSONTokener((String) entry.getValue())));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void loadLdapConnections(Map<String, JSONObject> connections) {
		Properties props = Utils.loadProperties(BPMNEditorConstants.YAOQIANG_LDAP_CONNECTION_FILE);
		for (Entry<Object, Object> entry : props.entrySet()) {
			try {
				connections.put((String) entry.getKey(), new JSONObject(new JSONTokener((String) entry.getValue())));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void loadOrganizations(final BPMNEditor editor, List<JSONObject> organizations) {
		File organizationsDir = new File(Constants.YAOQIANG_USER_HOME + File.separator + BPMNEditorConstants.YAOQIANG_ORGS_DIR);
		for (File orgFile : organizationsDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".org");
			}
		})) {
			try {
				Document document = EditorUtils.parseXml(new FileInputStream(orgFile.getAbsolutePath()));
				if (document == null) {
					orgFile.renameTo(new File(orgFile.getAbsolutePath() + ".err"));
					continue;
				}
				mxCodec codec = new mxCodec(document);
				OrganizationGraphModel model = new OrganizationGraphModel();
				codec.decode(document.getDocumentElement(), model);

				if (model.getCell("com") == null) {
					orgFile.renameTo(new File(orgFile.getAbsolutePath() + ".err"));
					continue;
				}

				JSONObject obj = new JSONObject();
				try {
					obj.putOpt("id", orgFile.getName().substring(0, orgFile.getName().length() - 4));
					obj.putOpt("name", model.getOrganizationName());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				organizations.add(obj);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String generateOrganizationId() {
		String path = Constants.YAOQIANG_USER_HOME + File.separator + BPMNEditorConstants.YAOQIANG_ORGS_DIR;
		String id_start = "dc=example";
		String id_end = ",dc=com";
		int num = 1;
		while (new File(path + File.separator + id_start + num + id_end + ".org").exists()) {
			num++;
		}
		return id_start + num + id_end;
	}

	public static void saveOrganization(String id, GraphModel model) {
		String xml = mxXmlUtils.getXml(new mxCodec().encode(model));
		String filename = Constants.YAOQIANG_USER_HOME + File.separator + BPMNEditorConstants.YAOQIANG_ORGS_DIR + File.separator + id + ".org";
		try {
			mxUtils.writeFile(xml, filename);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static GraphModel getOrganizationGraphModel(String orgId) {
		OrganizationGraphModel model = new OrganizationGraphModel();
		File orgFile = new File(Constants.YAOQIANG_USER_HOME + File.separator + BPMNEditorConstants.YAOQIANG_ORGS_DIR + File.separator + orgId + ".org");
		try {
			Document document = EditorUtils.parseXml(new FileInputStream(orgFile.getAbsolutePath()));
			mxCodec codec = new mxCodec(document);
			codec.decode(document.getDocumentElement(), model);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return model;
	}

	public static void saveConnection(String id, JSONObject connection) {
		Utils.saveConfigure(id, connection.toJSONString(), BPMNEditorConstants.YAOQIANG_CONNECTION_FILE, "Yaoqiang BPMN Editor Connections");
	}

	public static void removeConnection(String id) {
		Utils.removeConfigure(id, BPMNEditorConstants.YAOQIANG_CONNECTION_FILE, "Yaoqiang BPMN Editor Connections");
	}

	public static boolean testConnection(String vendor, String urlstring, String username, String password) {
		boolean success = false;
		try {
			if (vendor.equals("Activiti")) {
				urlstring += "/login";
			}
			URL url = new URL(urlstring);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-type", "application/json");
			String data = "{\"userId\":\"" + username + "\",\"password\":\"" + password + "\"}";
			conn.getOutputStream().write(data.getBytes());
			conn.getOutputStream().flush();
			conn.getOutputStream().close();

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				success = new JSONObject(new JSONTokener(getResponseContent(conn))).optBoolean("success");
			} else {
				String message = "";
				if (conn.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
					message = mxResources.get("checkNameAndPassword");
				} else if (conn.getResponseCode() == HttpURLConnection.HTTP_BAD_METHOD) {
					message = mxResources.get("checkBaseURL");
				} else {
					message = mxResources.get("checkServer");
				}
				JOptionPane.showMessageDialog(null, message, mxResources.get("connectionFailed"), JOptionPane.ERROR_MESSAGE);
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ConnectException e) {
			JOptionPane.showMessageDialog(null, e.getMessage() + "\n" + mxResources.get("checkServer"), mxResources.get("connectionFailed"),
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return success;
	}

	public static boolean deploy(JSONObject con, File deployFile) {
		boolean success = false;

		try {
			String boundary = "**#Yaoqiang$**";

			int bytesRead, bytesAvailable, bufferSize;
			byte[] buffer;
			int maxBufferSize = 1 * 1024 * 1024;
			FileInputStream fileInputStream = new FileInputStream(deployFile);
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			HttpURLConnection conn = creatHttpURLConnection(con, "deploy");
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-type", "multipart/form-data;boundary=" + boundary);

			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

			String filename = BPMNEditor.getCurrentFile().getName();
			filename = filename.replaceAll(" ", "_");
			if (filename.endsWith(".bpmn20.xml")) {
				filename = filename.substring(0, filename.lastIndexOf(".bpmn20.xml")) + ".bar";
			} else {
				filename = filename.substring(0, filename.lastIndexOf(".")) + ".bar";
			}
			dos.writeBytes("--" + boundary + "\r\nContent-Disposition: form-data; name=\"deployment\"; filename=\"" + filename + "\"\r\n\r\n");
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}
			dos.writeBytes("\r\n--" + boundary + "--\r\n");

			fileInputStream.close();
			dos.flush();
			dos.close();

			int code = conn.getResponseCode();
			if (code == HttpURLConnection.HTTP_OK) {
				String response = getResponseContent(conn);
				success = response.indexOf("deploymentTime") > 0 || response.indexOf("success()") > 0;
			}
			conn.disconnect();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return success;
	}

	public static List<JSONObject> retrieveDefinitionsFromEngine(JSONObject con) {
		List<JSONObject> definitions = new ArrayList<JSONObject>();

		try {
			HttpURLConnection conn = creatHttpURLConnection(con, "retrieveDefinitions");
			conn.setUseCaches(false);

			int code = conn.getResponseCode();
			if (code == HttpURLConnection.HTTP_OK) {
				JSONObject response = new JSONObject(new JSONTokener(getResponseContent(conn)));
				JSONArray data = response.getJSONArray("data");
				JSONObject item;
				for (int i = 0; i < data.length(); i++) {
					JSONObject d = data.getJSONObject(i);
					item = new JSONObject();
					String name = d.optString("name");
					if (name.length() == 0) {
						name = d.optString("key");
					}
					item.putOnce("name", name);
					item.putOnce("version", d.getInt("version"));
					item.putOnce("deploymentId", d.getInt("deploymentId"));
					item.putOnce("resourceName", d.getString("resourceName"));
					item.putOnce("type", "engine");
					definitions.add(item);
				}
			}
			conn.disconnect();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return definitions;
	}

	public static List<JSONObject> retrieveDefinitionsFromRepository() {
		List<JSONObject> definitions = new ArrayList<JSONObject>();
		try {
			JSONObject item;
			ResultSet rs = BPMNEditor.dbConn.createStatement().executeQuery("SELECT ID, DEF_ID, DEF_NAME, REVISION FROM YAO_DEFINITIONS WHERE HEAD = 1");
			while (rs.next()) {
				item = new JSONObject();
				item.putOnce("id", rs.getInt(1));
				item.putOnce("def_id", rs.getString(2));
				String name = rs.getString(3);
				if (name.length() == 0) {
					name = rs.getString(2);
				}
				item.putOnce("name", name);
				item.putOnce("version", rs.getInt(4));
				item.putOnce("type", "native");
				definitions.add(item);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return definitions;
	}

	public static List<JSONObject> getRevisionsFromRepository(String def_id) {
		List<JSONObject> revisions = new ArrayList<JSONObject>();
		try {
			JSONObject item;
			PreparedStatement ps = BPMNEditor.dbConn.prepareStatement("SELECT ID, REVISION FROM YAO_DEFINITIONS WHERE DEF_ID = ?");
			ps.setString(1, def_id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				item = new JSONObject();
				item.putOnce("id", rs.getInt(1));
				item.putOnce("name", rs.getInt(2));
				revisions.add(item);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return revisions;
	}

	public static void updateRepositorySchema() {
		try {
			ResultSet rs = BPMNEditor.dbConn.createStatement().executeQuery("SELECT VALUE FROM YAO_PROPERTY WHERE NAME = 'schema.version'");
			while (rs.next()) {
				String version = rs.getString(1);
				if ("2.0.122".equals(version)) {
					RunScript.execute(BPMNEditor.dbConn,
							new InputStreamReader(BPMNEditorUtils.class.getClassLoader()
									.getResourceAsStream("org/yaoqiang/bpmn/editor/resources/db/update.sql")));
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static boolean deleteDeployment(JSONObject con, String deploymentId) {
		boolean success = false;

		try {
			HttpURLConnection conn = creatHttpURLConnection(con, "deleteDeployment", deploymentId);
			conn.setUseCaches(false);
			conn.setRequestMethod("DELETE");

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				success = new JSONObject(new JSONTokener(getResponseContent(conn))).optBoolean("success");
			}

			conn.disconnect();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return success;
	}

	public static InputStream getDeploymentResource(JSONObject con, String deploymentId, String resourceName) {
		try {
			HttpURLConnection conn = creatHttpURLConnection(con, "getDeploymentResource", deploymentId, resourceName);
			conn.setUseCaches(false);
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return conn.getInputStream();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static HttpURLConnection creatHttpURLConnection(JSONObject con, String type, String... params) {
		String urlstring = con.optString("url");
		if (con.optString("vendor").equals("Activiti")) {
			if (type.equals("retrieveDefinitions")) {
				urlstring += "/process-definitions";
			} else if (type.equals("deploy")) {
				urlstring += "/deployment";
			} else if (type.equals("deleteDeployment")) {
				urlstring += "/deployment/" + params[0] + "?cascade=true";
			} else if (type.equals("getDeploymentResource")) {
				urlstring += "/deployment/" + params[0] + "/resource/" + params[1];
			}
		}

		HttpURLConnection conn = null;
		URL url;
		try {
			BASE64Encoder enc = new BASE64Encoder();
			String userpassword = con.optString("username") + ":" + con.optString("password");
			String encodedAuthorization = enc.encode(userpassword.getBytes());

			url = new URL(urlstring);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Authorization", "Basic " + encodedAuthorization);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return conn;
	}

	private static String getResponseContent(HttpURLConnection conn) {
		StringBuffer temp = new StringBuffer();
		try {
			InputStream is = conn.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String tempLine = rd.readLine();
			while (tempLine != null) {
				temp.append(tempLine);
				tempLine = rd.readLine();
			}
			rd.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return temp.toString();
	}

	public static File createBarFile(BPMNGraphComponent graphComponent, String barFilename) {
		String[] filenames;
		String filename = BPMNEditor.getCurrentFile().getName();
		filename = filename.replaceAll(" ", "_");
		if (!filename.endsWith(".bpmn20.xml")) {
			filename = filename.substring(0, filename.lastIndexOf(".")) + ".bpmn20.xml";
		}
		String randomFilename = System.getProperty("java.io.tmpdir", "/tmp") + "/yaoqiang_deploy_" + filename.substring(0, filename.lastIndexOf(".bpmn20.xml"))
				+ System.currentTimeMillis();
		String imageFilename = randomFilename + ".png";

		BufferedImage image = mxCellRenderer.createBufferedImage(graphComponent.getGraph(), null, 1, null, graphComponent.isAntiAlias(), null, true,
				graphComponent.getCanvas());
		if (image != null) {
			try {
				ImageIO.write(image, "png", new File(imageFilename));
			} catch (IOException e) {
				e.printStackTrace();
			}
			filenames = new String[] { filename, filename.substring(0, filename.lastIndexOf(".bpmn20.xml")) + ".png" };
		} else {
			filenames = new String[] { filename };
		}

		String zipFilename = barFilename;
		if (zipFilename == null) {
			zipFilename = randomFilename + ".bar";
		}

		ZipOutputStream out;
		try {
			out = new ZipOutputStream(new FileOutputStream(zipFilename));

			int len;
			byte[] buffer = new byte[1024];

			for (int i = 0; i < filenames.length; i++) {
				String tempname = BPMNEditor.getCurrentFile().getAbsolutePath();
				if (i == 1) {
					tempname = imageFilename;
				}
				FileInputStream in = new FileInputStream(tempname);
				out.putNextEntry(new ZipEntry(filenames[i]));

				while ((len = in.read(buffer)) > 0) {
					out.write(buffer, 0, len);
				}

				out.closeEntry();
				in.close();
			}

			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new File(zipFilename);
	}

	public static void createODTFile(BPMNGraphComponent graphComponent, String filename) {
		Map<String, Object> odtFiles = new HashMap<String, Object>();
		BPMNGraph graph = graphComponent.getGraph();

		odtFiles.put("BPMNDefinition_Name", graph.getBpmnModel().getName());

		int diagramNum = 0;
		Object currentRoot = graph.getCurrentRoot();
		graph.getView().setCurrentRoot(null);
		BufferedImage image = createImageForPage(graphComponent, null);
		graph.getView().setCurrentRoot(currentRoot);
		BPMNDiagram bpmnDiagram = graph.getBpmnModel().getFirstBPMNDiagram();
		if (image != null) {
			diagramNum++;
			odtFiles.put("Pictures/BPMNDiagram_1.png", image);
			odtFiles.put("BPMNDiagram_1_Name", bpmnDiagram == null || bpmnDiagram.getName().length() == 0 ? mxResources.get("newDiagram") : bpmnDiagram.getName());
		}
		for (XMLElement el : BPMNModelUtils.getAllSubProcesses(graph.getBpmnModel())) {
			Object parent = graph.getModel().getCell(((SubProcess) el).getId());
			if (graph.getModel().isCollapsedSubProcess(parent) && graph.getChildCells(parent).length > 0) {
				graph.getView().setCurrentRoot(parent);
				image = createImageForPage(graphComponent, null);
				graph.getView().setCurrentRoot(currentRoot);
				if (image != null) {
					diagramNum++;
					odtFiles.put("Pictures/BPMNDiagram_" + diagramNum + ".png", image);
					odtFiles.put("BPMNDiagram_" + diagramNum + "_Name", ((SubProcess) el).getName());
				}
			}
		}
		if (graph.getBpmnModel().getBPMNDiagrams().size() > 1) {
			for (XMLElement el : graph.getBpmnModel().getBPMNDiagrams().getXMLElements()) {
				if (el == bpmnDiagram) {
					continue;
				}
				Object parent = graph.getModel().getCell(((BPMNDiagram) el).getBPMNPlane().getBpmnElement());
				image = createImageForPage(graphComponent, graph.getChildCells(parent));
				if (image != null) {
					diagramNum++;
					odtFiles.put("Pictures/BPMNDiagram_" + diagramNum + ".png", image);
					odtFiles.put("BPMNDiagram_" + diagramNum + "_Name", ((BPMNDiagram) el).getName());
				}
			}
		}

		odtFiles.put("mimetype", new ByteArrayInputStream("application/vnd.oasis.opendocument.text".getBytes()));
		odtFiles.put("settings.xml", new ByteArrayInputStream(createODTSettingsXml().getBytes()));
		odtFiles.put("meta.xml", new ByteArrayInputStream(createODTMetaXml().getBytes()));
		try {
			odtFiles.put("styles.xml", new ByteArrayInputStream(createODTStylesXml(new File(filename).getName()).getBytes("UTF-8")));
			odtFiles.put("content.xml", new ByteArrayInputStream(createODTContentXml(graph.getBpmnModel(), odtFiles, diagramNum).getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		odtFiles.put("META-INF/manifest.xml", new ByteArrayInputStream(createODTManifestXml(diagramNum).getBytes()));
		createZipFile(filename, odtFiles);
	}

	public static String createODTSettingsXml() {
		Document doc = mxDomUtils.createDocument();
		Element root = doc.createElement("office:document-settings");
		doc.appendChild(root);
		root.setAttribute("xmlns:office", OdtConstants.XMLNS_OFFICE);
		root.setAttribute("office:version", "1.1");
		return mxXmlUtils.getXml(doc);
	}

	public static String createODTMetaXml() {
		Document doc = mxDomUtils.createDocument();
		Element root = doc.createElement("office:document-meta");
		doc.appendChild(root);
		root.setAttribute("xmlns:office", OdtConstants.XMLNS_OFFICE);
		root.setAttribute("xmlns:meta", OdtConstants.XMLNS_META);
		root.setAttribute("office:version", "1.1");
		Element meta = doc.createElement("office:meta");
		root.appendChild(meta);
		Element metaChild = doc.createElement("meta:generator");
		metaChild.setTextContent(mxResources.get("title") + "/" + BaseEditor.VERSION);
		meta.appendChild(metaChild);
		metaChild = doc.createElement("meta:initial-creator");
		metaChild.setTextContent(mxResources.get("title"));
		meta.appendChild(metaChild);
		metaChild = doc.createElement("meta:creation-date");
		metaChild.setTextContent(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));
		meta.appendChild(metaChild);
		return mxXmlUtils.getXml(doc);
	}

	public static String createODTStylesXml(String filename) {
		Document doc = null;
		try {
			URL url = BPMNEditorUtils.class.getResource("/org/yaoqiang/bpmn/editor/resources/odt/styles.xml");
			doc = EditorUtils.parseXml(url.openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		String xmlstring = mxXmlUtils.getXml(doc);
		xmlstring = xmlstring.replaceFirst("@@FILE_NAME@@", replaceEscapedChars(filename));
		xmlstring = xmlstring.replaceFirst("@@DATE@@", new SimpleDateFormat("d MMM yyyy").format(new Date()));
		return xmlstring;
	}

	public static String createODTContentXml(Definitions bpmnModel, Map<String, Object> files, int diagramNum) {
		Document doc = null;
		try {
			URL url = BPMNEditorUtils.class.getResource("/org/yaoqiang/bpmn/editor/resources/odt/content.xml");
			doc = EditorUtils.parseXml(url.openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		String xmlstring = mxXmlUtils.getXml(doc);

		xmlstring = xmlstring.replaceFirst("@@AUTHOR@@", replaceEscapedChars(System.getProperty("user.name")));
		xmlstring = xmlstring.replaceFirst("@@DEFINITION_NAME@@", replaceEscapedChars((String) files.remove("BPMNDefinition_Name")));

		String diagrams = "";
		String diagramItem = "<text:p text:style-name=\"P5\"><draw:frame draw:style-name=\"Graphics\" draw:name=\"@@BPMN_DIAGRAM_ID@@\" text:anchor-type=\"paragraph\" svg:x=\"0in\" svg:y=\"0in\" svg:width=\"@@WIDTH@@in\" svg:height=\"@@HEIGHT@@in\" style:rel-width=\"100%\" style:rel-height=\"100%\"><draw:image xlink:href=\"@@BPMN_DIAGRAM_PATH@@\" xlink:type=\"simple\" xlink:show=\"embed\" xlink:actuate=\"onLoad\"/></draw:frame>\n@@BPMN_DIAGRAM_NAME@@\n</text:p>";
		for (int i = 1; i <= diagramNum; i++) {
			String diagramId = "BPMNDiagram_" + i;
			String diagramPath = "Pictures/BPMNDiagram_" + i + ".png";
			diagrams += diagramItem;
			diagrams = diagrams.replaceFirst("@@BPMN_DIAGRAM_ID@@", diagramId);
			diagrams = diagrams.replaceFirst("@@BPMN_DIAGRAM_PATH@@", diagramPath);
			diagrams = diagrams.replaceFirst("@@BPMN_DIAGRAM_NAME@@",
					"BPMN Diagram " + i + ": " + replaceEscapedChars((String) files.remove(diagramId + "_Name")));
			diagrams = diagrams.replaceFirst("@@WIDTH@@", "" + (double) ((BufferedImage) files.get(diagramPath)).getWidth() / 96);
			diagrams = diagrams.replaceFirst("@@HEIGHT@@", "" + (double) ((BufferedImage) files.get(diagramPath)).getHeight() / 96);
			if (((BufferedImage) files.get(diagramPath)).getHeight() < 880) {
				diagrams += "<text:p text:style-name=\"P5\"/>";
			}
		}
		xmlstring = xmlstring.replaceFirst("@@BPMN_DIAGRAMS@@", diagrams);
		xmlstring = createODTElementsContentXml(bpmnModel, xmlstring);
		return xmlstring;
	}

	public static String createODTElementsContentXml(Definitions bpmnModel, String xmlstring) {
		String elementTable = "";
		String elementRow = "<table:table-row table:style-name=\"TableRow\"><table:table-cell table:style-name=\"TableCell\"><text:p text:style-name=\"Standard\">@@TYPE@@</text:p></table:table-cell><table:table-cell table:style-name=\"TableCell\"><text:p text:style-name=\"Standard\">@@ID@@</text:p></table:table-cell><table:table-cell table:style-name=\"TableCell\"><text:p text:style-name=\"Standard\">@@NAME@@</text:p></table:table-cell><table:table-cell table:style-name=\"TableCell\"><text:p text:style-name=\"Standard\">@@DESC@@</text:p></table:table-cell></table:table-row>";
		for (XMLElement el : BPMNModelUtils.getAllFlowElements(bpmnModel)) {
			FlowElement f = (FlowElement) el;
			String desc = "";
			for (XMLElement doc : f.getDocumentations().getXMLElements()) {
				desc += doc.toValue();
			}
			elementTable += elementRow.replaceFirst("@@TYPE@@", f.getClass().getSimpleName()).replaceFirst("@@ID@@", f.getId())
					.replaceFirst("@@NAME@@", replaceEscapedChars(f.getName())).replaceFirst("@@DESC@@", replaceEscapedChars(desc));
		}
		return xmlstring.replaceFirst("@@BPMN_ELEMENTS@@", elementTable);
	}

	public static String createODTManifestXml(int diagramNum) {
		Document doc = null;
		try {
			URL url = BPMNEditorUtils.class.getResource("/org/yaoqiang/bpmn/editor/resources/odt/manifest.xml");
			doc = EditorUtils.parseXml(url.openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		String xmlstring = mxXmlUtils.getXml(doc);
		String picEntries = "";
		String picEntry = "<manifest:file-entry manifest:full-path=\"@@BPMN_DIAGRAM_PATH@@\" manifest:media-type=\"image/png\"/>";
		for (int i = 1; i <= diagramNum; i++) {
			if (picEntries.length() > 0) {
				picEntries += "\n\t";
			}
			picEntries += picEntry.replaceFirst("@@BPMN_DIAGRAM_PATH@@", "Pictures/BPMNDiagram_" + i + ".png");
		}
		xmlstring = xmlstring.replaceFirst("@@MANIFEST_FILE_ENTRY@@", picEntries);
		return xmlstring;
	}

	public static BufferedImage createImageForPage(BPMNGraphComponent graphComponent, Object[] cells) {
		BufferedImage after = null;
		BufferedImage image = mxCellRenderer.createBufferedImage(graphComponent.getGraph(), cells, 1, null, graphComponent.isAntiAlias(), null, true,
				graphComponent.getCanvas());
		if (image != null) {
			int w = image.getWidth();
			int h = image.getHeight();
			double imgH = 882.528;
			double imgW = 553.728;
			boolean scale = false;
			boolean rotate = false;

			if (Math.max(w, h) > imgH || Math.min(w, h) > imgW) {
				scale = true;
			}
			if (w > h && w > imgW) {
				rotate = true;
			}

			if (!scale && !rotate) {
				after = image;
			} else {
				AffineTransform at = new AffineTransform();
				double s = 1.0;
				if (rotate) {
					if (scale) {
						s = Math.min(imgH / w, imgW / h);
						at.scale(s, s);
					}
					at.translate(0.5 * h, 0.5 * w);
					at.rotate(Math.PI / 2);
					at.translate(-0.5 * w, -0.5 * h);
					after = new BufferedImage((int) (s * h), (int) (s * w), BufferedImage.TYPE_INT_ARGB);
				} else {
					s = Math.min(imgH / h, imgW / w);
					at.scale(s, s);
					after = new BufferedImage((int) (s * w), (int) (s * h), BufferedImage.TYPE_INT_ARGB);
				}
				AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
				after = rotateOp.filter(image, after);
			}
		}

		return after;
	}

	public static void createZipFile(String filename, Map<String, Object> files) {
		ZipOutputStream out;
		try {
			out = new ZipOutputStream(new FileOutputStream(filename));

			int len;
			byte[] buffer = new byte[1024];

			for (Entry<String, Object> entry : files.entrySet()) {
				out.putNextEntry(new ZipEntry(entry.getKey()));

				if (entry.getValue() instanceof BufferedImage) {
					ImageIO.write((BufferedImage) entry.getValue(), "png", out);
				} else if (entry.getValue() instanceof ByteArrayInputStream) {
					ByteArrayInputStream in = (ByteArrayInputStream) entry.getValue();
					while ((len = in.read(buffer)) > 0) {
						out.write(buffer, 0, len);
					}
					in.close();
				} else if (entry.getValue() instanceof String) {
					FileInputStream in = new FileInputStream((String) entry.getValue());
					while ((len = in.read(buffer)) > 0) {
						out.write(buffer, 0, len);
					}
					in.close();
				}

				out.closeEntry();

			}

			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String replaceEscapedChars(String string) {
		return string.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\\", "\\\\");
	}

	public static void export(String bpmnFile, String file) {
		String ext = ".png";
		if (file.length() > 0 && file.endsWith(".bpmn")) {
			System.out.println("Generating BPMN XML file...");
			ext = ".bpmn";
		} else if (file.length() > 0 && file.endsWith(".svg")) {
			System.out.println("Generating BPMN SVG file...");
			ext = ".svg";
		} else {
			System.out.println("Generating BPMN diagram...");
			if (file.length() > 0) {
				if (!file.endsWith(ext)) {
					int index = file.lastIndexOf(".");
					if (index > 0) {
						file = file.substring(0, file.lastIndexOf(".")) + ext;
					} else {
						file = file + ext;
					}
				}
			} else {
				int index = bpmnFile.lastIndexOf(".");
				if (index > 0) {
					file = bpmnFile.substring(0, index) + ext;
				} else {
					file = bpmnFile + ext;
				}
			}
		}

		if (new File(file).exists()) {
			String tmpFile = file;
			int num = 0;
			while (new File(tmpFile).exists()) {
				num++;
				tmpFile = file.substring(0, file.lastIndexOf(".")) + num + ext;
			}
			file = tmpFile;
		}

		BPMNGraph graph = new BPMNGraph(new GraphModel(BaseEditor.VERSION));
		BPMNGraphComponent graphComponent = new BPMNGraphComponent(graph);
		BPMNCodec codec = new BPMNCodec(graph);
		codec.decode(bpmnFile);
		if (codec.isAutolayout()) {
			for (Object pool : graph.getAllPools()) {
				GraphUtils.arrangeSwimlaneSize(graph, pool, false, false, false);
			}
			GraphUtils.arrangeSwimlanePosition(graphComponent);
		}

		if (".png".equalsIgnoreCase(ext)) {
			BufferedImage image = GraphUtils.generateDiagram(graphComponent);
			if (image != null) {
				try {
					ImageIO.write(image, ext.substring(1), new File(file));
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("The BPMN diagram is exported to an image file : " + file);
			}
		} else if (".svg".equalsIgnoreCase(ext)) {
			SvgCanvas canvas = (SvgCanvas) mxCellRenderer.drawCells(graph, null, 1, null, true, new CanvasFactory() {
				public mxICanvas createCanvas(int width, int height) {
					SvgCanvas canvas = new SvgCanvas(mxDomUtils.createSvgDocument(width, height));
					canvas.setEmbedded(true);
					return canvas;
				}
			});
			try {
				mxUtils.writeFile(mxXmlUtils.getXml(canvas.getDocument()), file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("The BPMN diagram is exported to an svg file : " + file);
		} else {
			graph.getBpmnModel().setExporter("Yaoqiang BPMN Editor");
			graph.getBpmnModel().setExporterVersion(BaseEditor.VERSION);
			try {
				mxUtils.writeFile(mxXmlUtils.getXml(codec.encode().getDocumentElement()), file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("The BPMN Definitions is exported to an xml file : " + file);
		}
	}

	public static void openGraphML(BPMNEditor editor, File file, Document document) {
		String filename = file.getName();
		filename = filename.substring(0, filename.length() - 8) + ".bpmn";

		if (new File(file.getParent() + File.separator + filename).exists()
				&& JOptionPane.showConfirmDialog(editor, mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
			return;
		}

		GraphMLCodec codec = new GraphMLCodec(editor.getGraphComponent(), document);
		codec.decode();
		editor.setCurrentFile(new File(file.getParent() + File.separator + filename));
		resetEditor(editor, true);
	}

	public static void openVdx(BPMNEditor editor, File file, Document document) {
		String filename = file.getName();
		filename = filename.substring(0, filename.length() - 4) + ".bpmn";

		if (new File(file.getParent() + File.separator + filename).exists()
				&& JOptionPane.showConfirmDialog(editor, mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
			return;
		}

		VdxCodec codec = new VdxCodec(editor.getGraphComponent(), document);
		int res = codec.decode();
		if (res == -1) {
			JOptionPane.showMessageDialog(editor, mxResources.get("warningVdxVendor"), mxResources.get("Warning"), JOptionPane.WARNING_MESSAGE);
		}
		editor.setCurrentFile(new File(file.getParent() + File.separator + filename));
		resetEditor(editor, true);
	}

	public static void openBPMN(BPMNEditor editor, Object file) {
		BPMNGraph graph = editor.getGraphComponent().getGraph();
		BPMNCodec codec = new BPMNCodec(graph);
		List<ErrorMessage> errorMessages = null;
		String parentPath = null;
		if (file instanceof File) {
			errorMessages = codec.decode(((File) file).getAbsolutePath());
			parentPath = ((File) file).getParent();
		} else {
			errorMessages = codec.decode(file);
		}

		if (errorMessages != null && errorMessages.size() > 0) {
			editor.error(errorMessages);
			editor.setModified(false);
			return;
		}
		graph.getExternalDefinitions().putAll(BPMNEditorUtils.getExternalDefinitions(parentPath, graph.getBpmnModel()));

		Set<mxImageBundle> artifacts = new HashSet<mxImageBundle>(graph.getImageBundles());
		artifacts.removeAll(BPMNEditor.getArtifacts());
		if (!artifacts.isEmpty()) {
			File adir = new File(Constants.YAOQIANG_USER_HOME + File.separator + EditorConstants.YAOQIANG_ARTIFACTS_DIR + File.separator + "Artifacts");
			if (!adir.exists()) {
				try {
					adir.mkdir();
				} catch (Exception exc) {
				}
			}
		}
		for (mxImageBundle a : artifacts) {
			String name = a.getImages().keySet().iterator().next();
			BufferedImage image = mxUtils.loadImage(a.getImages().get(name));
			ImageIcon icon = new ImageIcon(image);
			try {
				ImageIO.write(image, "png", new File(Constants.YAOQIANG_USER_HOME + File.separator + EditorConstants.YAOQIANG_ARTIFACTS_DIR + File.separator
						+ "Artifacts" + File.separator + name + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			TextAnnotation artifact = new TextAnnotation(name);
			artifact.setTextFormat("image/png");
			artifact.add(new XMLAttribute(artifact, "yaoqiang:name", name));
			editor.getArtifactsPalette().addTemplate(name, icon, "image;image=" + name, icon.getIconWidth(), icon.getIconHeight(), artifact);
			BPMNEditor.getArtifacts().add(a);
		}

		boolean resetPageFormat = true;
		if (graph.getBpmnModel().getFirstBPMNDiagram() != null) {
			if (graph.getBpmnModel().getFirstBPMNDiagram().getDocumentation().startsWith("background=")) {
				resetPageFormat = false;
			}
		}

		if (file instanceof File) {
			editor.setCurrentFile((File) file);
		} else {
			editor.setCurrentFile(null);
		}
		GraphModel model = graph.getModel();
		editor.setDiagramName((String) model.getValue(model.getChildAt(model.getRoot(), 0)));
		BPMNEditorUtils.refreshDiagramList(editor, null);
		if (codec.isAutolayout()) {
			for (Object pool : graph.getAllPools()) {
				GraphUtils.arrangeSwimlaneSize(graph, pool, false, false, false);
			}
			GraphUtils.arrangeSwimlanePosition(editor.getGraphComponent());
		}
		editor.activateAddons();
		resetEditor(editor, resetPageFormat);
	}

	public static void resetEditor(BPMNEditor editor, boolean resetPageFormat) {
		BPMNEditor.configSimulation = false;
		((JPanel) editor.getMainPane().getRightComponent()).remove(editor.getSimulationPane());
		editor.getMainPane().revalidate();
		BPMNGraphComponent graphComponent = editor.getGraphComponent();
		BPMNGraph graph = graphComponent.getGraph();
		GraphModel model = graph.getModel();
		if (resetPageFormat) {
			PageFormat pageFormat = model.getPageFormat();
			Paper paper = pageFormat.getPaper();
			paper.setSize(Constants.PAGE_HEIGHT, Constants.PAGE_WIDTH);
			double margin = 5;
			paper.setImageableArea(margin, margin, paper.getWidth() - margin * 2, paper.getHeight() - margin * 2);
			pageFormat.setPaper(paper);
			model.setPageFormat(pageFormat);
		}

		graphComponent.setPageFormat(model.getPageFormat());
		graphComponent.setVerticalPageCount(model.getPageCount());
		graphComponent.setHorizontalPageCount(model.getHorizontalPageCount());
		graphComponent.getViewport().setOpaque(false);
		graphComponent.setBackground(model.getBackgroundColor());

		GraphUtils.arrangeAllSwimlaneLength(graph, true);
		graph.refresh();
		editor.setModified(false);
		editor.getUndoManager().clear();
		editor.getGraphComponent().zoomAndCenter();
		editor.getBpmnView().refreshView(graph);
	}

}
