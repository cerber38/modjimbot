<?php
/*
   CREATE TABLE `xstatus` (
  `id` int(11) NOT NULL,
  `number` int(11) NOT NULL,
  `text` text(300) default NULL,
  `type` int(11) NOT NULL,
  PRIMARY KEY  (`id`)
  );
*/

$host = "localhost:3306"; // Хост БД
$username = "test";// Имя пользователя
$password = "test";// Пароль пользователя
$name = "test";// Имя БД

// Конект с бд
$connect = mysql_connect($host, $username, $password )or 
exit( "Не удалось соединиться с сервером. ".mysql_error() );
$db = mysql_select_db( $name, $connect )or
exit( "Не удалось выбрать БД. ".mysql_error() );
  $file = file("./status.txt");
  $count = count($file);

 for ($i = 0; $i < $count; $i++)
  {
  $tx = explode("|", $file[$i]);
  $qwerty=mysql_query("INSERT INTO `xstatus` ( `id` , `number` , `text` , `type` ) VALUES ('".$i."',
  '" . mysql_real_escape_string(trim($tx[0])) . "',
  '" . mysql_real_escape_string(trim($tx[1])) . "',
  '0');");
  if(!$qwerty) echo"ERROR ".mysql_error();
  }
  print "Статусы добавлены<br>";
// Отключение от бд
mysql_close( $connect );  		
?>
 