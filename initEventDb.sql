INSERT INTO `cfpdev`.`events` (`id`) VALUES ('default');

INSERT INTO `cfpdev`.`config` (`key`, `value`, `event_id`) VALUES ('eventName', 'Breizhcamp 2017', 'default');
INSERT INTO `cfpdev`.`config` (`key`, `value`, `event_id`) VALUES ('community', 'Breizhcamp', 'default');
INSERT INTO `cfpdev`.`config` (`key`, `value`, `event_id`) VALUES ('date', '23/03/2017', 'default');
INSERT INTO `cfpdev`.`config` (`key`, `value`, `event_id`) VALUES ('releaseDate', '13/01/2017', 'default');
INSERT INTO `cfpdev`.`config` (`key`, `value`, `event_id`) VALUES ('decisionDate', '25/01/2017', 'default');
INSERT INTO `cfpdev`.`config` (`key`, `value`, `event_id`) VALUES ('open', 'true', 'default');


-- pwd = toto
INSERT INTO `cfpdev`.`users` (`email`, `lastname`, `firstname`, `password`, `verified`) VALUES ('breizhcamp@cfp.io', 'camp', 'breizh', '$2a$10$pKrWwOBhBVOCZqIzq2xqDe/AQA/tpfTbAxQzcI5vQMPKxIce6.e76', 1);


