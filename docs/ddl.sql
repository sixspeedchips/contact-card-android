CREATE TABLE IF NOT EXISTS `contact`
(
    `contact_id` INTEGER PRIMARY KEY AUTOINCREMENT,
    `first_name` TEXT,
    `last_name`  TEXT,
    `email`      TEXT,
    `phone`      TEXT,
    `created`    INTEGER NOT NULL,
    `updated`    INTEGER NOT NULL
);
CREATE INDEX IF NOT EXISTS `index_Contact_first_name` ON `contact` (`first_name`);
CREATE INDEX IF NOT EXISTS `index_Contact_last_name` ON `contact` (`last_name`);
CREATE INDEX IF NOT EXISTS `index_Contact_created` ON `contact` (`created`);
CREATE INDEX IF NOT EXISTS `index_Contact_updated` ON `contact` (`updated`);
CREATE TABLE IF NOT EXISTS `image`
(
    `image_id`   INTEGER PRIMARY KEY AUTOINCREMENT,
    `contact_id` INTEGER,
    `image_url`  TEXT,
    `raw`        INTEGER NOT NULL,
    `created`    INTEGER NOT NULL,
    FOREIGN KEY (`contact_id`) REFERENCES `Contact` (`contact_id`) ON UPDATE NO ACTION ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS `index_Image_contact_id` ON `image` (`contact_id`);
CREATE INDEX IF NOT EXISTS `index_Image_created` ON `image` (`created`);
CREATE TABLE IF NOT EXISTS `processed_text`
(
    `processed_text_id` INTEGER,
    `canonical_key`     TEXT,
    `contact_id`        INTEGER,
    `created`           INTEGER NOT NULL,
    `field_value`       TEXT,
    PRIMARY KEY (`processed_text_id`),
    FOREIGN KEY (`contact_id`) REFERENCES `Contact` (`contact_id`) ON UPDATE NO ACTION ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS `index_ProcessedText_canonical_key` ON `processed_text` (`canonical_key`);
CREATE INDEX IF NOT EXISTS `index_ProcessedText_contact_id` ON `processed_text` (`contact_id`);
CREATE INDEX IF NOT EXISTS `index_ProcessedText_created` ON `processed_text` (`created`);
CREATE TABLE IF NOT EXISTS `raw_text`
(
    `raw_text_id` INTEGER PRIMARY KEY AUTOINCREMENT,
    `contact_id`  INTEGER,
    `created`     INTEGER NOT NULL,
    `text_output` TEXT,
    FOREIGN KEY (`contact_id`) REFERENCES `Contact` (`contact_id`) ON UPDATE NO ACTION ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS `index_RawText_contact_id` ON `raw_text` (`contact_id`);
CREATE INDEX IF NOT EXISTS `index_RawText_created` ON `raw_text` (`created`);