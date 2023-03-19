create TABLE IF NOT EXISTS `apps` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `phase` varchar(20) NOT NULL,
  `api_key` varchar(50) NOT NULL,
  `slack_channel` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE (`name`, `phase`),
  UNIQUE (`api_key`)
);
