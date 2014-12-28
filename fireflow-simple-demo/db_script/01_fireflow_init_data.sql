insert into t_ff_cfg_fireflow_config(id,config_Id,config_name,config_value,description,category_id,parent_config_Id,last_editor)
 values('00000000','ALL_CONFIG_CATEGORIES','所有的配置参数','ALL CONFIG CATEGORIES','','','','system_initiator');
 
insert into t_ff_cfg_fireflow_config(id,config_Id,config_name,config_value,description,category_id,parent_config_Id,last_editor)
 values('00000100','BIZ_TYPE','业务类别','BUSINESS TYPE','','ALL_CONFIG_CATEGORIES','','system_initiator');
insert into t_ff_cfg_fireflow_config(id,config_Id,config_name,config_value,description,category_id,parent_config_Id,last_editor)
 values('00000101','ALL_BIZ','所有业务','ALL BIZ','','BIZ_TYPE','','system_initiator');
insert into t_ff_cfg_fireflow_config(id,config_Id,config_name,config_value,description,category_id,parent_config_Id,last_editor)
 values('00000102','FIREFLOW_DEMO','FIREFLOW应用Demo','FIREFLOW DEMO','','BIZ_TYPE','ALL_BIZ','system_initiator');
insert into t_ff_cfg_fireflow_config(id,config_Id,config_name,config_value,description,category_id,parent_config_Id,last_editor)
 values('00000103','FIREFLOW_EXAMPLE','FIREFLOW流程示例','FIREFLOW EXAMPLE','','BIZ_TYPE','ALL_BIZ','system_initiator'); 
    