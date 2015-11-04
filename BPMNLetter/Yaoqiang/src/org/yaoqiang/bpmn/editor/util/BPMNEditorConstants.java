package org.yaoqiang.bpmn.editor.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import org.yaoqiang.graph.util.Constants;

public class BPMNEditorConstants {

	/**
	 * YAOQIANG_FRAGMENTS_DIR
	 */
	public static final String YAOQIANG_FRAGMENTS_DIR = "fragments";

	/**
	 * YAOQIANG_ADDONS_DIR
	 */
	public static final String YAOQIANG_ADDONS_DIR = "add-ons";

	/**
	 * YAOQIANG_ORGS_DIR
	 */
	public static final String YAOQIANG_ORGS_DIR = "organizations";

	/**
	 * YAOQIANG_DICTIONARY_DIR
	 */
	public static final String YAOQIANG_DICTIONARY_DIR = "dictionaries";

	/**
	 * YAOQIANG_CONNECTION_FILE
	 */
	public static final String YAOQIANG_CONNECTION_FILE = "connection.properties";

	/**
	 * YAOQIANG_LDAP_CONNECTION_FILE
	 */
	public static final String YAOQIANG_LDAP_CONNECTION_FILE = "ldap_connection.properties";

	/**
	 * YAOQIANG_USER_FILE
	 */
	public static final String YAOQIANG_USER_FILE = "user.properties";

	/**
	 * RESOURCES_URI
	 */
	public static final String RESOURCES_URI = "org/yaoqiang/bpmn/editor/resources/editor";

	/**
	 * ROLE_ICON
	 */
	public static ImageIcon ROLE_ICON = new ImageIcon(Constants.class.getResource("/org/yaoqiang/bpmn/editor/images/marker_role.gif"));

	/**
	 * GROUP_ICON
	 */
	public static ImageIcon GROUP_ICON = new ImageIcon(Constants.class.getResource("/org/yaoqiang/bpmn/editor/images/marker_group.png"));

	/**
	 * LDAP_ROOT_ICON
	 */
	public static ImageIcon LDAP_ROOT_ICON = new ImageIcon(Constants.class.getResource("/org/yaoqiang/bpmn/editor/images/marker_ldaproot.png"));

	/**
	 * COMPANY_ICON
	 */
	public static ImageIcon COMPANY_ICON = new ImageIcon(Constants.class.getResource("/org/yaoqiang/bpmn/editor/images/marker_company.png"));

	/**
	 * OU_ICON
	 */
	public static ImageIcon OU_ICON = new ImageIcon(Constants.class.getResource("/org/yaoqiang/bpmn/editor/images/marker_organization.png"));

	/**
	 * USER_ICON
	 */
	public static ImageIcon USER_ICON = new ImageIcon(Constants.class.getResource("/org/yaoqiang/bpmn/editor/images/marker_user.png"));

	/**
	 * DB_INIT_SCRIPT
	 */
	public static final String DB_INIT_SCRIPT = "INIT=RUNSCRIPT FROM 'classpath:org/yaoqiang/bpmn/editor/resources/db/init.sql'";

	/**
	 * DB_URL
	 */
	public static final String DB_URL = "jdbc:h2:~/.yaoqiang/db/data;AUTO_SERVER=TRUE;";

	/**
	 * ACTIVITI_CONNECTION
	 */
	public static Map<String, Object> ACTIVITI_CONNECTION = new HashMap<String, Object>();

	/**
	 * LDAP_CONNECTION
	 */
	public static Map<String, Object> LDAP_CONNECTION = new HashMap<String, Object>();

	/**
	 * USER_TEMPLATE
	 */
	public static Map<String, Object> USER_TEMPLATE = new HashMap<String, Object>();

	static {
		ACTIVITI_CONNECTION.put("id", "");
		ACTIVITI_CONNECTION.put("name", "New Connection");
		ACTIVITI_CONNECTION.put("vendor", "Activiti");
		ACTIVITI_CONNECTION.put("host", "localhost");
		ACTIVITI_CONNECTION.put("port", "8080");
		ACTIVITI_CONNECTION.put("baseURL", "/activiti-rest/service");
		ACTIVITI_CONNECTION.put("useSecurityCredentials", Boolean.TRUE);
		ACTIVITI_CONNECTION.put("username", "kermit");
		ACTIVITI_CONNECTION.put("password", "kermit");
		ACTIVITI_CONNECTION.put("saveSecurityCredentials", Boolean.TRUE);
		
		LDAP_CONNECTION.put("id", "");
		LDAP_CONNECTION.put("name", "New LDAP Connection");
		LDAP_CONNECTION.put("host", "localhost");
		LDAP_CONNECTION.put("port", "389");
		LDAP_CONNECTION.put("protocol", "LDAP v3");
		LDAP_CONNECTION.put("baseDN", "dc=my-domain,dc=com");
		
		USER_TEMPLATE.put("username", "username");
		USER_TEMPLATE.put("fullname", "Full Name");
		USER_TEMPLATE.put("memberOf", new ArrayList<String>());
	}
}
