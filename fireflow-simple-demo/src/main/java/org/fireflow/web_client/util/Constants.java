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
package org.fireflow.web_client.util;


/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class Constants {
	/**
	 * 从request中获得流程操作错误信息的Key
	 */
	public static final String ERROR_MESSAGE = "org.fireflow.web.constants.ERROR_MESSAGE";
	/**
	 * 从request中获得流程操作错误栈的key
	 */
	public static final String ERROR_STACK = "org.fireflow.web.constants.ERROR_STACK";
	/**
	 * 流程工具栏的输入参数之一：业务表单Id
	 */
	public static final String BIZ_FORM_ID = "org.fireflow.web.toolbar.bizFormId";
	/**
	 * 流程工具栏输入参数之二：流程操作成功之后的跳转页面，如果该参数没有设置，则自动转向到“我的待办”页面
	 */
	public static final String FORWARD_URL = "org.fireflow.web.toolbar.forwardURL";
	
	//流程操作相关参数的Key	
	public static final String WORKITEM_ID = "workItemId";
	public static final String BIZ_ID = "bizId";
	public static final String WORKITEM_STATE = "workItemState";
	public static final String PROCESS_ID = "processId";
	public static final String FIRST_ACTIVITY_ID = "firstActivityId";
	public static final String TARGET_ACTIVITY_ID = "targetActivityId";
	public static final String PROCESS_INSTANCE_ID = "processInstanceId";
	public static final String ACTIVITY_INSTANCE_ID = "activityInstanceId";
	
	//所有的流程操作Action
	public static final String ACTION_TYPE = "workflowActionType";
	public static final String CREATE_PROCESS_INSTANCE = "CREATE_PROCESS_INSTANCE";//已经实现
	public static final String OPEN_BIZ_FORM = "OPEN_BIZ_FORM";//从待办、已办列表打开业务表单
	public static final String CLAIM_WORKITEM = "CLAIM_WORKITEM";//签收工作项
	public static final String DISCLAIM_WORKITEM = "DISCLAIM_WORKITEM";//退签收工作项
	public static final String COMPLETE_WORKITEM = "COMPLETE_WORKITEM";//已经实现
	public static final String REASSIGN_WORKITEM = "REASSIGN_WORKITEM";//加签，已经实现
	public static final String COMPLETE_WORKITEM_AND_JUMP_TO = "COMPLETE_WORKITEM_AND_JUMP_TO";//跳转到
	public static final String OPEN_NEXT_STEP_ACTOR_SELECTOR = "OPEN_NEXT_STEP_ACTOR_SELECTOR";//打开选择下一步处理人界面
	public static final String OPEN_TARGET_ACTIVITY_SELECTOR = "OPEN_TARGET_ACTIVITY_SELECTOR";//打开选择下一步activity的界面
	public static final String OPEN_REASSIGN_ACTOR_SELECTOR = "OPEN_REASSIGN_ACTOR_SELECTOR";//打开加签功能的操作员选择界面

	public static final String LIST_TODO_WORKITEMS = "LIST_TODO_WORKITEMS";//已经实现
	public static final String LIST_HAVEDONE_WORKITEMS = "LIST_HAVEDONE_WORKITEMS";//已经实现
	public static final String LIST_READONLY_WORKITEMS = "LIST_READONLY_WORKITEMS";//已经实现
	public static final String LIST_WORKITEMS_IN_PROCESS_INSTANCE = "LIST_WORKITEMS_IN_PROCESS_INSTANCE";
	public static final String LIST_MY_ACTIVE_PROCESS_INSTANCE = "LIST_MY_ACTIVE_PROCESS_INSTANCE";//已经实现
	
	public static final String LOAD_OU_AS_JSTREE_XML = "LOAD_OU_AS_JSTREE_XML";//根据父级组织机构Id获得直接下级组织机构和人员的数据，以jstree xml形态返回
//	public static final String HAS_CUSTOMIZED_ACTORS = "HAS_CUSTOMIZED_ACTORS";//标示COMPLETE_WORKITEM请求中是否有指定的下一步操作者
	
	public static final String NEXT_ACTOR_INPUT_NAME_PREFIX = "actor_4_";//界面上的下一环节操作者的input框的name的前缀
	
	
	public Constants(){

	}
	
	public String getERROR_MESSAGE(){
		return ERROR_MESSAGE;
	}
	public String getERROR_STACK(){
		return ERROR_STACK;
	}
	
	public String getBIZ_FORM_ID(){
		return BIZ_FORM_ID;
	}
	
	public String getFORWARD_URL(){
		return FORWARD_URL;
	}
	
	//流程操作相关参数的Key	
	public String getWORKITEM_ID(){
		return WORKITEM_ID;
	}	
	
	public String getBIZ_ID(){
		return BIZ_ID;
	}
	
	public String getWORKITEM_STATE(){
		return WORKITEM_STATE;
	}		
	
	public String getPROCESS_ID(){
		return PROCESS_ID;
	}	
	public String getFIRST_ACTIVITY_ID(){
		return FIRST_ACTIVITY_ID;
	}	
	public String getTARGET_ACTIVITY_ID(){
		return TARGET_ACTIVITY_ID;
	}	
	public String getPROCESS_INSTANCE_ID(){
		return PROCESS_INSTANCE_ID;
	}	
	public String getACTIVITY_INSTANCE_ID(){
		return ACTIVITY_INSTANCE_ID;
	}	
	//所有的流程操作Action
	public String getACTION_TYPE(){
		return ACTION_TYPE;
	}	
	public String getCREATE_PROCESS_INSTANCE(){
		return CREATE_PROCESS_INSTANCE;
	}	
	public String getOPEN_BIZ_FORM(){
		return OPEN_BIZ_FORM;
	}		
	
	public String getCLAIM_WORKITEM(){
		return CLAIM_WORKITEM;
	}
	
	public String getDISCLAIM_WORKITEM(){
		return DISCLAIM_WORKITEM;
	}
	public String getCOMPLETE_WORKITEM(){
		return COMPLETE_WORKITEM;
	}	
	
	public String getOPEN_NEXT_STEP_ACTOR_SELECTOR(){
		return OPEN_NEXT_STEP_ACTOR_SELECTOR;
	}		
	public String getOPEN_TARGET_ACTIVITY_SELECTOR(){
		return OPEN_TARGET_ACTIVITY_SELECTOR;
	}		
	
	public String getREASSIGN_WORKITEM(){
		return REASSIGN_WORKITEM;
	}
//	public String getPREPARE_REASSIGN_TO_BEFORE_ME(){
//		return PREPARE_REASSIGN_TO_BEFORE_ME;
//	}	
//	public String getREASSIGN_TO_BEFORE_ME(){
//		return REASSIGN_TO_BEFORE_ME;
//	}	
//	public String getPREPARE_REASSIGN_TO_AFTER_ME(){
//		return PREPARE_REASSIGN_TO_AFTER_ME;
//	}		
//	public String getREASSIGN_TO_AFTER_ME(){
//		return REASSIGN_TO_AFTER_ME;
//	}	
	
	public String getLIST_TODO_WORKITEMS(){
		return LIST_TODO_WORKITEMS;
	}	
	
	public String getLIST_HAVEDONE_WORKITEMS(){
		return LIST_HAVEDONE_WORKITEMS;
	}	
	
	public String getLIST_READONLY_WORKITEMS(){
		return LIST_READONLY_WORKITEMS;
	}	
	
	public String getLIST_WORKITEMS_IN_PROCESS_INSTANCE(){
		return LIST_WORKITEMS_IN_PROCESS_INSTANCE;
	}
	
	public String getLIST_MY_ACTIVE_PROCESS_INSTANCE(){
		return LIST_MY_ACTIVE_PROCESS_INSTANCE;
	}
	
	public String getCOMPLETE_WORKITEM_AND_JUMP_TO(){
		return COMPLETE_WORKITEM_AND_JUMP_TO;
	}
//	public String getHAS_CUSTOMIZED_ACTORS(){
//		return HAS_CUSTOMIZED_ACTORS;
//	}
	
	public String getNEXT_ACTOR_INPUT_NAME_PREFIX(){
		return NEXT_ACTOR_INPUT_NAME_PREFIX;
	}
}
