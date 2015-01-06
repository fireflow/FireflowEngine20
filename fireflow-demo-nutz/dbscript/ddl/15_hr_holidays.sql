DROP TABLE IF EXISTS fireflowdemo.T_hr_holidays;
DROP  TABLE  IF EXISTS fireflowdemo.T_hr_leave_request_detail;
DROP  TABLE  IF EXISTS fireflowdemo.T_hr_leave_request ;

CREATE TABLE fireflowdemo.T_hr_holidays (
       id BIGINT NOT NULL AUTO_INCREMENT
     , employee_id VARCHAR(50) NOT NULL
     , year_start DATE NOT NULL
     , year_end DATE NOT NULL
     , paid_vacation_days NUMERIC(4, 1) NOT NULL DEFAULT 0
     , used_paid_vacation_days NUMERIC(4, 1) NOT NULL DEFAULT 0
     , used_sick_leave_days NUMERIC(4, 1) NOT NULL DEFAULT 0
     , used_absence_leave_days NUMERIC(4, 1) NOT NULL DEFAULT 0
     , used_marital_leave_days NUMERIC(4, 1) NOT NULL DEFAULT 0
     , used_maternity_leave_days NUMERIC(4, 1) NOT NULL DEFAULT 0
     , used_funeral_leave_days NUMERIC(4, 1) NOT NULL DEFAULT 0
     , last_update_person VARCHAR(50) NOT NULL DEFAULT 'sys'
     , last_update_time  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
     , UNIQUE UQ_T_hr_holidays_1 (employee_id, year_start, year_end)
     , PRIMARY KEY (id)
)ENGINE = innoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE fireflowdemo.T_hr_leave_request (
       id BIGINT NOT NULL AUTO_INCREMENT
     , bill_code VARCHAR(50) NOT NULL
     , employee_id VARCHAR(50) NOT NULL
     , employee_name VARCHAR(50) NOT NULL
     , creator_id VARCHAR(50) NOT NULL
     , create_time DATETIME NOT NULL     
     , leave_type VARCHAR(20) NOT NULL
     , leave_type_name VARCHAR(50) NOT NULL
     , from_date DATE NOT NULL
     , to_date DATE NOT NULL
     , total_days NUMERIC(4, 1) NOT NULL
     , dept_mgr_id VARCHAR(50)
     , dept_mgr_name VARCHAR(50)
     , dept_approve_flag TINYINT
     , dept_approve_time DATETIME
     , dept_approve_info VARCHAR(150)
     , director_id VARCHAR(50)
     , director_name VARCHAR(50)
     , director_approve_flag TINYINT
     , director_approve_time DATETIME
     , director_approve_info VARCHAR(150)
     , ceo_id VARCHAR(50)
     , ceo_name VARCHAR(50)
     , ceo_approve_flag TINYINT
     , ceo_approve_time DATETIME
     , ceo_approve_info VARCHAR(150)
     , financial_record_uid VARCHAR(50)
     , financial_record_uname VARCHAR(50)
     , financial_record_time DATETIME
     , financial_record_info VARCHAR(150)
     , last_update_person VARCHAR(50) NOT NULL DEFAULT 'sys'
     , last_update_time  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
     , UNIQUE UQ_T_hr_leave_request_1 (bill_code)
     , PRIMARY KEY (id)
)ENGINE = innoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;



CREATE TABLE fireflowdemo.T_hr_leave_request_detail (
       id BIGINT NOT NULL AUTO_INCREMENT
     , bill_code VARCHAR(50) NOT NULL
     , leave_date DATE NOT NULL
     , time_section TINYINT NOT NULL DEFAULT 2
     , financial_record_uid VARCHAR(50)
     , financial_record_uname VARCHAR(50)
     , financial_record_time DATETIME
     , financial_record_info CHAR(10)
     , last_update_person CHAR(10) NOT NULL DEFAULT 'sys'
     , last_update_time  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
     , PRIMARY KEY (id)
     , INDEX (bill_code)
     , CONSTRAINT FK_T_hr_leave_request_detail_1 FOREIGN KEY (bill_code)
                  REFERENCES fireflowdemo.T_hr_leave_request (bill_code)
);


