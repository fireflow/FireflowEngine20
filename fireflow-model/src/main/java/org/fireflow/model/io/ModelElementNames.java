/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.model.io;


/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public interface ModelElementNames {
    /** Namespace prefix to use for XSD elements. */
    String XSD_NS_PREFIX = "xsd";
    
    /** The XSD namespace URI. */
    String XSD_URI = "http://www.w3.org/2001/XMLSchema";
    
    /** Namespace prefix to use for XSD elements. */
    String XSI_NS_PREFIX = "xsi";
    /** The XSD namespace URI. */
    String XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";
    
    String XSI_SCHEMA_LOCATION = "schemaLocation";
    
    /** Namespace prefix for services  */
    String SERVICE_NS_PREFIX = "s";
    
    /** Namespace prefix for resources */
    String RESOURCE_NS_PREFIX = "r";
    
    /** The service namespace URI */
    //String SERVICE_NS_URI = "http://www.fireflow.org/schema/service/20";
    String SERVICE_NS_URI = "http://www.fireflow.org/schema/service";//去掉“/20”这个版本号，便于向前兼容
    
    /** The resource URI */
    //String RESOURCE_NS_URI = "http://www.fireflow.org/schema/resource/20";
    String RESOURCE_NS_URI = "http://www.fireflow.org/schema/resource";//去掉“/20”这个版本号，便于向前兼容
    
    String SERVICE_SCHEMA_LOCATION = SERVICE_NS_URI+" "+SERVICE_NS_URI+"/20/ServiceSchema-2.0.xsd";//SERVICE_NS_URI+" "+"ServiceSchema-2.0.xsd";//
    
    String RESOURCE_SCHEMA_LOCATION = RESOURCE_NS_URI+" "+RESOURCE_NS_URI+"/20/ResourceSchema-2.0.xsd";//RESOURCE_NS_URI+" "+"ResourceSchema-2.0.xsd";//
    
    //******************************************//
    //***********   公共                 *****************//
    //*****************************************//
    /** Unique identifier. */
    String ID = "id";

    /** Entity name. */
    String NAME = "name";

    /** Tag which defines a brief description of an element. */
    String DESCRIPTION = "description";
    
    String DISPLAY_NAME="display-name";
    
    String VALUE = "value";
    String PACKAGE_ID = "package-id";
    String DATA_PATTERN = "data-pattern";
    
    String EXPRESSION = "expression";
    
    
    String DEFAULT_VALUE = "default-value";
    
    String BEAN_NAME = "bean-name";
    String CLASS_NAME = "class-name";
    
    String EXTENDED_ATTRIBUTES = "extended-attributes";
    String EXTENDED_ATTRIBUTE = "extended-attribute";
    
    String TARGET_NAMESPACE = "target-namespace";
    
    String NAMESPACE_PREFIX_URI_MAP = "namespace_prefix_uri_map";
    
    String ENTRY = "entry";
    //******************************************//
    //*********** Service 相关  *****************//
    //*****************************************//
    String SERVICES = "services";
    String SERVICE = "service";
    String INTERFACE_COMMON = "interface.common";
    String PARSER_CLASS_NAME = "parser-class";
    String INVOKER_BEAN_NAME = "invoker-bean";
    String INVOKER_CLASS_NAME = "invoker-class";
    
    String EXECUTOR_NAME = "executor-name";
    
    String SERVICE_TYPE = "service-type";
    
    String OPERATIONS = "operations";
    
    String OPERATION = "operation";
    
    String OPERATION_NAME = "operation-name";
    
    String IO_SPECIFICATION = "io-specification";
    
    String INPUTS = "inputs";
    
    String INPUT = "input";
    
    String OUTPUTS = "outputs";
    
    String OUTPUT = "output";

//    String PROP_GROUPS = "prop-groups";
//    
//    String PROP_GROUP = "prop-group";
//    
//    String PROP = "prop";

    //******************************************//
    //*********** Resource 相关  *****************//
    //*****************************************//
    String RESOURCES = "resources";
    String RESOURCE = "resource";
    String RESOURCE_TYPE = "resource-type";
    String RESOLVER = "resolver";
    
    String PARAMETERS = "parameters";
    String PARAMETER = "parameter";

    String LANGUAGE = "language";
    String BODY = "body";
    
    String DATA_TYPE = "data-type";
    
//    Namespace SERVICE_NS = new Namespace(SERVICE_NS_PREFIX, SERVICE_NS_URI);
//    Namespace RESOURCE_NS = new Namespace(RESOURCE_NS_PREFIX, RESOURCE_NS_URI);
}
