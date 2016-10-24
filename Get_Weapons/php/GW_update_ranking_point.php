<?php
$id = addslashes($_POST['id']);
$point = addslashes($_POST['point']);
$connect = mysqli_connect("localhost", "client", "1234", "GW");
if(mysqli_connect_errno()) {
	echo "{\"status\":\"SEV_ERR\"}";
}
$rankPointResult = mysqli_query($connect, "select rank_point from userinfo where id = '$id'");
$row = mysqli_fetch_row($rankPointResult);
$ranking;
if($row) {
	$ranking = $row[0];
}
else {
	echo "{\"status\":\"NO\"}";
}	
$result = mysqli_query($connect, "update userinfo set rank_point = $point + $ranking where id = '$id'");
if($result) {
	echo "{\"status\":\"OK\"}";
}
else {
	echo "{\"status\":\"NO\"}";
}
mysqli_close($connect);
?>
