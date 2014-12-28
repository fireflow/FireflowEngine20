/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License v3 as published by the Free Software
 * Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, see http://www.gnu.org/licenses/lgpl.html.
 *
 */
package org.fireflow.console.misc;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public class TreeNode {
	private String treeId = null;
	private String name = null;
	private String description = null;
	private String actionUrl = null;
	private boolean isLeaf = false;

	private List<TreeNode> children = new ArrayList<TreeNode>();

	public List<TreeNode> getChildren() {
		return children;
	}

	public void addChild(TreeNode child) {
		children.add(child);
	}

	public String getTreeId() {
		return treeId;
	}

	public void setTreeId(String treeId) {
		this.treeId = treeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getActionUrl() {
		return actionUrl;
	}

	public void setActionUrl(String actionUrl) {
		this.actionUrl = actionUrl;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean b) {
		isLeaf = b;
	}

	public String toHtml(String imgPath,String contextPath, String targetWinName) {
		StringBuffer buffer = new StringBuffer();

		if (isLeaf) {
			buffer.append("<ul><li class=\"")
			.append("Child")
			.append("\">")
			.append("<img class=\"s\" src=\"")
			.append(imgPath)
			.append("/s.gif")
			.append("\" />")
			.append("<a href=\"").append(contextPath).append(this.actionUrl).append("\" target=\"").append(targetWinName).append("\">")
			.append(this.getName()).append("</a>");
			buffer.append("</ul>");
		} else {
			buffer.append("<ul><li class=\"")
					.append("Closed")
					.append("\">")
					.append("<img class=\"s\" src=\"")
					.append(imgPath)
					.append("/s.gif")
					.append("\" onclick=\"javascript:ChangeStatus(this);\" />")
					.append("<a href=\"#\" onclick=\"javascript:ChangeStatus(this);\">")
					.append(this.getName()).append("</a>");

			if (this.children.size() > 0) {
				for (TreeNode node : children) {
					buffer.append(node.toHtml(imgPath,contextPath, targetWinName)
							.toString());
				}
			}

			buffer.append("</ul>");
		}

		return buffer.toString();
	}

}
