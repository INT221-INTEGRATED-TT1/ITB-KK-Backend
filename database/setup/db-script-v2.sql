CREATE USER 'dev1'@'%' IDENTIFIED BY 'dev@sit';
GRANT ALL privileges ON . TO 'dev1'@'%';
CREATE SCHEMA `task_base`;
CREATE TABLE `task_base`.`tasks` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `assignees` VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `status` ENUM('NO_STATUS', 'TO_DO', 'DOING', 'DONE') NOT NULL DEFAULT 'NO_STATUS',
  `createdOn` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  `updatedOn` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE `task_base`.`tasks2` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `assignees` VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `statusNo` INT NOT NULL DEFAULT 101,
  `createdOn` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  `updatedOn` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE `task_base`.`statuses` (
  `statusNo` INT NOT NULL AUTO_INCREMENT,
  `statusName` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL UNIQUE,
  `statusDescription` VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `statusColor` VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  PRIMARY KEY (`statusNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
USE task_base;
ALTER TABLE statuses AUTO_INCREMENT = 101;
INSERT INTO statuses (statusName, statusDescription, statusColor) VALUES ('No Status',null,'#5A5A5A');
INSERT INTO statuses (statusName, statusDescription, statusColor) VALUES ('To Do',null,'#321b00');
INSERT INTO statuses (statusName, statusDescription, statusColor) VALUES ('Doing',null,'#102049');
INSERT INTO statuses (statusName, statusDescription, statusColor) VALUES ('Done',null,'#043109');
INSERT INTO tasks (title, description, assignees, status, createdOn, updatedOn) VALUES ('TaskTitle1TaskTitle2TaskTitle3TaskTitle4TaskTitle5TaskTitle6TaskTitle7TaskTitle8TaskTitle9TaskTitle0','Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti1Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti2Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti3Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti4Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti5','Assignees1Assignees2Assignees3','NO_STATUS','2024-04-22 09:00:00','2024-04-22 09:00:00');
INSERT INTO tasks (title, description, assignees, status, createdOn, updatedOn) VALUES ('Repository',null,null,'TO_DO','2024-04-22 09:05:00','2024-04-22 14:00:00');
INSERT INTO tasks (title, description, assignees, status, createdOn, updatedOn) VALUES ('ดาต้าเบส','ສ້າງຖານຂໍ້ມູນ','あなた、彼、彼女 (私ではありません)','DOING','2024-04-22 09:10:00','2024-04-25 00:00:00');
INSERT INTO tasks (title, description, assignees, status, createdOn, updatedOn) VALUES ('_Infrastructure_','_Setup containers_','ไก่งวง กับ เพนกวิน','DONE','2024-04-22 09:15:00','2024-04-22 10:00:00');
INSERT INTO tasks2 (title, description, assignees, statusNo, createdOn, updatedOn) VALUES ('TaskTitle1TaskTitle2TaskTitle3TaskTitle4TaskTitle5TaskTitle6TaskTitle7TaskTitle8TaskTitle9TaskTitle0','Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti1Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti2Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti3Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti4Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti5','Assignees1Assignees2Assignees3',101,'2024-04-22 09:00:00','2024-04-22 09:00:00');
INSERT INTO tasks2 (title, description, assignees, statusNo, createdOn, updatedOn) VALUES ('Repository',null,null,102,'2024-04-22 09:05:00','2024-04-22 14:00:00');
INSERT INTO tasks2 (title, description, assignees, statusNo, createdOn, updatedOn) VALUES ('ดาต้าเบส','ສ້າງຖານຂໍ້ມູນ','あなた、彼、彼女 (私ではありません)',103,'2024-04-22 09:10:00','2024-04-25 00:00:00');
INSERT INTO tasks2 (title, description, assignees, statusNo, createdOn, updatedOn) VALUES ('_Infrastructure_','_Setup containers_','ไก่งวง กับ เพนกวิน',104,'2024-04-22 09:15:00','2024-04-22 10:00:00');
