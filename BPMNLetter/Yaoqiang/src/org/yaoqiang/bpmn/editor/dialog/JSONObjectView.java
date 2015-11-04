package org.yaoqiang.bpmn.editor.dialog;

import org.json.JSONObject;

/**
 * JSONObjectView
 * 
 * @author Shi Yaoqiang(shi_yaoqiang@yahoo.com)
 */
public class JSONObjectView {

	protected JSONObject object;

	protected String objectString = "";

	public JSONObjectView(JSONObject obj) {
		this.object = obj;
	}

	public JSONObjectView(String el) {
		this.objectString = el;
	}

	public JSONObject getJSONObject() {
		return object;
	}

	public void setJSONObject(JSONObject obj) {
		this.object = obj;
	}

	public String getJSONObjectString() {
		return this.objectString;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof JSONObjectView))
			return false;
		if (getJSONObject() != null) {
			JSONObject o = getJSONObject();
			JSONObject o2 = (((JSONObjectView) obj).getJSONObject());
			if (o2 == null) {
				return false;
			} else if (o == o2) {
				return true;
			} else {
				return o.optString("id").equals(o2.optString("id"));
			}
		} else if (getJSONObjectString() != null) {
			return getJSONObjectString().equals(((JSONObjectView) obj).getJSONObjectString());
		}
		return false;
	}

	public String toString() {
		if (object != null) {
			return object.optString("name");
		} else {
			return objectString;
		}
	}
}