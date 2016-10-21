<?php
$id = addslashes($_POST['id']);
$pw = addslashes($_POST['pw']);
$connect = mysqli_connect("localhost", "client", "1234", "embedded") or die ("Connecting to database failed.");
$result = mysqli_query($connect, "select * from userinfo where id = '$id' and pw = '$pw'");
mysqli_data_seek($result, 0);
$record = mysqli_fetch_array($result);
if($record != null) {
	echo "{\"status\":\"OK\"}";
}
else {
	echo "{\"status\":\"NO\"}";
}
mysqli_close($connect);
?>
