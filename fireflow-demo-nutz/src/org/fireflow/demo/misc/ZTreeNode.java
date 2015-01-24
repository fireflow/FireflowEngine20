package org.fireflow.demo.misc;

import java.util.ArrayList;
import java.util.List;

/**
 * ZTree 的node对象，用于构造Ztree
 * @author 非也，20140803
 *
 */
public class ZTreeNode {
	public static final String NODE_TYPE_ORGANIZATION = "O";
	public static final String NODE_TYPE_GROUP = "G";
	public static final String NODE_TYPE_ROLE = "R";
	public static final String NODE_TYPE_USER = "U";
	public static final String NODE_TYPE_SUBJECT = "S";
	/**
	 * 节点Id，可能是组织机构code，也可能是group code,user loginname ,role code ,subject code
	 */
	String id = null;//
	
	/**
	 * 节点的类型，O=organization,G=group,U=user,R=role,S=subject
	 */
	String nodeType = null;//
	String name = null;
	boolean checked = false;
	boolean chkDisabled = false;
	
	String icon = null;
	String iconClose = null;
	String iconOpen = null;
	
	boolean isParent = true;
	
	boolean nocheck = false;
	
	boolean open = false;
	
	String parentId = "";
	
	List<ZTreeNode> children = new ArrayList<ZTreeNode>();
	
	//自定义的数据项
//	String orgCode = null;
//	String orgName = null;
//	String groupCode = null;
//	String groupName = null;
	
	
	String url = null;
	String target = null;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public boolean isChkDisabled() {
		return chkDisabled;
	}
	public void setChkDisabled(boolean chkDisabled) {
		this.chkDisabled = chkDisabled;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getIconClose() {
		return iconClose;
	}
	public void setIconClose(String iconClose) {
		this.iconClose = iconClose;
	}
	public String getIconOpen() {
		return iconOpen;
	}
	public void setIconOpen(String iconOpen) {
		this.iconOpen = iconOpen;
	}
	public boolean isParent() {
		return isParent;
	}
	public void setParent(boolean isParent) {
		this.isParent = isParent;
	}
	public boolean isNocheck() {
		return nocheck;
	}
	public void setNocheck(boolean nocheck) {
		this.nocheck = nocheck;
	}
	public boolean isOpen() {
		return open;
	}
	public void setOpen(boolean open) {
		this.open = open;
	}
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public List<ZTreeNode> getChildren() {
		return children;
	}
	public void setChildren(List<ZTreeNode> children) {
		this.children = children;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentCode) {
		this.parentId = parentCode;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((parentId == null) ? 0 : parentId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ZTreeNode other = (ZTreeNode) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;
		return true;
	}
	
	
}
