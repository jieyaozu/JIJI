package com.jiji.iready.httpmanager;

import java.util.ArrayList;

/**
 * 请求网络参数类
 * @author wangfang
 *
 */
public class ParamList extends ArrayList<ParamList.Parameter> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static class Parameter {

		private String name;
		private Object value;

		public Parameter(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

	}
	
	/**
	 * 拼接请求参数串
	 *            请求参数
	 * @return
	 */
	public String getUrlEncode() {
		StringBuilder sbuilder = new StringBuilder();
		for (int i = 0; i < size(); i++) {
			Parameter param = get(i);
			if (sbuilder.length() > 0) {
				sbuilder.append("&");
			}
			if (param.getValue() != null) {
				sbuilder.append(param.getName() + "="
						+ param.getValue().toString());
			} else {
				sbuilder.append(param.getName() + "=");
			}
		}
		return "?" + sbuilder.toString();
	}
}
