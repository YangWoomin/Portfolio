<?php
$id = addslashes($_POST['id']);
$msg = addslashes($_POST['msg']);
$connect = mysqli_connect("localhost", "client", "1234", "embedded") or die ("Connecting to database failed.");
mysqli_query("SET NAMES UTF8");
$result = mysqli_query($connect, "insert into usermsg values('$id','$msg')");
if($result) {
	echo "{\"status\":\"INSERT_OK\"}";
}
else {
	echo "{\"status\":\"INSERT_NO\"}";
}
mysqli_close($connect);
?>
