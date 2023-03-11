create TABLE IF NOT EXISTS `apps` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `phase` varchar(20) DEFAULT NULL,
  `api_key` varchar(50) NOT NULL,
  `slack_channel` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE (`name`, `phase`),
  UNIQUE (`api_key`)
);
