# 系统用户
CREATE TABLE sys_user(
  id BIGINT AUTO_INCREMENT COMMENT 'id',
  username VARCHAR(16) NOT NULL COMMENT '用户名',
  password VARCHAR(16) NOT NULL COMMENT '密码',
  phone CHAR(11) DEFAULT '' COMMENT '手机',

  PRIMARY KEY (id)

)ENGINE = InnoDB AUTO_INCREMENT = 1000 COMMENT='系统用户表';

# 用户_角色关联表
CREATE TABLE sys_user_role(
  id BIGINT AUTO_INCREMENT COMMENT 'id',
  sys_user_id BIGINT NOT NULL COMMENT '用户id',
  sys_role_id BIGINT NOT NULL COMMENT '角色id',

  PRIMARY KEY (id)
)ENGINE = InnoDB AUTO_INCREMENT = 1000 COMMENT='用户_角色关联表';

# 系统角色
CREATE TABLE sys_role(
  id BIGINT AUTO_INCREMENT COMMENT 'id',
  name VARCHAR(16) NOT NULL COMMENT '角色名',

  PRIMARY KEY (id)

)ENGINE = InnoDB AUTO_INCREMENT = 1000 COMMENT='系统角色表';

# 角色_权限关联表
CREATE TABLE  sys_role_permission(
  id BIGINT AUTO_INCREMENT COMMENT 'id',
  sys_role_id BIGINT NOT NULL COMMENT '角色id',
  sys_permission_id BIGINT NOT NULL COMMENT '权限id',

  PRIMARY KEY (id)
)ENGINE = InnoDB AUTO_INCREMENT = 1000 COMMENT='角色_权限关联表';

# 系统权限表
CREATE TABLE sys_permission(
  id BIGINT AUTO_INCREMENT COMMENT 'id',
  name VARCHAR(16) NOT NULL COMMENT '权限名',
  url VARCHAR(32) NOT NULL COMMENT 'url',
  description VARCHAR(32) DEFAULT '' COMMENT '备注',
  pid BIGINT DEFAULT 0 COMMENT '父id',

  PRIMARY KEY (id)

)ENGINE = InnoDB AUTO_INCREMENT = 1000 COMMENT='系统权限表';

INSERT INTO sys_user(username, password,phone)
  VALUE ("a","111111","111111111"),
  ("b","111111","22222222"),
  ("c","111111","33333333"),
  ("d","111111","4444444444"),
  ("e","111111","5555555"),
  ("f","111111","666666666");

INSERT INTO sys_permission(name, url,description,pid)
  VALUE ("权限a","/a","权限a",0),
  ("权限b","/b","权限b",0),
  ("权限c","/c","权限c",0),
  ("权限d","/d","权限d",0),
  ("权限e","/e","权限e",0),
  ("权限f","/**","权限f",0);

INSERT INTO sys_role(name)
  VALUE ("角色a"),
  ("角色b"),
  ("角色c"),
  ("角色d"),
  ("角色e"),
  ("角色f");

INSERT sys_role_permission (sys_role_id, sys_permission_id)
  VALUE (1000, 1000),
  (1001, 1000),
  (1001, 1001),
  (1002, 1000),
  (1002, 10001),
  (1002, 1002),
  (1003, 1000),
  (1003, 1001),
  (1003, 1002),
  (1003, 1003),
  (1004, 1000),
  (1004, 1001),
  (1004, 1002),
  (1004, 1003),
  (1004, 1004),
  (1005, 1005);

INSERT INTO sys_user_role(sys_user_id, sys_role_id)
  VALUE (1000,1000),
  (1001,1000),
  (1001,1001),
  (1002,1000),
  (1002,1001),
  (1002,1002),
  (1003,1000),
  (1003,1001),
  (1003,1002),
  (1003,1003),
  (1004,1000),
  (1004,1001),
  (1004,1002),
  (1004,1003),
  (1004,1004),
  (1005,1005),
  (1005,1000);
