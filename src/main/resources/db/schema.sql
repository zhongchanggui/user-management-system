-- 用户管理系统数据库脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS user_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE user_management;

-- 用户表
CREATE TABLE IF NOT EXISTS t_user (
    fid BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    floginname VARCHAR(50) NOT NULL COMMENT '用户名',
    fpassword VARCHAR(100) NOT NULL COMMENT '密码',
    fnickname VARCHAR(50) NOT NULL COMMENT '昵称',
    fstate TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0=禁用、1=正常、2=冻结）',
    fbirthday DATE COMMENT '出生日期',
    fphonenumber VARCHAR(20) COMMENT '电话号码',
    femail VARCHAR(50) COMMENT '电子邮箱',
    ftype TINYINT NOT NULL DEFAULT 0 COMMENT '类型（0=注册、1=添加）',
    fcreatedatetime DATETIME NOT NULL DEFAULT NOW() COMMENT '创建时间',
    fcreateuserid BIGINT COMMENT '创建人主键',
    fsex TINYINT DEFAULT NULL COMMENT '性别（0=女、1=男）',
    faddress VARCHAR(200) DEFAULT NULL COMMENT '地址',
    fidcard VARCHAR(20) DEFAULT NULL COMMENT '身份证号',
    fentrydate DATE DEFAULT NULL COMMENT '入职日期',
    fdeptid BIGINT DEFAULT NULL COMMENT '部门ID',
    fposition VARCHAR(100) DEFAULT NULL COMMENT '职位',
    fsuperiorid BIGINT DEFAULT NULL COMMENT '上级ID',
    PRIMARY KEY (fid),
    UNIQUE KEY uk_loginname (floginname),
    KEY idx_state (fstate),
    KEY idx_type (ftype),
    KEY idx_createuserid (fcreateuserid),
    KEY idx_deptid (fdeptid),
    KEY idx_superiorid (fsuperiorid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入默认管理员账号（密码：admin123，明文存储）
INSERT INTO t_user (fid, floginname, fpassword, fnickname, fstate, fbirthday, fphonenumber, femail, ftype, fcreatedatetime, fcreateuserid)
VALUES (1, 'admin', 'admin123', '系统管理员', 1, '1990-01-01', '13800138000', 'admin@example.com', 1, NOW(), NULL)
ON DUPLICATE KEY UPDATE floginname = floginname;
