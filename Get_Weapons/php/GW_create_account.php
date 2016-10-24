<?php
$id = addslashes($_POST['id']);
$connect = mysqli_connect("localhost", "client", "1234", "GW");
if(mysqli_connect_errno()) {
	echo "{\"status\":\"SEV_ERR\"}";
}
if($result = mysqli_query($connect, "insert into userinfo values('$id', 0)")) {
	echo "{\"status\":\"OK\"}";
}
else {
	echo "{\"status\":\"NO\"}";
}
mysqli_close($connect);
?>
