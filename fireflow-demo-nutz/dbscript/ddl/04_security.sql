DROP TABLE IF EXISTS fireflowdemo.T_security_permissions;
DROP TABLE IF EXISTS fireflowdemo.T_security_user;
DROP TABLE IF EXISTS fireflowdemo.T_security_group;
DROP TABLE IF EXISTS fireflowdemo.T_security_organization;
DROP TABLE IF EXISTS fireflowdemo.T_security_user_role;
DROP TABLE IF EXISTS fireflowdemo.T_security_role_belongs;
DROP TABLE IF EXISTS fireflowdemo.T_security_role;
DROP TABLE IF EXISTS fireflowdemo.T_hr_organization;


CREATE TABLE fireflowdemo.T_hr_organization (
       id BIGINT NOT NULL AUTO_INCREMENT
     , code VARCHAR(50) NOT NULL
     , name VARCHAR(100) NOT NULL
     , parent_code VARCHAR(50) DEFAULT '0'
     , forShort VARCHAR(20) NOT NULL
     , type VARCHAR(5) NOT NULL
     , last_update_person VARCHAR(50) NOT NULL
     , last_update_time TIMESTAMP NOT NULL
     , UNIQUE UQ_T_security_organization_1 (code)
     , PRIMARY KEY (id)
)ENGINE = innoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE fireflowdemo.T_hr_organization COMMENT='组织机构';
ALTER TABLE fireflowdemo.T_hr_organization MODIFY COLUMN code VARCHAR(50) NOT NULL
      COMMENT '代码';
ALTER TABLE fireflowdemo.T_hr_organization MODIFY COLUMN name VARCHAR(100) NOT NULL
      COMMENT '名称';
ALTER TABLE fireflowdemo.T_hr_organization MODIFY COLUMN parent_code VARCHAR(50) DEFAULT '0'
      COMMENT '父代码';
ALTER TABLE fireflowdemo.T_hr_organization MODIFY COLUMN forShort VARCHAR(20) NOT NULL
      COMMENT '汉语简称';
ALTER TABLE fireflowdemo.T_hr_organization MODIFY COLUMN type VARCHAR(5) NOT NULL
      COMMENT '类型，og=集团（即organization group），o=公司（即organization），ou=部门（即organization unit）';
ALTER TABLE fireflowdemo.T_hr_organization MODIFY COLUMN last_update_person VARCHAR(50) NOT NULL
      COMMENT '最后修改人';
ALTER TABLE fireflowdemo.T_hr_organization MODIFY COLUMN last_update_time TIMESTAMP NOT NULL
      COMMENT '最后修改时间';

CREATE TABLE fireflowdemo.T_security_group (
       id BIGINT NOT NULL AUTO_INCREMENT
     , code VARCHAR(50) NOT NULL
     , name VARCHAR(100)
     , parent_code VARCHAR(50)
     , org_code VARCHAR(50)
     , org_name CHAR(50)
     , last_update_person VARCHAR(50) NOT NULL
     , last_update_time TIMESTAMP NOT NULL
     , UNIQUE UQ_T_security_group_1 (code)
     , PRIMARY KEY (id)
)ENGINE = innoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE fireflowdemo.T_security_group MODIFY COLUMN last_update_person VARCHAR(50) NOT NULL
      COMMENT '最后修改人';
ALTER TABLE fireflowdemo.T_security_group MODIFY COLUMN last_update_time TIMESTAMP NOT NULL
      COMMENT '最后修改时间';
      
CREATE TABLE fireflowdemo.T_security_permissions (
       id BIGINT NOT NULL AUTO_INCREMENT
     , grantee_code VARCHAR(50) NOT NULL
     , grantee_name VARCHAR(50)
     , grantee_type CHAR(10) NOT NULL
     , function_code VARCHAR(50) NOT NULL
     , function_name VARCHAR(100)
     , last_update_person VARCHAR(50) NOT NULL
     , last_update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
     , UNIQUE UQ_T_security_permissions_1 (grantee_code, function_code)
     , PRIMARY KEY (id)
)ENGINE = innoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE fireflowdemo.T_security_permissions COMMENT='授权表';
ALTER TABLE fireflowdemo.T_security_permissions MODIFY COLUMN grantee_code VARCHAR(50) NOT NULL
      COMMENT '权限所有者code';
ALTER TABLE fireflowdemo.T_security_permissions MODIFY COLUMN grantee_name VARCHAR(50)
      COMMENT '权限所有者名称（冗余）';
ALTER TABLE fireflowdemo.T_security_permissions MODIFY COLUMN grantee_type CHAR(10) NOT NULL
      COMMENT '权限所有者类型，U=用户，G=group，R=Role';
ALTER TABLE fireflowdemo.T_security_permissions MODIFY COLUMN function_code VARCHAR(50) NOT NULL
      COMMENT '权限代号';
ALTER TABLE fireflowdemo.T_security_permissions MODIFY COLUMN function_name VARCHAR(100)
      COMMENT '权限名称（冗余字段）';
ALTER TABLE fireflowdemo.T_security_permissions MODIFY COLUMN last_update_person VARCHAR(50) NOT NULL
      COMMENT '授予操作人姓名';
ALTER TABLE fireflowdemo.T_security_permissions MODIFY COLUMN last_update_time TIMESTAMP NOT NULL
      COMMENT '授权操作时间';

CREATE TABLE fireflowdemo.T_security_user (
       id BIGINT NOT NULL AUTO_INCREMENT
     , login_name VARCHAR(100) NOT NULL
     , name VARCHAR(50) NOT NULL
     , pwd VARCHAR(128) NOT NULL DEFAULT ''
     , salt CHAR(50) NOT NULL DEFAULT ''
     , security_key VARCHAR(64)
     , expire_date DATETIME
     , must_change_pwd BOOLEAN NOT NULL DEFAULT true
     , status TINYINT NOT NULL DEFAULT 0
     , is_admin TINYINT NOT NULL DEFAULT 0
     , org_code VARCHAR(50) NOT NULL
     , org_name VARCHAR(60)
     , group_code VARCHAR(50) NOT NULL DEFAULT '0'
     , group_name VARCHAR(60)
     , tel VARCHAR(20)
     , employee_id VARCHAR(50) NOT NULL
     , last_update_person VARCHAR(50) NOT NULL
     , last_update_time TIMESTAMP NOT NULL
     , UNIQUE UQ_operator_email (login_name)
     , UNIQUE UQ_T_security_user_1 (name)
     , PRIMARY KEY (id)
     , INDEX (org_code)
     , CONSTRAINT FK_T_security_user_1 FOREIGN KEY (org_code)
                  REFERENCES fireflowdemo.T_hr_organization (code)
     , INDEX (group_code)
     , CONSTRAINT FK_T_security_user_2 FOREIGN KEY (group_code)
                  REFERENCES fireflowdemo.T_security_group (code)
)ENGINE = innoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE fireflowdemo.T_security_user COMMENT='系统用户表';
ALTER TABLE fireflowdemo.T_security_user MODIFY COLUMN login_name VARCHAR(100) NOT NULL
      COMMENT '登录名称';
ALTER TABLE fireflowdemo.T_security_user MODIFY COLUMN name VARCHAR(50) NOT NULL
      COMMENT '姓名';
ALTER TABLE fireflowdemo.T_security_user MODIFY COLUMN pwd VARCHAR(128) NOT NULL DEFAULT ''
      COMMENT '密码';
ALTER TABLE fireflowdemo.T_security_user MODIFY COLUMN salt CHAR(50) NOT NULL DEFAULT ''
      COMMENT '加密盐';
ALTER TABLE fireflowdemo.T_security_user MODIFY COLUMN security_key VARCHAR(64)
      COMMENT '随机生成的32位字符串';
ALTER TABLE fireflowdemo.T_security_user MODIFY COLUMN expire_date DATETIME
      COMMENT '过期时间';
ALTER TABLE fireflowdemo.T_security_user MODIFY COLUMN must_change_pwd BOOLEAN
      COMMENT '是否在登陆时强制修改密码';
ALTER TABLE fireflowdemo.T_security_user MODIFY COLUMN status TINYINT NOT NULL DEFAULT 0
      COMMENT '状态，0=禁用或者未激活，3=正常状态；如果所属组织被禁用，则一律不准登录。';
ALTER TABLE fireflowdemo.T_security_user MODIFY COLUMN is_admin TINYINT NOT NULL DEFAULT 0
      COMMENT '是否是管理员，0不是，1是';
ALTER TABLE fireflowdemo.T_security_user MODIFY COLUMN org_code VARCHAR(50)
      COMMENT '组织机构代码';
ALTER TABLE fireflowdemo.T_security_user MODIFY COLUMN org_name VARCHAR(200)
      COMMENT '所属组织机构名称';
ALTER TABLE fireflowdemo.T_security_user MODIFY COLUMN group_code VARCHAR(50) NOT NULL DEFAULT '0'
      COMMENT '所属用户组的代号，没有用户组的时候，该值为0';
ALTER TABLE fireflowdemo.T_security_user MODIFY COLUMN tel VARCHAR(20)
      COMMENT '联系电话';
ALTER TABLE fireflowdemo.T_security_user MODIFY COLUMN last_update_person VARCHAR(50) NOT NULL
      COMMENT '最后修改人';
ALTER TABLE fireflowdemo.T_security_user MODIFY COLUMN last_update_time TIMESTAMP NOT NULL
      COMMENT '最后修改时间';

      
CREATE TABLE fireflowdemo.T_security_role (
       id BIGINT NOT NULL AUTO_INCREMENT
     , code VARCHAR(50) NOT NULL
     , name VARCHAR(100) NOT NULL
     , description VARCHAR(100)
	 , is_built_in TINYINT NOT NULL DEFAULT 0
	 , is_position TINYINT NOT NULL DEFAULT 0
     , last_update_person VARCHAR(50) NOT NULL
     , last_update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
     , UNIQUE UQ_T_security_role_code (code)
     , PRIMARY KEY (id)
)ENGINE = innoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE fireflowdemo.T_security_role COMMENT='角色表';
ALTER TABLE fireflowdemo.T_security_role MODIFY COLUMN code VARCHAR(50) NOT NULL
      COMMENT '角色表代码';
ALTER TABLE fireflowdemo.T_security_role MODIFY COLUMN name VARCHAR(100) NOT NULL
      COMMENT '角色名称';
ALTER TABLE fireflowdemo.T_security_role MODIFY COLUMN description VARCHAR(100)
      COMMENT '角色描述';
ALTER TABLE fireflowdemo.T_security_role MODIFY COLUMN last_update_person VARCHAR(50) NOT NULL
      COMMENT '最后修改人';
ALTER TABLE fireflowdemo.T_security_role MODIFY COLUMN last_update_time TIMESTAMP NOT NULL
      COMMENT '最后修改时间';
ALTER TABLE fireflowdemo.T_security_role MODIFY COLUMN is_built_in TINYINT NOT NULL DEFAULT 0
      COMMENT '是否为内置角色，内置角色在界面上不可以删除，1=是，0=否';      

      
CREATE TABLE fireflowdemo.T_security_user_role (
       id BIGINT NOT NULL AUTO_INCREMENT
     , user_code VARCHAR(50) NOT NULL
     , role_code VARCHAR(50) NOT NULL
     , group_code VARCHAR(50) NOT NULL DEFAULT '0'
     , last_update_person VARCHAR(50) NOT NULL
     , last_update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
     , UNIQUE UQ_T_security_user_role_1 (user_code, role_code)
     , PRIMARY KEY (id)
)ENGINE = innoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE fireflowdemo.T_security_user_role COMMENT='用户角色关联表';
ALTER TABLE fireflowdemo.T_security_user_role MODIFY COLUMN user_code VARCHAR(50) NOT NULL
      COMMENT '用户编码';
ALTER TABLE fireflowdemo.T_security_user_role MODIFY COLUMN role_code VARCHAR(50) NOT NULL
      COMMENT '角色编码';
ALTER TABLE fireflowdemo.T_security_user_role MODIFY COLUMN group_code VARCHAR(50) NOT NULL DEFAULT '0'
      COMMENT '所属群组，当role是岗位时，需要填写所属群组代码；否则此字段的值为0';
ALTER TABLE fireflowdemo.T_security_user_role MODIFY COLUMN last_update_person VARCHAR(50) NOT NULL
      COMMENT '最后修改人';
ALTER TABLE fireflowdemo.T_security_user_role MODIFY COLUMN last_update_time TIMESTAMP NOT NULL
      COMMENT '最后修改时间';
      

CREATE TABLE fireflowdemo.T_security_role_belongs (
       id BIGINT NOT NULL AUTO_INCREMENT
     , org_group_code VARCHAR(50) NOT NULL
     , role_code VARCHAR(50) NOT NULL
     , last_update_person VARCHAR(50) NOT NULL
     , last_update_time TIMESTAMP NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
     , UNIQUE UQ_T_security_role_belongs_1 (org_group_code, role_code)
     , PRIMARY KEY (id)
)ENGINE = innoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE fireflowdemo.T_security_role_belongs MODIFY COLUMN org_group_code VARCHAR(50) NOT NULL
      COMMENT '所属的组织机构或者群组代号';
ALTER TABLE fireflowdemo.T_security_role_belongs MODIFY COLUMN role_code VARCHAR(50) NOT NULL
      COMMENT '角色代号';
ALTER TABLE fireflowdemo.T_security_role_belongs MODIFY COLUMN last_update_person VARCHAR(50) NOT NULL
      COMMENT '最后修改人';
ALTER TABLE fireflowdemo.T_security_role_belongs MODIFY COLUMN last_update_time TIMESTAMP NOT NULL
      COMMENT '最后修改时间';