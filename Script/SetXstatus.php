<?php

$host = "localhost:3306";
$username = "test";
$password = "test";
$name = "test";

// Конект с бд
$connect = mysql_connect($host, $username, $password )or 
exit( "It was not possible to incorporate to server. ".mysql_error() );
$db = mysql_select_db( $name, $connect )or
exit( "It was not possible to select a DB. ".mysql_error() );
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
  print "Statuses are added<br>";
// Отключение от бд
mysql_close( $connect );  		
?>
 