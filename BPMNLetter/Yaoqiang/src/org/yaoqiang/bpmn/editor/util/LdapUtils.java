package org.yaoqiang.bpmn.editor.util;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.ContextNotEmptyException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.dialog.ldaptree.LdapTreeNode;
import org.yaoqiang.bpmn.editor.view.BPMNGraph;
import org.yaoqiang.graph.action.GraphActions;
import org.yaoqiang.graph.model.GraphModel;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxDomUtils;

/**
 * LdapUtils
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class LdapUtils {

	public static void saveLdapConnection(String id, JSONObject connection) {
		Utils.saveConfigure(id, connection.toJSONString(), BPMNEditorConstants.YAOQIANG_LDAP_CONNECTION_FILE, "Yaoqiang BPMN Editor LDAP Connections");
	}

	public static void removeLdapConnection(String id) {
		Utils.removeConfigure(id, BPMNEditorConstants.YAOQIANG_LDAP_CONNECTION_FILE, "Yaoqiang BPMN Editor LDAP Connections");
	}

	public static void deleteLdapEntry(JSONObject con, String dn) {
		try {
			DirContext ctx = initLdapConnection(con);
			try {
				ctx.unbind(dn);
			} catch (ContextNotEmptyException ne) {
				deleteLdapSubtree(dn, con);
			}
		} catch (NamingException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), mxResources.get("error"), JOptionPane.ERROR_MESSAGE);
		}
	}

	public static String editLdapEntry(JSONObject con, String type, String uid, String mail, String name, String sn, String ou, String manager, List<JSONObject> members,
			String pdn, String rdn, String oldDn) {
		String dn = null;
		DirContext ctx = null;
		Attributes attrs = null;
		try {
			ctx = initLdapConnection(con);

			if (oldDn != null) {
				attrs = BPMNEditor.getLdapEntries().get(oldDn);
			} else {
				attrs = new BasicAttributes();
			}

			BasicAttribute objectClass = new BasicAttribute("objectClass");

			if ("person".equals(type)) {
				objectClass.add("inetOrgPerson");
				objectClass.add("organizationalPerson");
				objectClass.add("person");
				objectClass.add("top");
			} else if ("ou".equals(type)) {
				objectClass.add("organizationalUnit");
				objectClass.add("top");
			} else if ("o".equals(type)) {
				objectClass.add("organization");
				objectClass.add("top");
			} else if ("role".equals(type)) {
				objectClass.add("organizationalRole");
				objectClass.add("top");
				if (members != null && !members.isEmpty()) {
					BasicAttribute roleOccupant = new BasicAttribute("roleOccupant");
					for (JSONObject m : members) {
						roleOccupant.add(m.optString("dn"));
					}
					attrs.put(roleOccupant);
				}
			} else if ("group".equals(type)) {
				objectClass.add("groupOfNames");
				objectClass.add("top");
				if (members != null && !members.isEmpty()) {
					BasicAttribute member = new BasicAttribute("member");
					for (JSONObject m : members) {
						member.add(m.optString("dn"));
					}
					attrs.put(member);
				}
			}

			attrs.put(objectClass);

			if ("person".equals(type)) {
				attrs.put("cn", name);
				attrs.put("sn", sn);

				if (ou.length() > 0) {
					attrs.put("ou", ou);
				} else {
					attrs.remove("ou");
				}
				if (manager.length() > 0) {
					attrs.put("manager", manager);
				} else {
					attrs.remove("manager");
				}
				if (uid.length() > 0) {
					attrs.put("uid", uid);
				} else {
					attrs.remove("uid");
				}
				if (mail.length() > 0) {
					attrs.put("mail", mail);
				} else {
					attrs.remove("mail");
				}
			} else if ("ou".equals(type)) {
				attrs.put("ou", name);
			} else if ("o".equals(type)) {
				attrs.put("o", name);
			} else if ("group".equals(type) || "role".equals(type)) {
				attrs.put("cn", name);
				if (ou.length() > 0) {
					attrs.put("ou", ou);
				} else {
					attrs.remove("ou");
				}
			} else {
				attrs.put("cn", name);
			}

			if (mxResources.get("uid").equals(rdn)) {
				dn = "uid=" + uid + "," + pdn;
			} else if (mxResources.get("cn").equals(rdn)) {
				dn = "cn=" + name + "," + pdn;
			} else if (mxResources.get("ou").equals(rdn)) {
				dn = "ou=" + name + "," + pdn;
			} else if (mxResources.get("o").equals(rdn)) {
				dn = "o=" + name + "," + pdn;
			}

			if (oldDn != null) {
				if (oldDn.equals(dn)) {
					ctx.rebind(dn, null, attrs);
				} else if (mxResources.get("uid").equals(rdn) ? uid.length() > 0 : name.length() > 0) {
					ctx.unbind(oldDn);
					ctx.bind(dn, null, attrs);
				} else {
					return null;
				}
			} else {
				ctx.bind(dn, null, attrs);
			}

			return dn;
		} catch (ContextNotEmptyException cne) {
			Map<String, Attributes> subtree = deleteLdapSubtree(oldDn, con);
			subtree.remove(oldDn);

			Map<String, Attributes> newtree = new TreeMap<String, Attributes>(new Comparator<String>() {
				public int compare(String k1, String k2) {
					int i = k1.split(",").length - k2.split(",").length;
					if (i == 0) {
						return k1.compareTo(k2);
					} else {
						return i;
					}
				}
			});
			for (Entry<String, Attributes> entry : subtree.entrySet()) {
				String key = entry.getKey();
				if (!oldDn.equals(dn)) {
					key = key.substring(0, key.lastIndexOf(oldDn)) + dn;
				}
				newtree.put(key, entry.getValue());
			}

			try {
				ctx.bind(dn, null, attrs);
				for (Entry<String, Attributes> entry : newtree.entrySet()) {
					ctx.bind(entry.getKey(), null, entry.getValue());
				}
			} catch (NamingException e) {
				e.printStackTrace();
				return null;
			}
			return dn;
		} catch (NamingException ne) {
			ne.printStackTrace();
			JOptionPane.showMessageDialog(null, ne.getMessage(), mxResources.get("error"), JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	public static Map<String, Attributes> deleteLdapSubtree(String baseDN, JSONObject con) {
		Map<String, Attributes> subtree = searchLdapSubtree(baseDN, con);

		List<String> dnList = new ArrayList<String>(subtree.keySet());
		Collections.sort(dnList, new Comparator<String>() {
			public int compare(String o1, String o2) {
				int i = o2.split(",").length - o1.split(",").length;
				if (i == 0) {
					return o2.compareTo(o1);
				} else {
					return i;
				}
			}
		});
		DirContext ctx;
		try {
			ctx = initLdapConnection(con);
			for (String dname : dnList) {
				ctx.unbind(dname);
			}
		} catch (NamingException e) {
			e.printStackTrace();
			return subtree;
		}

		return subtree;
	}

	public static Map<String, Attributes> searchLdapSubtree(String baseDN, JSONObject con) {
		return searchLdap(con.optString("protocol").substring(6), con.optString("url"), baseDN, "Subtree", "(objectClass=*)",
				Long.parseLong(con.optString("countlimit")), Integer.parseInt(con.optString("timelimit")) * 1000, !con.optBoolean("useSecurityCredentials"),
				con.optString("userDN"), con.optString("password"));
	}

	public static boolean testLdapConnection(String version, String urlstring, String baseDN, boolean anonymous, String username, String password) {
		return !searchLdap(version, urlstring, baseDN, "Base", "(objectClass=*)", 0, 0, anonymous, username, password).isEmpty();
	}

	public static Map<String, Attributes> connectLdap(String version, String urlstring, String baseDN, long countlimit, int timelimit, boolean anonymous,
			String username, String password) {
		return searchLdap(version, urlstring, baseDN, "Subtree", "(objectClass=*)", countlimit, timelimit, anonymous, username, password);
	}

	public static Map<String, Attributes> searchLdap(JSONObject con) {
		return searchLdap(con.optString("protocol").substring(6), con.optString("url"), con.optString("baseDN"), con.optString("scope"),
				con.optString("filter"), Long.parseLong(con.optString("countlimit")), Integer.parseInt(con.optString("timelimit")) * 1000,
				!con.optBoolean("useSecurityCredentials"), con.optString("userDN"), con.optString("password"));
	}

	public static Map<String, Attributes> searchLdap(String version, String urlstring, String baseDN, String scope, String filter, long countlimit,
			int timelimit, boolean anonymous, String username, String password) {
		Map<String, Attributes> results = new TreeMap<String, Attributes>(new Comparator<String>() {
			public int compare(String k1, String k2) {
				int i = k1.split(",").length - k2.split(",").length;
				if (i == 0) {
					return k1.compareTo(k2);
				} else {
					return i;
				}
			}
		});
		try {
			DirContext ctx = initLdapConnection(version, urlstring, anonymous, username, password);

			SearchControls constraints = new SearchControls();
			int s = SearchControls.OBJECT_SCOPE;
			if ("Subtree".equals(scope)) {
				s = SearchControls.SUBTREE_SCOPE;
			} else if ("One level".equals(scope)) {
				s = SearchControls.ONELEVEL_SCOPE;
			}
			constraints.setSearchScope(s);
			constraints.setCountLimit(countlimit);
			constraints.setTimeLimit(timelimit);

			NamingEnumeration<SearchResult> result = ctx.search(baseDN, filter, constraints);
			while (result.hasMore()) {
				SearchResult r = result.next();
				results.put(r.getNameInNamespace(), r.getAttributes());
			}
		} catch (CommunicationException e) {
			JOptionPane.showMessageDialog(null, e.getRootCause() + "\n" + mxResources.get("checkServer"), mxResources.get("connectionFailed"),
					JOptionPane.ERROR_MESSAGE);
		} catch (NamingException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), mxResources.get("connectionFailed"), JOptionPane.ERROR_MESSAGE);
		}
		return results;
	}

	public static DirContext initLdapConnection(JSONObject con) throws NamingException {
		return initLdapConnection(con.optString("protocol").substring(6), con.optString("url"), !con.optBoolean("useSecurityCredentials"),
				con.optString("userDN"), con.optString("password"));
	}

	public static DirContext initLdapConnection(String version, String urlstring, boolean anonymous, String username, String password) throws NamingException {
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

		env.put("java.naming.ldap.version", version);
		env.put(Context.PROVIDER_URL, urlstring);

		if (anonymous) {
			env.put(Context.SECURITY_AUTHENTICATION, "none");
		} else {
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, username);
			env.put(Context.SECURITY_CREDENTIALS, password);
		}

		return new InitialDirContext(env);
	}

	public static Map<String, LdapTreeNode> buildLdapTree(JSONObject con) {
		Map<String, Attributes> entries = searchLdap(con);
		BPMNEditor.setLdapEntries(entries);
		return buildLdapTreeNodes(con.optString("baseDN"), entries);
	}

	public static Map<String, LdapTreeNode> buildLdapTreeNodes(String baseDN, Map<String, Attributes> entries) {
		Map<String, LdapTreeNode> nodes = new HashMap<String, LdapTreeNode>();
		LdapTreeNode root = new LdapTreeNode(baseDN, entries.get(baseDN));
		nodes.put(baseDN, root);
		for (Entry<String, Attributes> entry : entries.entrySet()) {
			if (entry.getKey().equals(baseDN)) {
				continue;
			}
			String nodeName = entry.getKey().substring(0, entry.getKey().indexOf(","));
			LdapTreeNode node = new LdapTreeNode(nodeName, entry.getValue());
			nodes.put(entry.getKey(), node);
			LdapTreeNode parent = nodes.get(entry.getKey().substring(nodeName.length() + 1));
			if (parent != null) {
				parent.addChild(node);
			}
		}

		return nodes;
	}

	public static List<JSONObject> getGroupMembers(JSONObject group) {
		List<JSONObject> members = new ArrayList<JSONObject>();
		JSONArray memberArray = group.optJSONArray("member");
		if (memberArray != null) {
			for (int i = 0; i < memberArray.length(); i++) {
				JSONObject member = new JSONObject();
				try {
					member.put("dn", memberArray.opt(i));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				members.add(member);
			}
		}
		return members;
	}

	public static List<JSONObject> getRoleOccupants(JSONObject role) {
		List<JSONObject> occupants = new ArrayList<JSONObject>();
		JSONArray occupantArray = role.optJSONArray("roleOccupant");
		if (occupantArray != null) {
			for (int i = 0; i < occupantArray.length(); i++) {
				JSONObject occupant = new JSONObject();
				try {
					occupant.put("dn", occupantArray.opt(i));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				occupants.add(occupant);
			}
		}
		return occupants;
	}

	public static List<String> getLdapEntries(String type) {
		List<String> entryList = new ArrayList<String>();

		if (type == null) {
			entryList.addAll(BPMNEditor.getLdapEntries().keySet());
		} else {
			for (Entry<String, Attributes> entry : BPMNEditor.getLdapEntries().entrySet()) {
				Attribute attr = entry.getValue().get("objectclass");
				if (type.equals("person") && (attr.contains("inetOrgPerson") || attr.contains("organizationalPerson") || attr.contains("person"))) {
					entryList.add(entry.getKey());
				} else if (type.equals("group") && attr.contains("groupOfNames")) {
					entryList.add(entry.getKey());
				} else if (type.equals("o") && attr.contains("organization")) {
					entryList.add(entry.getKey());
				} else if (type.equals("role") && attr.contains("organizationalRole")) {
					entryList.add(entry.getKey());
				} else if (type.equals("ou") && attr.contains("organizationalUnit")) {
					entryList.add(entry.getKey());
				}
			}
		}

		Collections.sort(entryList, new Comparator<String>() {
			public int compare(String o1, String o2) {
				int i = o1.split(",").length - o2.split(",").length;
				if (i == 0) {
					return o1.compareTo(o2);
				} else {
					return i;
				}
			}
		});
		return entryList;
	}

	public static void exportLDIF(String filename, BPMNEditor editor, String from) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new FileOutputStream(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			if (from.equals("ldap")) {
				for (Entry<String, Attributes> entry : BPMNEditor.getLdapEntries().entrySet()) {
					writeFormattedString(writer, "dn: ", entry.getKey().toCharArray());
					for (NamingEnumeration<? extends Attribute> ae = entry.getValue().getAll(); ae.hasMoreElements();) {
						Attribute attr = ae.next();
						String attrName = attr.getID();
						Enumeration<?> vals = attr.getAll();
						while (vals.hasMoreElements()) {
							writeFormattedString(writer, attrName + ": ", vals.nextElement().toString().toCharArray());
						}
					}
					writer.println();
				}
			} else {
				writeOrganizationCells(writer, editor.getOrgGraphComponent().getGraph(), "com", null);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}

		writer.flush();
		writer.close();
	}

	public static void writeOrganizationCells(PrintWriter writer, BPMNGraph graph, String parentDN, Object cell) throws IOException {
		String dn = "";
		if (cell == null || cell == graph.getDefaultParent()) {
			Object root = null;
			for (Object v : graph.getChildVertices(graph.getDefaultParent())) {
				if (graph.isOrganizationRoot(v)) {
					root = v;
					break;
				}
			}
			if (root == null) {
				return;
			}
			cell = root;
		}

		Element value = (Element) ((mxCell) cell).getValue();
		if (graph.isOrganizationRoot(cell)) {
			String dc = value.getAttribute("dc");
			if (dc.length() == 0) {
				dc = graph.convertValueToString(cell);
			}
			dn = "dc=" + dc + ",dc=" + parentDN;
		} else if (graph.isOrganization(cell)) {
			dn = "o=" + graph.convertValueToString(cell) + "," + parentDN;
		} else if (graph.isOrganizationalUnit(cell)) {
			dn = "ou=" + graph.convertValueToString(cell) + "," + parentDN;
		} else {
			dn = "cn=" + graph.convertValueToString(cell) + "," + parentDN;
		}
		writeFormattedString(writer, "dn: ", dn.toCharArray());

		NamedNodeMap nodeMap = value.getAttributes();
		for (int i = 0; i < nodeMap.getLength(); i++) {
			Node node = nodeMap.item(i);
			String attrName = node.getNodeName();
			writeFormattedString(writer, attrName + ": ", node.getNodeValue().toCharArray());
		}
		NodeList nodeList = value.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				String attrName = node.getNodeName();
				writeFormattedString(writer, attrName + ": ", node.getAttributes().getNamedItem("name").getNodeValue().toCharArray());
			}
		}
		writer.println();

		for (Object edge : graph.getOutgoingEdges(cell)) {
			Object child = ((mxCell) edge).getTarget();
			writeOrganizationCells(writer, graph, dn, child);
		}
	}

	private static void writeFormattedString(Writer w, String firstline, char p[]) throws IOException {
		int size = 76;
		int len = (size + 1) - firstline.length();
		w.write(firstline);

		if (p.length + firstline.length() <= size) {
			w.write(p);
			w.write(System.getProperty("line.separator"));
			return;
		}

		w.write(p, 0, len);
		w.write(System.getProperty("line.separator"));

		for (int i = len; i < p.length; i += size) {
			w.write(" ");
			if (i + size > p.length) {
				w.write(p, i, p.length - i);
			} else {
				w.write(p, i, size);
			}
			w.write(System.getProperty("line.separator"));
		}
	}

	public static void importLDIF(JSONObject con, String filename) {
		Map<String, Map<String, List<String>>> entries = convertLDIFtoMap(filename);

		DirContext ctx = null;
		try {
			ctx = initLdapConnection(con);
		} catch (NamingException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), mxResources.get("error"), JOptionPane.ERROR_MESSAGE);
		}

		int bindCount = 0;
		int rebindCount = 0;
		int ignoreCount = 0;
		try {
			for (Entry<String, Map<String, List<String>>> entry : entries.entrySet()) {
				String dn = entry.getKey();
				Attributes attrs = new BasicAttributes();
				for (Entry<String, List<String>> attrEntry : entry.getValue().entrySet()) {
					BasicAttribute attr = new BasicAttribute(attrEntry.getKey());
					for (String attrVal : attrEntry.getValue()) {
						attr.add(attrVal);
					}
					attrs.put(attr);
				}

				try {
					ctx.bind(dn, null, attrs);
					bindCount++;
				} catch (NameAlreadyBoundException e) {
					try {
						ctx.rebind(dn, null, attrs);
						rebindCount++;
					} catch (ContextNotEmptyException ne) {
						ignoreCount++;
						continue;
						// List<String> dnList = searchLdapSubtreeDNs(dn, con);
						// Collections.sort(dnList, new Comparator<String>() {
						// public int compare(String o1, String o2) {
						// int i = o2.split(",").length - o1.split(",").length;
						// if (i == 0) {
						// return o2.compareTo(o1);
						// } else {
						// return i;
						// }
						// }
						// });
						// for (String name : dnList) {
						// ctx.unbind(name);
						// }
						// ctx.bind(dn, null, attrs);
					}
				} catch (NamingException e) {
				}
			}
			String message = bindCount + " " + mxResources.get("entriesAdded") + "\n" + rebindCount + " " + mxResources.get("entriesUpdated") + "\n"
					+ ignoreCount + " " + mxResources.get("entriesIgnored");
			JOptionPane.showMessageDialog(null, message, mxResources.get("optionTitle"), JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), mxResources.get("error"), JOptionPane.ERROR_MESSAGE);
		}

	}

	public static Map<String, Map<String, List<String>>> convertLDIFtoMap(String filename) {
		Map<String, Map<String, List<String>>> entries = new TreeMap<String, Map<String, List<String>>>(new Comparator<String>() {
			public int compare(String k1, String k2) {
				int i = k1.split(",").length - k2.split(",").length;
				if (i == 0) {
					return k1.compareTo(k2);
				} else {
					return i;
				}
			}
		});
		String dataline;
		List<String> data = new ArrayList<String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			while ((dataline = in.readLine()) != null) {
				if (dataline.matches("^ .*")) {
					data.set(data.size() - 1, data.get(data.size() - 1) + dataline.substring(1));
				} else {
					data.add(dataline);
				}
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String currentdn = "";
		for (String line : data) {
			Pattern p = Pattern.compile("^(.*?): (.*)");
			Matcher m = p.matcher(line);
			if (m.matches()) {
				String elem1 = m.group(1);
				String elem2 = m.group(2);

				if (elem1.equals("dn")) {
					currentdn = elem2;
					if (!entries.containsKey(currentdn)) {
						entries.put(currentdn, new HashMap<String, List<String>>());
					}
				} else if (!elem1.matches("^version|^changetype|^modify|^add|^replace|^delete")) {
					if (entries.get(currentdn).containsKey(elem1)) {
						List<String> allattravalues = entries.get(currentdn).get(elem1);
						if (!allattravalues.contains(elem2)) {
							entries.get(currentdn).get(elem1).add(elem2);
						}
					} else {
						List<String> allattrvalues = new ArrayList<String>();
						allattrvalues.add(elem2);
						entries.get(currentdn).put(elem1, allattrvalues);
					}
				}
			}
		}
		return entries;
	}

	public static JSONObject importLDIFtoDiagram(BPMNEditor editor, String filename) {
		Map<String, Map<String, List<String>>> entries = LdapUtils.convertLDIFtoMap(filename);
		if (entries.isEmpty()) {
			return null;
		}
		boolean create = true;
		for (JSONObject o : editor.getOrganizations()) {
			if (o.optString("id").equals(entries.keySet().toArray()[0])) {
				create = false;
				break;
			}
		}

		if (!create
				&& JOptionPane.showConfirmDialog(null, "Id: " + entries.keySet().toArray()[0], mxResources.get("overwriteExistingFile"),
						JOptionPane.YES_NO_CANCEL_OPTION) != JOptionPane.YES_OPTION) {
			return null;
		}

		Document doc = mxDomUtils.createDocument();
		BPMNGraph orgGraph = editor.getOrgGraphComponent().getGraph();
		GraphModel orgModel = orgGraph.getModel();
		orgModel.clear();

		String orgId = null;
		JSONObject org = new JSONObject();
		mxCell cell = null;
		for (Entry<String, Map<String, List<String>>> entry : entries.entrySet()) {
			int i = entry.getKey().indexOf(",");
			String parentId = entry.getKey().substring(i + 1);

			cell = new mxCell();
			cell.setId(entry.getKey());
			cell.setGeometry(new mxGeometry(0, 0, 32, 32));
			cell.setVertex(true);

			List<String> objectclass = new ArrayList<String>();
			Element value = doc.createElement("entry");
			for (Entry<String, List<String>> attrEntry : entry.getValue().entrySet()) {
				String attrName = attrEntry.getKey();
				if (attrName.equals("objectClass") || attrName.equals("member") || attrName.equals("roleOccupant")) {
					if (attrName.equals("objectClass")) {
						objectclass = attrEntry.getValue();
					}
					for (String attrVal : attrEntry.getValue()) {
						Element el = doc.createElement(attrName);
						el.setAttribute("name", attrVal);
						value.appendChild(el);
					}
				} else if (attrEntry.getValue().size() == 1) {
					value.setAttribute(attrEntry.getKey(), attrEntry.getValue().get(0));
				}
			}
			cell.setValue(value);

			String style = null;
			if (objectclass.contains("inetOrgPerson") || objectclass.contains("organizationalPerson") || objectclass.contains("person")) {
				style = "organizationalPerson";
			} else if (objectclass.contains("groupOfNames")) {
				style = "groupOfNames";
			} else if (objectclass.contains("organization")) {
				if (orgModel.getCell(parentId) == null) {
					style = "organizationRoot";
				} else {
					style = "organization";
				}
			} else if (objectclass.contains("organizationalRole")) {
				style = "organizationalRole";
			} else {
				style = "organizationalUnit";
			}
			cell.setStyle(style);
			Object rootCell = orgGraph.getDefaultParent();
			orgModel.add(rootCell, cell, 0);
			Object pCell = orgModel.getCell(parentId);
			if (pCell == null) {
				orgId = entry.getKey();
				try {
					org.putOpt("id", orgId);
					org.putOpt("name", orgGraph.convertValueToString(cell));
				} catch (JSONException e) {
					e.printStackTrace();
				}

				mxCell orgCell = new mxCell(org.optString("name"), new mxGeometry(28, 28, 200, 35), "organizationName");
				orgCell.setId("com");
				orgCell.setVertex(true);
				orgCell.setConnectable(false);
				orgModel.add(rootCell, orgCell, 0);

				cell.getGeometry().setX(570);
				cell.getGeometry().setY(60);
			} else {
				mxCell edge = new mxCell(null, new mxGeometry(), "organizationFlow");
				edge.setEdge(true);
				edge.getGeometry().setRelative(true);
				edge.setSource((mxCell) pCell);
				edge.setTarget(cell);
				orgModel.add(rootCell, edge, 0);
			}

		}

		GraphActions.getAutoLayoutAction().actionPerformed(new ActionEvent(editor.getOrgGraphComponent(), 0, ""));
		BPMNEditorUtils.saveOrganization(orgId, orgModel);
		if (create) {
			editor.getOrganizations().add(org);
		}
		editor.setCurrentOrganization(org);
		return org;
	}

	public static void cacheLdap(BPMNEditor editor, boolean create) {
		Document doc = mxDomUtils.createDocument();
		BPMNGraph orgGraph = editor.getOrgGraphComponent().getGraph();
		GraphModel orgModel = orgGraph.getModel();
		orgModel.clear();

		JSONObject org = new JSONObject();
		mxCell cell = null;
		for (Entry<String, Attributes> entry : BPMNEditor.getLdapEntries().entrySet()) {
			try {
				int i = entry.getKey().indexOf(",");
				String parentId = entry.getKey().substring(i + 1);
				cell = new mxCell();
				cell.setId(entry.getKey());
				cell.setGeometry(new mxGeometry(0, 0, 32, 32));
				cell.setVertex(true);

				Element value = doc.createElement("entry");
				for (NamingEnumeration<? extends Attribute> ae = entry.getValue().getAll(); ae.hasMoreElements();) {
					Attribute attr = ae.next();
					String attrName = attr.getID();
					if (attrName.equals("objectClass") || attrName.equals("member") || attrName.equals("roleOccupant")) {
						Enumeration<?> vals = attr.getAll();
						while (vals.hasMoreElements()) {
							Element el = doc.createElement(attrName);
							el.setAttribute("name", vals.nextElement().toString());
							value.appendChild(el);
						}
					} else {
						value.setAttribute(attrName, attr.get().toString());
					}
				}
				cell.setValue(value);

				String style = null;
				Attribute attr = entry.getValue().get("objectclass");
				if (attr.contains("inetOrgPerson") || attr.contains("organizationalPerson") || attr.contains("person")) {
					style = "organizationalPerson";
				} else if (attr.contains("groupOfNames")) {
					style = "groupOfNames";
				} else if (attr.contains("organization")) {
					if (orgModel.getCell(parentId) == null) {
						style = "organizationRoot";
					} else {
						style = "organization";
					}
				} else if (attr.contains("organizationalRole")) {
					style = "organizationalRole";
				} else {
					style = "organizationalUnit";
				}
				cell.setStyle(style);
				Object rootCell = orgGraph.getDefaultParent();
				orgModel.add(rootCell, cell, 0);
				Object pCell = orgModel.getCell(parentId);
				if (pCell == null) {
					org.putOpt("id", entry.getKey());
					org.putOpt("name", orgGraph.convertValueToString(cell));

					mxCell orgCell = new mxCell(org.optString("name"), new mxGeometry(28, 28, 200, 35), "organizationName");
					orgCell.setId("com");
					orgCell.setVertex(true);
					orgCell.setConnectable(false);
					orgModel.add(rootCell, orgCell, 0);

					cell.getGeometry().setX(570);
					cell.getGeometry().setY(60);
				} else {
					mxCell edge = new mxCell(null, new mxGeometry(), "organizationFlow");
					edge.setEdge(true);
					edge.getGeometry().setRelative(true);
					edge.setSource((mxCell) pCell);
					edge.setTarget(cell);
					orgModel.add(rootCell, edge, 0);
				}
			} catch (NamingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		GraphActions.getAutoLayoutAction().actionPerformed(new ActionEvent(editor.getOrgGraphComponent(), 0, ""));
		BPMNEditorUtils.saveOrganization(editor.getLdapPanel().getRootNode().toString(), orgModel);
		if (create) {
			editor.getOrganizations().add(org);
		}
		editor.setCurrentOrganization(org);
	}

}
