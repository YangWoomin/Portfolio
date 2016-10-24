<?php
$query = stripslashes($_POST['query']);

$connect = mysqli_connect("localhost", "client", "1234", "GW");
if(mysqli_connect_errno()) {
	echo "{\"status\":\"SEV_ERR\"}";
}
$result = mysqli_query($connect, $query);
if($result) {
	echo "{\"status\":\"OK\"}";
}
else {
	echo "{\"status\":\"NO\"}";
}
mysqli_close($connect);
?>
