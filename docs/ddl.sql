CREATE TABLE IF NOT EXISTS `Contact`
(
    `contact_id` INTEGER PRIMARY KEY AUTOINCREMENT,
    `first_name` TEXT,
    `last_name`  TEXT,
    `email`      TEXT,
    `phone`      TEXT,
    `created`    INTEGER NOT NULL,
    `updated`    INTEGER NOT NULL
);
CREATE INDEX IF NOT EXISTS `index_Contact_first_name` ON `Contact` (`first_name`);
CREATE INDEX IF NOT EXISTS `index_Contact_last_name` ON `Contact` (`last_name`);
CREATE INDEX IF NOT EXISTS `index_Contact_created` ON `Contact` (`created`);
CREATE INDEX IF NOT EXISTS `index_Contact_updated` ON `Contact` (`updated`);
CREATE TABLE IF NOT EXISTS `Image`
(
    `image_id`   INTEGER PRIMARY KEY AUTOINCREMENT,
    `contact_id` INTEGER,
    `image_url`  TEXT,
    `raw`        INTEGER NOT NULL,
    `created`    INTEGER NOT NULL,
    FOREIGN KEY (`contact_id`) REFERENCES `Contact` (`contact_id`) ON UPDATE NO ACTION ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS `index_Image_contact_id` ON `Image` (`contact_id`);
CREATE INDEX IF NOT EXISTS `index_Image_created` ON `Image` (`created`);
CREATE TABLE IF NOT EXISTS `ProcessedText`
(
    `processed_text_id` INTEGER,
    `canonical_key`     TEXT,
    `contact_id`        INTEGER,
    `created`           INTEGER NOT NULL,
    `field_value`       TEXT,
    PRIMARY KEY (`processed_text_id`),
    FOREIGN KEY (`contact_id`) REFERENCES `Contact` (`contact_id`) ON UPDATE NO ACTION ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS `index_ProcessedText_canonical_key` ON `ProcessedText` (`canonical_key`);
CREATE INDEX IF NOT EXISTS `index_ProcessedText_contact_id` ON `ProcessedText` (`contact_id`);
CREATE INDEX IF NOT EXISTS `index_ProcessedText_created` ON `ProcessedText` (`created`);
CREATE TABLE IF NOT EXISTS `RawText`
(
    `raw_text_id` INTEGER PRIMARY KEY AUTOINCREMENT,
    `contact_id`  INTEGER,
    `created`     INTEGER NOT NULL,
    `text_output` TEXT,
    FOREIGN KEY (`contact_id`) REFERENCES `Contact` (`contact_id`) ON UPDATE NO ACTION ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS `index_RawText_contact_id` ON `RawText` (`contact_id`);
CREATE INDEX IF NOT EXISTS `index_RawText_created` ON `RawText` (`created`);
CREATE TABLE IF NOT EXISTS `FirstName`
(
    `name_id` INTEGER PRIMARY KEY AUTOINCREMENT,
    `name`    TEXT
);
CREATE INDEX IF NOT EXISTS `index_FirstName_name` ON `FirstName` (`name`);
CREATE TABLE IF NOT EXISTS `LastName`
(
    `name_id` INTEGER PRIMARY KEY AUTOINCREMENT,
    `name`    TEXT
);
CREATE INDEX IF NOT EXISTS `index_LastName_name` ON `LastName` (`name`);
