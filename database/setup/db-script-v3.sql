CREATE
USER 'dev1'@'%' IDENTIFIED BY 'dev@sit';
GRANT ALL privileges ON *.* TO
'dev1'@'%';
CREATE
DATABASE IF NOT EXISTS `task_base_v3` CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE
`task_base_v3`;

CREATE TABLE `boards`
(
    `boardID`   varchar(45)  NOT NULL,
    `boardName` varchar(120) NOT NULL,
    `ownerID`   varchar(36)  NOT NULL,
    `createOn`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updateOn`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`boardID`),
    UNIQUE KEY `boardID_UNIQUE` (`boardID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `statuses`
(
    `statusID`          int         NOT NULL AUTO_INCREMENT,
    `statusName`        varchar(50) NOT NULL,
    `statusDescription` varchar(200) DEFAULT NULL,
    `statusColor`       varchar(10)  DEFAULT NULL,
    `boardID`           varchar(45) NOT NULL,
    PRIMARY KEY (`statusID`),
    KEY                 `board_FK_idx` (`boardID`),
    CONSTRAINT `boardStatus_FK` FOREIGN KEY (`boardID`) REFERENCES `boards` (`boardID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tasks3`
(
    `taskID`      int          NOT NULL AUTO_INCREMENT,
    `taskTitle`   varchar(100) NOT NULL,
    `description` varchar(500)          DEFAULT NULL,
    `assignees`   varchar(30)           DEFAULT NULL,
    `statusID`    int          NOT NULL,
    `boardID`     varchar(45)  NOT NULL,
    `createOn`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updateOn`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`taskID`),
    KEY           `status_FK_idx` (`statusID`),
    KEY           `board_FK_idx` (`boardID`),
    CONSTRAINT `board_FK` FOREIGN KEY (`boardID`) REFERENCES `boards` (`boardID`),
    CONSTRAINT `status_FK` FOREIGN KEY (`statusID`) REFERENCES `statuses` (`statusID`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;