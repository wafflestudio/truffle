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

create TABLE IF NOT EXISTS `exceptions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `app_id` bigint NOT NULL,
  `class_name` varchar(50) NOT NULL,
  `elements` text NOT NULL,
  `hash_code` int NOT NULL,
  `status` int NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
);

create TABLE IF NOT EXISTS `exception_events` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `exception_id` bigint NOT NULL,
  `message` varchar(300) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);
