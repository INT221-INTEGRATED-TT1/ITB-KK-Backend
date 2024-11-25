CREATE
USER 'dev1'@'%' IDENTIFIED BY 'dev@sit';
GRANT ALL privileges ON *.* TO
'dev1'@'%';
CREATE DATABASE IF NOT EXISTS `task_base_v3` CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE `task_base_v3`;

CREATE TABLE `localusers` (
                              `oid` varchar(36) NOT NULL,
                              `name` varchar(100) NOT NULL,
                              `username` varchar(50) NOT NULL,
                              `email` varchar(50) NOT NULL,
                              `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              `updated_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              PRIMARY KEY (`oid`),
                              UNIQUE KEY `oid_UNIQUE` (`oid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `boards` (
                          `boardId` varchar(45) NOT NULL,
                          `name` varchar(120) NOT NULL,
                          `visibility` varchar(45) NOT NULL,
                          `owner_id` varchar(36) NOT NULL,
                          `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          `updated_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          PRIMARY KEY (`boardId`),
                          UNIQUE KEY `boardID_UNIQUE` (`boardId`),
                          KEY `users_FK_idx` (`owner_id`),
                          CONSTRAINT `users_FK` FOREIGN KEY (`owner_id`) REFERENCES `localusers` (`oid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `collaborator` (
                                `collabId` int NOT NULL AUTO_INCREMENT,
                                `board_id` varchar(45) NOT NULL,
                                `user_id` varchar(45) NOT NULL,
                                `accessRight` varchar(45) NOT NULL,
                                `addedOn` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (`collabId`),
                                KEY `collab_FK_idx` (`board_id`),
                                KEY `loaclUser_FK_idx` (`user_id`),
                                CONSTRAINT `collabBoard_FK` FOREIGN KEY (`board_id`) REFERENCES `boards` (`boardId`) ON DELETE CASCADE,
                                CONSTRAINT `collabUser_FK` FOREIGN KEY (`user_id`) REFERENCES `localusers` (`oid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `statuses` (
                            `statusId` int NOT NULL AUTO_INCREMENT,
                            `name` varchar(50) NOT NULL,
                            `description` varchar(200) DEFAULT NULL,
                            `color` varchar(10) DEFAULT NULL,
                            `board_id` varchar(45) NOT NULL,
                            `created_on` datetime DEFAULT NULL,
                            `updated_on` datetime DEFAULT NULL,
                            PRIMARY KEY (`statusId`),
                            KEY `boardStatus_FK_idx` (`board_id`),
                            CONSTRAINT `boardStatus_FK` FOREIGN KEY (`board_id`) REFERENCES `boards` (`boardId`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tasks3` (
                          `taskId` int NOT NULL AUTO_INCREMENT,
                          `title` varchar(100) NOT NULL,
                          `description` varchar(500) DEFAULT NULL,
                          `assignees` varchar(30) DEFAULT NULL,
                          `status_id` int NOT NULL,
                          `board_id` varchar(10) NOT NULL,
                          `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          `updated_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          PRIMARY KEY (`taskId`),
                          KEY `status_FK_idx` (`status_id`),
                          KEY `board_FK_idx` (`board_id`),
                          CONSTRAINT `board_FK` FOREIGN KEY (`board_id`) REFERENCES `boards` (`boardId`),
                          CONSTRAINT `status_FK` FOREIGN KEY (`status_id`) REFERENCES `statuses` (`statusId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;