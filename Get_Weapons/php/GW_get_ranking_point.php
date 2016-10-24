<?php
$id = addslashes($_POST['id']);
$connect = mysqli_connect("localhost", "client", "1234", "GW");
if(mysqli_connect_errno()) {
	echo "{\"rank_point\":-1}";
}
if($result = mysqli_query($connect, "select rank_point from userinfo where id = '$id'")) {
	$row = mysqli_fetch_object($result);
	echo urldecode(json_encode($row));
}
else {
	echo "{\"rank_point\":-2}";
}
mysqli_close($connect);
?>
