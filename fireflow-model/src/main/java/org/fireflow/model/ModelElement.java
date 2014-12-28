/**
 * Copyright 2003-2008,非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.model;

import java.util.List;
import java.util.Map;

/**
 * 工作流元素的抽象接口，工作流元素主要包括:<br/>
 * 1)业务流程 WorkflowProcess，这是顶层元素<br/>
 * 2)任务(Task)<br/>
 * 3)开始节点(StartNode)、结束节点(EndNode)、同步器(Synchronizer)、环节(Activity)<br/>
 * 4)转移(Transition)和循环(Loop)<br/>
 * 5)流程数据项(DataField)<br/>
 * 
 * @author 非也,nychen2000@163.com
 * 
 */
public interface ModelElement {

	public static final String ID_SEPARATOR = ".";
    /**
     * 返回元素的序列号，
     * 业务系统无须关心该序列号。
     * @return 元素序列号
     */
//    public String getSn();


    /**
     * 返回工作流元素的Id
     * 工作流元素的Id采用“父Id.自身Name”的方式组织。
     * @return 元素Id
     */
    public String getId();

    /**
     * 返回工作流元素的名称
     * @return 元素名称
     */
    public String getName();

    
    public void setName(String name);

    /**
     * 返回工作流元素的显示名
     * @return 显示名
     */
    public String getDisplayName();

    public void setDisplayName(String displayName);

    /**
     * 返回流程元素的描述
     * @return 流程元素描述
     */
    public String getDescription();

    public void setDescription(String description);


    /**
     * 返回父元素
     * @return 父元素
     */
    public ModelElement getParent();

    public void setParent(ModelElement parent);
//
//    /**
//     * 返回事件监听器列表
//     * @return 事件监听器列表
//     */
//    public List<EventListener> getEventListeners();
//
    
    public String getExtendedAttribute(String propName);
    /**
     * 返回扩展属性Map
     * @return
     */
    public Map<String, String> getExtendedAttributes();
}
