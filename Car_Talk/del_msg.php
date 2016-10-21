<?php
$id = addslashes($_POST['id']);
$msg = addslashes($_POST['msg']);
$connect = mysqli_connect("localhost", "client", "1234", "embedded") or die ("Connecting to database failed.");
$result = mysqli_query($connect, "delete from usermsg where id = '$id' and msg = '$msg'");
if($result) {
	echo "{\"status\":\"DELETE_OK\"}";
}
else {
	echo "{\"status\":\"DELETE_NO\"}";
}
mysqli_close($connect);
?>
