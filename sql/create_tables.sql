create table user
(
    id           bigint auto_increment comment '用户id'
        primary key,
    userName     varchar(256)                       null comment '用户名',
    userAccount  varchar(256)                       not null comment '账号',
    gender       tinyint                            null comment ' 性别 0 -男性 1-女性',
    userPassword varchar(256)                       not null comment '密码',
    avatarUrl    varchar(1024)                      null comment '头像 ',
    email        varchar(256)                       null comment '邮箱',
    phone        varchar(256)                       null comment '电话号码',
    userRole     tinyint  default 0                 not null comment '用户身份 0-普通用户 1-管理员',
    userStatus   tinyint  default 0                 not null comment '账号状态',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    planetCode   varchar(512)                       null comment '星球编号'
)
    comment '用户表';