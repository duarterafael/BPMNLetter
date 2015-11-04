package org.yaoqiang.bpmn.editor.addon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.yaoqiang.bpmn.editor.BPMNEditor;
import org.yaoqiang.bpmn.editor.swing.BaseEditor;
import org.yaoqiang.bpmn.editor.util.BPMNEditorConstants;
import org.yaoqiang.graph.util.Constants;

/**
 * AddonsManager
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class AddonsManager {

	protected Map<String, Addon> addons = new HashMap<String, Addon>();

	protected BPMNEditor bpmnEditor;

	public AddonsManager(BPMNEditor bpmnEditor) {
		this.bpmnEditor = bpmnEditor;
	}

	public BPMNEditor getBPMNEditor() {
		return bpmnEditor;
	}

	public void initAddons() {
		Addon addon = null;
		boolean hasAddon = false;
		try {
			ClassLoader classloader = discoverAddonJars();
			Enumeration<URL> resources = classloader.getResources("META-INF/services/yaoqiang.addons");

			while (resources.hasMoreElements()) {
				hasAddon = true;
				URL resource = resources.nextElement();
				String addonClass = new BufferedReader(new InputStreamReader(resource.openStream(), "UTF-8")).readLine().trim();
				if (addonClass.startsWith("org.yaoqiang.bpmn.addon.")) {
					JOptionPane.showMessageDialog(null, "Please download the latest add-ons, then restart the editor.");
					continue;
				}
				Class<?> clazz = classloader.loadClass(addonClass);
				if (Addon.class.isAssignableFrom(clazz)) {
					addon = (Addon) clazz.newInstance();
					if (addon.getVersion().equals("5.7.v20110925") || addon.getVersion().equals("5.8.v20111020") || addon.getVersion().equals("5.8.v20111027")) {
						JOptionPane.showMessageDialog(null, "Please download the latest add-ons, then restart the editor.");
						continue;
					}
					addon.setAddonsManager(this);
					addons.put(addon.getName(), addon);
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (addon == null && hasAddon) {
				if (BaseEditor.splash != null) {
					BaseEditor.splash.dispose();
					BaseEditor.splash = null;
				}
				JOptionPane.showMessageDialog(null, "Please download the latest add-ons, then restart the editor.");
				System.exit(0);
			}
		}

	}

	public List<String> getAddonInfos() {
		List<String> names = new ArrayList<String>();
		for (String name : addons.keySet()) {
			names.add(name + " (" + getAddon(name).getVersion() + ")");
		}
		Collections.sort(names);
		return names;
	}

	public Collection<Addon> getAddons() {
		return addons.values();
	}

	public Addon getAddon(String name) {
		return addons.get(name);
	}

	private ClassLoader discoverAddonJars() {

		List<URL> list = new ArrayList<URL>();

		File d = new File(Constants.YAOQIANG_USER_HOME + File.separator + BPMNEditorConstants.YAOQIANG_ADDONS_DIR);
		if (d.isDirectory()) {
			File[] f = d.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(".jar") && pathname.isFile();
				}

			});
			for (int i = 0; i < f.length; i++) {
				try {
					list.add(f[i].toURI().toURL());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}

		URL[] urls = list.toArray(new URL[list.size()]);
		return new URLClassLoader(urls, AddonsManager.class.getClassLoader());
	}

}
