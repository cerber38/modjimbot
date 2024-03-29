CREATE TABLE `users` (`id` int(11) not null,`sn` varchar(20) default null,`nick` varchar(255) default null,`localnick` varchar(255) default null,`fname` varchar(255) default null,`lname` varchar(255) default null,`email` varchar(255) default null,`city` varchar(255) default null,`homepage` varchar(255) default null,`gender` int(11) default null,`birthyear` int(11) default null,`birthmonth` int(11) default null,`birthday` int(11) default null,`age` int(11) default null,`country` int(11) default null,`language` int(11) default null,`state` int(11) default null,`basesn` varchar(20) default null,`createtime` timestamp not null default current_timestamp on update current_timestamp,`room` int(11) default null,`lastkick` timestamp not null default '0000-00-00 00:00:00',`grouptime` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',`data` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',`lastclosed` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',`ball` INT( 11 ) NULL,`answer` INT( 11 ) NULL,`status` varchar(255) default '',`clansman` INT( 11 ) NULL,`clangroup` varchar(255) default '',`wedding`INT( 11 ) NULL,`car` varchar(255) default '',`home` varchar(255) default '',`clothing` varchar(255) default '',`animal` varchar(255) default '',`notice`INT( 11 ) NULL, `personal_room`INT( 11 ) NULL, primary key  (`id`));
CREATE TABLE `ads` (`id` int(11) NOT NULL auto_increment,`txt` text NOT NULL,`enable` int(1) NOT NULL default '1',`note` text,PRIMARY KEY  (`id`));
CREATE TABLE `ads_log` (`ads_id` int(11) NOT NULL,`time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,`uin` varchar(20) NOT NULL);
CREATE TABLE `aneks` (`id` int(11) NOT NULL,`text` text NOT NULL,PRIMARY KEY  (`id`));
CREATE TABLE `aneks_tmp` (`id` int(11) NOT NULL auto_increment,`text` text,`uin` varchar(20) default NULL,PRIMARY KEY  (`id`));
CREATE TABLE `events` (`id` bigint(20) NOT NULL auto_increment,`time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,`user_id` int(11) default NULL,`user_sn` varchar(50) default NULL,`type` varchar(10) default NULL,`user_id2` int(11) default NULL,`user_sn2` varchar(50) default NULL,`msg` text,PRIMARY KEY  (`id`)) ;
CREATE TABLE `log` (`id` bigint(20) NOT NULL auto_increment,`time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,`user_id` int(11) default NULL,`user_sn` varchar(50) default NULL,`type` varchar(10) default NULL,`msg` text,`room` int(11) NOT NULL,PRIMARY KEY  (`id`));
CREATE TABLE `sms_log` (`id` int(11) NOT NULL auto_increment,`num` varchar(10) default NULL,`operator` varchar(20) default NULL,`user_id` varchar(20) default NULL,`cost` float NOT NULL default '0',`msg` varchar(170) default NULL,PRIMARY KEY  (`id`));
CREATE TABLE `user_props` (`user_id` int(11) NOT NULL,`name` varchar(50) default NULL,`val` varchar(50) default NULL);
CREATE TABLE `rooms` (`id` int(11) NOT NULL,`name` varchar(100) NOT NULL,`topic` varchar(255) NOT NULL,`pass` varchar(20) NOT NULL,`user_id` int(11) NOT NULL, `personal` int(11) NOT NULL, PRIMARY KEY  (`id`));
CREATE TABLE `butilochka` (`id` int(11) NOT NULL,`word` varchar(255) NOT NULL,PRIMARY KEY  (`id`)) ;
CREATE TABLE `scripts` (`id` int(11) NOT NULL,`script` text NOT NULL,`describe` varchar(256) NOT NULL,`enable` int(11) NOT NULL,UNIQUE KEY `id` (`id`)) ENGINE=MyISAM DEFAULT CHARSET=utf8;
CREATE TABLE `admmsg` (`id` int(11) NOT NULL,`id_2` int(11) NOT NULL,`msg` varchar(256) NOT NULL,`time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',PRIMARY KEY  (`id`)) ;
CREATE TABLE `robadmin` (`id` int(11) NOT NULL,`msg` varchar(255) NOT NULL,PRIMARY KEY  (`id`)) ;
CREATE TABLE IF NOT EXISTS `victorina` (`id` int(11) NOT NULL,`question` varchar(255) NOT NULL,`answer` varchar(255) NOT NULL) ENGINE=MyISAM DEFAULT CHARSET=utf8;
CREATE TABLE `frends` (`id` int(11) NOT NULL,`user_id` int(11) NOT NULL,`frend_id` int(11) NOT NULL,`type` varchar(255) NOT NULL,PRIMARY KEY  (`id`)) ;
CREATE TABLE `demand` (`id` int(11) NOT NULL,`user_id` int(11) NOT NULL,`frend_id` int(11) NOT NULL,`type` varchar(255) NOT NULL,PRIMARY KEY  (`id`)) ;
CREATE TABLE `gift` (`id` int(11) NOT NULL,`gift` varchar(255) NOT NULL,`price` int(11) NOT NULL,PRIMARY KEY  (`id`)) ;
CREATE TABLE `thing` (`id` int(11) NOT NULL,`user_id` int(11) NOT NULL,`thing` varchar(255) NOT NULL,PRIMARY KEY  (`id`)) ;
CREATE TABLE `gift_user` (`id` int(11) NOT NULL,`user_id` int(11) NOT NULL,`user_id2` int(11) NOT NULL,`gift` varchar(255) NOT NULL,`text` text(200) NOT NULL,`time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',PRIMARY KEY  (`id`)) ;
CREATE TABLE `clans` (`id` int(11) NOT NULL,`leader_clan` int(11) NOT NULL,`room_clan` int(11) NOT NULL,`name_clan` varchar(255) NOT NULL,`ball_clan` int(11) NOT NULL,`info_clan` varchar(255) NOT NULL, `symbol_clan` varchar(255) NOT NULL, PRIMARY KEY  (`id`)) ;
CREATE TABLE `wall` (`id` int(11) NOT NULL,`user_id` int(11) NOT NULL,`txt` text(200) NOT NULL,`time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',PRIMARY KEY  (`id`));
CREATE TABLE `inforob` (`id` int(11) NOT NULL,`information` varchar(255) NOT NULL,PRIMARY KEY  (`id`)) ;
CREATE TABLE `help` (`id` int(11) NOT NULL,`auth` varchar(20) default NULL,`command` varchar(255) default NULL,`info` varchar(255) default NULL,PRIMARY KEY  (`id`));
CREATE TABLE `xstatus` (`id` int(11) NOT NULL,`number` int(11) NOT NULL,`text` text(300) default NULL,`type` int(11) NOT NULL,PRIMARY KEY  (`id`));
CREATE TABLE `avto` (`id` bigint(20) NOT NULL auto_increment,`tovar` varchar(255) NOT NULL,`price` int(11) NOT NULL,PRIMARY KEY  (`id`));
CREATE TABLE `dom` (`id` bigint(20) NOT NULL auto_increment,`tovar` varchar(255) NOT NULL,`price` int(11) NOT NULL,PRIMARY KEY  (`id`));
CREATE TABLE `odejda` (`id` bigint(20) NOT NULL auto_increment,`tovar` varchar(255) NOT NULL,`price` int(11) NOT NULL,PRIMARY KEY  (`id`));
CREATE TABLE `jivotnoe` (`id` bigint(20) NOT NULL auto_increment,`tovar` varchar(255) NOT NULL,`price` int(11) NOT NULL,PRIMARY KEY  (`id`));
CREATE TABLE `advertisement` (`id` int(11) NOT NULL,`text` varchar(255) NOT NULL,PRIMARY KEY  (`id`)) ;
CREATE TABLE `notice` (`id` int(11) NOT NULL,`user_id` int(11) NOT NULL,`moder_id` int(11) NOT NULL,`notice_text` varchar(255) NOT NULL,PRIMARY KEY  (`id`)) ;
INSERT INTO `inforob` VALUES (0, 'В комнате 555 проходит викторина');
INSERT INTO `robadmin` VALUES (0, 'А, че?');
INSERT INTO `robadmin` VALUES (1, 'хр-р-р... хр-р-р...');
INSERT INTO `robadmin` VALUES (2, 'Опять про то же самое?');
INSERT INTO `robadmin` VALUES (3, 'Не о чем больше поговорить?');
INSERT INTO `robadmin` VALUES (4, 'А с тобой я ваще больше не разговариваю');
INSERT INTO `robadmin` VALUES (5, 'Ты о своем, а я о своем');
INSERT INTO `robadmin` VALUES (6, 'Не, я так не думаю');
INSERT INTO `robadmin` VALUES (7, 'Ты серьезно?');
INSERT INTO `robadmin` VALUES (8, 'С тобой так интересно!');
INSERT INTO `robadmin` VALUES (9, 'Ха-ха, очень смешно...');
CREATE TABLE `text_in_out` (`id` int(11) NOT NULL, `type` varchar(3) NOT NULL, `text` varchar(255) NOT NULL, PRIMARY KEY  (`id`)) ;
INSERT INTO `robadmin` VALUES (10, 'Если ты высокого мения о своем интеллекте, то должен тебя разочаровать');
INSERT INTO `text_in_out` (`id`, `type`, `text`) VALUES (0, 'in', 'вошел(а) в чат *HI*'), (1, 'out', 'ушел(ушла) из чата');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('0', 'all', '!help (!справка !помощь)', 'Справка по командам');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('1', 'all', '!chat (!чат !вход)', 'Вход в чат');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('2', 'all', '!exit (!выход)', 'Выйти из чата');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('3', 'all', '!rules (!правила)', 'Правила чата');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('4', 'all', '!stat (!стат)', 'Статистика по номерам чата');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('5', 'all', '!gofree (!свюин)', 'Перейти на самый свободный уин чата');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('6', 'all', '!go (!юин)', 'Перейти на другой уин чата');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('7', 'ban', '!banlist (!банлист)', 'Список банов');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('8', 'kickone', '!kicklist (!киклист)', 'Список киков');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('9', 'info', '!info <id/uin> (!инфо)', 'Информация о пользователе.');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('10', 'kickone', '!kick <id/uin> (!кик)', 'Кикнуть пользователя');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('11', 'kickall', '!kickall (!кикалл)', 'Кикнуть все пользователей');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('12', 'all', '!listauth (!листаут)', 'Листинг допустимых полномочий');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('13', 'who', '!who <id>(!кто)', 'Последние ники пользователя');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('14', 'all', '!listgroup (!листгрупп)', 'Список допустимых групп');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('15', 'authread', '!checkuser <id> (!проверка)', 'Информация о правах пользователя.');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('16', 'authwrite', '!setgroup <id> <group> (!группа)', 'Установить группу пользователю.');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('17', 'authwrite', '!grant <id> <object> (!добавить)', 'Добавить полномичие пользователю.');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('18', 'authwrite', '!revoke <id> <object> (!лишить)', 'Лишить пользователя полномочий');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('19', 'ban', '!ban <id/uin> (!бан)', 'Забанить пользователя');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('20', 'ban', '!uban <id/uin> (!убан)', 'Разбанить пользователя');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('21', 'reg', '!reg <nick> (!рег !ник)', 'Регистрация/Смена ника в чате');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('22', 'all', '+а (!тут)', 'Список пользователей в комнате');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('23', 'pmsg', '+р <id> <msg> (!лс)', 'Отправить приватное сообщение пользователю');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('24', 'pmsg', '+pp <id> (!ответ)', 'Ответ на последний приват');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('25', 'settheme', '!settheme <test> (!тема)', 'Установить тему комнаты');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('26', 'admin', '!getinfo <uin> (!аська)', 'Запрос информации о пользователе у ICQ');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('27', 'room', '!room <id> (!комната !к)', 'Перейти в другую комнату');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('28', 'kickhist', '!kickhist (!кикист)', 'История киков');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('29', 'all', '!adm <text>  (!админу)', 'Оставить сообщение админу');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('30', 'ban', '!banhist (!банист)', 'История банов');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('31', 'all', '+aa (!все)', 'Список пользователей в чате');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('32', 'all', '!lroom (!комнаты)', 'Вывод списка комнат в чате');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('33', 'wroom', '!crroom <id> <text> (!создкомн)', 'Создать новую комнату');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('34', 'wroom', '!chroom <id> <text> (!измкомн)', 'Изменить название комнаты');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('35', 'qroup_time', '!таймгруппа <id> <day> <group>', 'Назначить пользователю временную группу');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('36', 'group_time', '!таймлист', 'Список пользователей в временной  группе');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('37', 'zakhist', '!закрытые', 'Вывод списка закрытых пользователей');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('38', 'banroom', '!запереть <id> <time> <r>', 'Закрыть пользователя в комнате');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('39', 'all', '!бутылочка', 'Играть в бутылочку');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('40', 'fraza', '!фраза <text>', 'Добавить фразу для бутылочки');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('41', 'admlist', '!админы', 'Список администрации online');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('42', 'chnick', '!chnick <id> <nick> (!смник)', 'Сменить ник пользователю');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('43', 'all', '!повысить <id>', 'Повысить рейтинг пользователю');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('44', 'all', '!понизить <id>', 'Понизить рейтинг пользователю');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('45', 'setpass', '!setpass <pass> (!пароль)', 'Установить пароль на комнату');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('46', 'admlist', '!адмлист', 'Список адм сообщений');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('47', 'robmsg', '!робмсг <text>', 'Добавить фразу для админ бота');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('48', 'xst', '!хстатус <n> <text>', 'Сменить х-статус чата');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('49', 'status_user', '!статус <""> <text>', 'Установить личный статус');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('50', 'ubanhist', '!разбанлист', 'Список разбанов');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('51', 'wroom', '!удалить <id>', 'Удалить комнату');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('52', 'invitation', '!пригласитьид <id> <text>', 'Пригласить в чат по иду');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('53', 'invitation', '!пригласитьуин <uin> <text>', 'Пригласить в чат по уину');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('54', 'allroom_message', '!везде <text>', 'Отправить сообщение во все комнаты');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('55', 'deladmmsg', '!деладм', 'Очится адм сообщений');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('56', 'all', '!данные', 'Заполнить информацию о себе');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('57', 'all', '!личное <id>', 'Просмотреть информацию о пользователе');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('58', 'setclan', '!аддклан', 'Создать клан');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('59', 'setclan', '!делклан', 'Удалить клан');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('60', 'all', '!кланхелп', 'Справка по клан командам');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('61', 'all', '!ларек', 'Список допустимых подарков для покупки');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('62', 'gift', '!добподарок <name> <$>', 'Добавить подарок в список покупок');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('63', 'all', '!вещи', 'Вещи купленные в ларьке');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('64', 'all', '!подарить <id> <id_gift> <text>', 'Подарить пользователю подарок');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('65', 'all', '!всеподарки <id>', 'Все подарки подаренные пользователю');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('66', 'gift', '!делподарок <id>', 'Удалить подарок из списка покупок');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('67', 'all', '!магазин', 'Магазин чата');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('68', 'shophist', '!учет', 'История покупок в магазине');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('69', 'all', '!голосование <id>', 'Открыть голосование на кик пользователя');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('70', 'all', '!делдруг <id>', 'Удалить друга');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('71', 'all', '!заявка <id>', 'Подать заявку на добавление в друзья');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('72', 'all', '!подтвердить <id>', 'Подтвердить заявку');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('73', 'all', '!отклонить <id>', 'Отклонить заявку');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('74', 'all', '!заявки', 'Вывод листинга всех заявок');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('75', 'all', '!аллдруг <id>', 'Вывод всех друзей пользователя');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('76', 'Wall', '!аддстена <text>', 'Добавить сообщение на стену');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('77', 'WallDel', '!делстена', 'Очистить стену');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('78', 'Wall', '!стена', 'Просмотр стены');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('79', 'infbot', '!создинф <text>', 'Создать информацию для админ бота');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('80', 'infbot', '!удинф <id>', 'Удалить информацию');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('81', 'infbot', '!листинф', 'Список всей информации для админ бота');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('82', 'all', '!about (!оботе)', 'Информация об авторе бота');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('83', 'wedding', '!свадьба <id> <id>', 'Обвенчать пользователей');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('84', 'wedding', '!развод <id> <id>', 'Развести пользователей');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('85', 'all', '!отдать <id> <id>', 'Передача баллов другому пользователю');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('86', 'chstatus', '!chstatus (!cмстатус) <id> <text>', 'Смена статуса другому пользователю');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('87', 'chid', '!измид <id> <id>', 'Менять ид другому пользователю');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('88', 'invise', '!спрятаться', 'Спрятаться в чате');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('89', 'invise', '!показаться', 'Показаться в чате');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('90', 'listinvise', '!скрылись', 'Листинг скрытых пользователей');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('91', 'all', '!автосалон', 'Купить авто');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('92', 'all', '!недвижимость', 'Купить дом');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('93', 'all', '!бутик', 'Купить одежду');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('94', 'all', '!зоо', 'Купить животное');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('95', 'gift', '!делтовар <name_table> <id>', 'Удалить товар из список покупок');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('96', 'gift', '!аддтовар <name_table> <$> <name>', 'Добавить товар в список покупок');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('97', 'all', '!уин <id>', 'Дать пользователю свой уин');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('98', 'restart', '!перезагрузить', 'Перезагрузить сервис');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('99', 'usermessages', '!юзеру <id/uin>', 'Отправить сообщение пользователю');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('100', 'uchat', '!затащить <id>', 'Затащить пользователя в чат');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('101', 'bot_messages', '!бот', 'Сообщение за бота');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('102', 'reg_user', '!регистрировать <uin> <nick>', 'Зарегистрировать пользователя');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('103', 'all', '!казино', 'Играть в игру казино');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('104', 'all', '!рулетка', 'Играть в игру рулетка');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('105', 'advertisement_work', '!аддреклама <text>', 'Добавить рекламу');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('106', 'advertisement_work', '!делреклама <id> ', 'Удалить рекламу');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('107', 'advertisement_work', '!листреклама', 'Листинг рекламы');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('108', 'getball', '!датьбалы <id> <ball>', 'Дать балы пользователю');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('109', 'ballwork', '!забратьбалы <id> <ball>', 'забрать балы пользователю');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('110', 'all', '!зарплата', 'Получить зарплату');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('111', 'notice_work', '!предупреждение <id> <text>', 'Выписать предупреждение пользователю');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('112', 'notice_work', '!нотист <id>', 'Листинг предупреждений пользователя');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('113', 'all', '!число', 'Играть в число');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('114', 'admalllist', '!всеадмины', 'Список всех админов');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('115', 'personal_room', '!лкомната', 'Назначить пользоватлю лчную комнату');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('116', 'personal_room', '!рлкомната', 'Убрать у пользоватля личную комнату');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('117', 'all', '!пригласить', 'Пригласить пользователя в личную комнату');
INSERT INTO `help` ( `id` , `auth` , `command` , `info` ) VALUES ('118', 'textinout', '!аддтекст', 'Добавить текст при вход и выходе');