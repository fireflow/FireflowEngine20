
DROP TABLE IF EXISTS fireflowdemo.T_sys_function;
DROP TABLE IF EXISTS fireflowdemo.T_sys_dictionary;
DROP TABLE IF EXISTS fireflowdemo.T_sys_menu;
DROP TABLE IF EXISTS fireflowdemo.T_sys_config;

CREATE TABLE fireflowdemo.T_sys_config (
       id BIGINT NOT NULL AUTO_INCREMENT
     , config_key VARCHAR(50) NOT NULL
     , config_value VARCHAR(50) NOT NULL
     , description VARCHAR(200)
     , parent_key VARCHAR(50)
     , last_update_person VARCHAR(50) NOT NULL
     , last_update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
     , UNIQUE UQ_T_sys_config_1 ( config_key)
     , PRIMARY KEY (id)
)ENGINE = innoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE fireflowdemo.T_sys_config COMMENT='系统设置表';
ALTER TABLE fireflowdemo.T_sys_config MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT
      COMMENT 'id';
ALTER TABLE fireflowdemo.T_sys_config MODIFY COLUMN config_key VARCHAR(50) NOT NULL
      COMMENT '设置键值，唯一索引';
ALTER TABLE fireflowdemo.T_sys_config MODIFY COLUMN config_value VARCHAR(50) NOT NULL
      COMMENT '设置值';
ALTER TABLE fireflowdemo.T_sys_config MODIFY COLUMN description VARCHAR(200)
      COMMENT '描述';
ALTER TABLE fireflowdemo.T_sys_config MODIFY COLUMN parent_key VARCHAR(50)
      COMMENT '父级键值';
ALTER TABLE fireflowdemo.T_sys_config MODIFY COLUMN last_update_person VARCHAR(50) NOT NULL
      COMMENT '最后修改人';
ALTER TABLE fireflowdemo.T_sys_config MODIFY COLUMN last_update_time TIMESTAMP NOT NULL
      COMMENT '最后修改时间';



CREATE TABLE fireflowdemo.T_sys_dictionary (
       id BIGINT(20) NOT NULL AUTO_INCREMENT
     , dic_type VARCHAR(50) NOT NULL
     , dic_key VARCHAR(200) NOT NULL
     , dic_value VARCHAR(250) NOT NULL
     , dic_description VARCHAR(500)
     , dic_mnemonic VARCHAR(50)
     , sort INT(11)
     , parent_key VARCHAR(200)
     , last_update_person VARCHAR(50) NOT NULL
     , last_update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
     , status TINYINT(4) DEFAULT 1
     , PRIMARY KEY (id)
)ENGINE = innoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
CREATE UNIQUE INDEX UQ_T_sys_dictionary_1 ON fireflowdemo.T_sys_dictionary (dic_type ASC, dic_key ASC);

CREATE TABLE fireflowdemo.T_sys_function (
       id BIGINT NOT NULL AUTO_INCREMENT
     , code VARCHAR(50) NOT NULL
     , name VARCHAR(100) NOT NULL
     , scope VARCHAR(50) NOT NULL
     , parent_code VARCHAR(50) NOT NULL DEFAULT '0'
     , description VARCHAR(500)
     , url VARCHAR(300)
     , sort SMALLINT DEFAULT 0
     , ftype char(10) not null default 'NONE'
     , UNIQUE UQ_T_sys_function_1 (code, scope)
     , PRIMARY KEY (id)
)ENGINE = innoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE fireflowdemo.T_sys_function COMMENT='系统业务功能表';
ALTER TABLE fireflowdemo.T_sys_function MODIFY COLUMN code VARCHAR(50) NOT NULL
      COMMENT '系统功能代码，唯一索引';
ALTER TABLE fireflowdemo.T_sys_function MODIFY COLUMN name VARCHAR(100) NOT NULL
      COMMENT '功能名称';
ALTER TABLE fireflowdemo.T_sys_function MODIFY COLUMN scope VARCHAR(50) NOT NULL
      COMMENT '功能适用范围，';
ALTER TABLE fireflowdemo.T_sys_function MODIFY COLUMN parent_code VARCHAR(50) NOT NULL DEFAULT '0'
      COMMENT '父功能代号';
ALTER TABLE fireflowdemo.T_sys_function MODIFY COLUMN description VARCHAR(500)
      COMMENT '功能描述';
ALTER TABLE fireflowdemo.T_sys_function MODIFY COLUMN url VARCHAR(300)
      COMMENT '功能url，非必录';

ALTER TABLE fireflowdemo.T_sys_function MODIFY COLUMN ftype char(10)
      COMMENT '功能的类别，GROUP=功能组，MENU=导航栏菜单，LINK=一个连接，BUTTON=一个按钮，NONE=未分类';
      
