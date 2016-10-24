<?php
$id = addslashes($_POST['id']);
$connect = mysqli_connect("localhost", "client", "1234", "GW");
if(mysqli_connect_errno()) {
	echo "[:]";
}

$result = mysqli_query($connect, "select item_name, quantity from costumes where id = '$id'");
$reply = array();
while($row = mysqli_fetch_object($result)) {
	$reply[] = $row;
}
echo urldecode(json_encode($reply));
mysqli_close($connect);
?>
