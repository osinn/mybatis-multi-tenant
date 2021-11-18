DROP TABLE IF EXISTS `book`;
CREATE TABLE `book`
(
    `id`        bigint(20) NOT NULL AUTO_INCREMENT,
    `book_name` varchar(255) NOT NULL,
    `user_id`   bigint(20) NOT NULL,
    `tenant_id` int(11) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`        bigint(20) NOT NULL AUTO_INCREMENT,
    `username`  varchar(255) NOT NULL,
    `password`  varchar(255) NOT NULL,
    `tenant_id` int(11) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`
(
    `id`        bigint(20) NOT NULL AUTO_INCREMENT,
    `name`      varchar(255) NOT NULL,
    `tenant_id` int(11) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`
(
    `id`      bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id` int(11) NOT NULL,
    `role_id` int(11) NOT NULL,
    `tenant_id` int(11) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `account`;
CREATE TABLE `account`
(
    `id`        bigint(20) NOT NULL AUTO_INCREMENT,
    `account`  varchar(255) NOT NULL,
    `password`  varchar(255) NOT NULL,
    `tenant_id` int(11) NOT NULL,
    PRIMARY KEY (`id`)
);

INSERT INTO `book` VALUES (1, '三国演义', 1, 1);
INSERT INTO `book` VALUES (2, '西游记', 2, 2);

INSERT INTO `user` VALUES (1, 'root', '123', 1);
INSERT INTO `user` VALUES (2, 'user', '123', 2);
INSERT INTO `user` VALUES (3, '张三', '123', 1);
INSERT INTO `role` VALUES (1, 'admin', 1);
INSERT INTO `role` VALUES (2, '测试', 1);
INSERT INTO `role` VALUES (3, '测试', 2);
INSERT INTO `user_role` VALUES (1, 1, 1, 1);
INSERT INTO `user_role` VALUES (2, 2, 3, 2);
INSERT INTO `user_role` VALUES (3, 1, 2, 1);

INSERT INTO `account` VALUES (1, '测试账号', '123', 1);
INSERT INTO `account` VALUES (2, 'admin', '123', 2);