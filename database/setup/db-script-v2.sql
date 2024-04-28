CREATE USER 'dev1'@'%' IDENTIFIED BY 'dev@sit';
GRANT ALL privileges ON *.* TO 'dev1'@'%' ;
CREATE SCHEMA  IF NOT EXISTS `task_base` ;
USE task_base;
SET character_set_results = 'utf8mb4';
CREATE TABLE `task_base`.`tasks` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `assignees` VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `status` ENUM('NO_STATUS', 'TO_DO', 'DOING', 'DONE') NOT NULL DEFAULT 'NO_STATUS',
  `createdOn` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  `updatedOn` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

USE task_base;
INSERT INTO tasks (title, description, assignees) VALUES ('TaskTitle1TaskTitle2TaskTitle3TaskTitle4TaskTitle5TaskTitle6TaskTitle7TaskTitle8TaskTitle9TaskTitle0','Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti1Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti2Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti3Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti4Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti5','Assignees1Assignees2Assignees3');
INSERT INTO tasks (title, description, assignees, status) VALUES ('Repository','','','TO_DO');
INSERT INTO tasks (title, description, assignees, status) VALUES ('ดาต้าเบส','ສ້າງຖານຂໍ້ມູນ','あなた、彼、彼女 (私ではありません)','DOING');
INSERT INTO tasks (title, description, assignees, status) VALUES ('_Infrastructure_','_Setup containers_','ไก่งวง กับ เพนกวิน','DONE');
UPDATE `task_base`.`tasks` SET `createdOn` = '2024-04-22 09:00:00', `updatedOn` = '2024-04-22 09:00:00' WHERE (`id` = '1');
UPDATE `task_base`.`tasks` SET `createdOn` = '2024-04-22 09:05:00', `updatedOn` = '2024-04-22 14:00:00' WHERE (`id` = '2');
UPDATE `task_base`.`tasks` SET `createdOn` = '2024-04-22 09:10:00', `updatedOn` = '2024-04-25 00:00:00' WHERE (`id` = '3');
UPDATE `task_base`.`tasks` SET `createdOn` = '2024-04-22 09:15:00', `updatedOn` = '2024-04-22 10:00:00' WHERE (`id` = '4');
