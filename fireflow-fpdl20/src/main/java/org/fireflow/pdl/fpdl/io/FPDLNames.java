/**
 * Copyright 2007-2011 非也
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
package org.fireflow.pdl.fpdl.io;

import org.dom4j.Namespace;
import org.fireflow.model.io.ModelElementNames;
/**
 * @author 非也
 * @version 2.0
 */
public interface FPDLNames extends ModelElementNames{

    /** Namespace prefix to use for FPDL elements. */
//    String FPDL_NS_PREFIX = "f20";
    String FPDL_NS_PREFIX = "f";//去掉“/20”这个版本号，便于向前兼容
    /** The FPDL20 namespace URI. */
//    String FPDL_NS_URI = "http://www.fireflow.org/schema/workflowprocess/20";
    String FPDL_NS_URI = "http://www.fireflow.org/schema/workflowprocess";//去掉“/20”这个版本号，便于向前兼容

    /** The FPDL schema URI. */
    String FPDL_SCHEMA_LOCATION = FPDL_NS_URI+" "+FPDL_NS_URI+"/20/WorkflowProcessSchema-2.0.xsd";//FPDL_NS_URI+" "+"WorkflowProcessSchema-2.0.xsd"; //
    


    String VERSION = "version";

    String WORKFLOW_PROCESS = "workflow-process";
    
    String IMPORT = "import";
    
    String IMPORT_TYPE = "type";    
    String LOCATION = "location";
    
    String PRIORITY = "priority";

    String DURATION = "duration";    
    String UNIT = "unit";
    String IS_BUSINESS_TIME = "is-business-time";
    
    String START_NODE = "start-node";
    String START_NODES = "start-nodes";
    String END_NODE = "end-node";
    String END_NODES = "end-nodes";

    String ACTIVITIES = "activities";
    String ACTIVITY = "activity";
    String LOOP_STRATEGY = "loop-strategy";

    String ROUTERS = "routers";
    String ROUTER = "router";


    String PROPERTIES = "properties";
    String PROPERTY = "property";
    
    String REF = "ref";

    String INIT_VALUE = "init-value";


    String TRANSITIONS = "transitions";
    String TRANSITION = "transition";
    String FROM = "from";
    String TO = "to";
    String IS_LOOP = "is-loop";
    String IS_DEFAULT = "is-default";

    String CONDITION = "condition";

    String TYPE = "type";


    String NAMESPACE = "namespace";

    

    String FEATURES = "features";
    
    String DEFAULT_ROUTER_FEATURE = "default-router-feature";
    String ANDJOIN_ANDSPLIT_FEATURE = "andjoin-andsplit-feature";
    String XORJOIN_XORSPLIT_FEATURE = "xorjoin-xorsplit-feature";
    String ORJOIN_ORSPLIT_FEATURE = "orjoin-orsplit-feature";
    String CUSTOMIZED_JOIN_SPLIT_FEATURE = "customized-join-split-feature";
    String JOIN_EVALUATOR = "join-evaluator";
    String SPLIT_EVALUATOR = "split-evaluator";
    
    String NORMAL_START_FEATURE = "normal-start-feature";
    String NORMAL_END_FEATURE = "normal-end-feature";
    String THROW_TERMINATION_FEATURE = "throw-termination-feature";
    String TIMER_START_FEATURE = "timer-start-feature";
    String WEBSERVICE_START_FEATURE = "webservice-start-feature";
    
    String ATTACHED_TO_ACTIVITY = "attached-to-activity";
    String IS_CANCEL_ATTACHED_TO_ACTIVITY = "is-cancel-attached-to-activity";
    String TIMER_OPERATION_NAME = "timer-operation-name";
    String CRON_EXPRESSION = "cron";
    String START_TIME_EXPRESSION = "start-time";
    String END_TIME_EXPRESSION = "end-time";
    String REPEAT_INTERVAL_EXPRESSION = "repeat-interval";
    String REPEAT_COUNT_EXPRESSION = "repeat-count";
    
    String CATCH_COMPENSATION_FEATURE = "catch-compensation-feature";
    String COMPENSATION_CODE = "compensation-code";
    String COMPENSATION_CODES = "compensation-codes";
    String ERROR_CODE = "error-code";
    String CATCH_FAULT_FEATURE = "catch-fault-feature";
    
    String THROW_COMPENSATION_FEATURE = "throw-compensation-feature";
    String THROW_FAULT_FEATURE = "throw-fault-feature";
    
    String SERVICE_BINDING = "service-binding";
    String SERVICE_ID = "service-id";
    
    String INPUT_ASSIGNMENTS = "input-assignments";
    String INPUT_ASSIGNMENT = "input-assignment";
    
    String OUTPUT_ASSIGNMENTS = "output-assignments";
    String OUTPUT_ASSIGNMENT = "output-assignment";
    
    String PROP_OVERRIDES = "prop-overrides";
    String PROP_OVERRIDE = "prop-override";
    
    String PROP_GROUP_NAME = "prop-group-name";
    String PROP_NAME = "prop-name";
    
    String RESOURCE_BINDING = "resource-binding";
    
    String ASSIGNMENT_STRATEGY = "assignment-strategy";
    
    String ADMINISTRATORS = "administrators";

    
    String RESOURCE_REF = "resource-ref";
    String RESOURCE_ID = "resource-id";
    
    String PARAMETER_ASSIGNMENTS = "parameter-assignments";
    String PARAMETER_ASSIGNMENT = "parameter-assignment";
    
    String POTENTIAL_OWNERS = "potential-owners";
    String READERS = "readers";
    
    String SUBPROCESSES = "sub-processes";
    String SUBPROCESS = "sub-process";

    String POOL = "pool";
    String IS_ABSTRACT = "is-abstract";
    String MESSAGEFLOW = "messageflow";
    String ASSOCIATION = "association";
    String COMMENT = "comment";
    String LANE = "lane";
    String GROUP = "group";
    
    String WORKITEM_EVENT_LISTENERS = "workitem_event-listeners";
    String EVENT_LISTENERS = "event-listeners";
    String EVENT_LISTENER = "event-listener";

    /////////////////////////////////////////////////////////
    ////    图形元素名称                                            /////////////////////
    /////////////////////////////////////////////////////////
    String NODE = "node";
    String FIGURE = "figure";
    
    String CONNECTOR = "connector";
    
    String DIAGRAMS = "diagrams";
    String DIAGRAM = "diagram";
    //String SUBFLOW_ID = "subflow-id";
    String DIRECTION = "direction";
    String GRID_ENABLED = "grid-enabled";
    String RULER_ENABLED = "ruler-enabled";
    String SNAP_ENABLED = "snap-enabled";
    
    //String PLANE = "plane";
    String LABEL = "label";
    String CONTENT = "content";
    
    String BOUNDS = "bounds";
    String X = "x";
    String Y = "y";
//    String UPPER_LEFT_CORNER = "upper-left-corner";
    String WIDTH = "width";
    String HEIGHT = "height";
    String BORDER_STYLE = "border-style";
    
    String LINE_TYPE = "line-type";
    String DASH_ARRAY = "dash-array";
    String THICK = "thick";
    //String SPACE = "space";
//    String CORNER_TYPE = "corner-type";
    String LABEL_POSITION = "titleLabel-position";
    String TEXT_DIRECTION = "text-direction";
    
    String RADIUS = "radius";
    
    String FULFIL_STYLE = "fulfil-style";
    
    String RECTANGLE = "rectangle";
    String TITLE = "title";
    
    String CIRCLE = "circle";
    String THE_CENTER = "the-center";
    
    String LINE = "line";

    
    String POINT_LIST = "point-list";

    String COLOR = "color";
    String SIZE = "size";
    String FONT_STYLE = "font-style";
    String FONT_NAME = "font-name";
    String GRADIENT_STYLE = "gradient-style";
    
    

    Namespace XSD_NS = new Namespace(XSD_NS_PREFIX, XSD_URI);
    Namespace XSI_NS = new Namespace(XSI_NS_PREFIX, XSI_URI);
    Namespace FPDL_NS = new Namespace(FPDL_NS_PREFIX, FPDL_NS_URI);

}
