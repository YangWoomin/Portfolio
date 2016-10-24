<?php
$query = addslashes($_POST['query']);
$connect = mysqli_connect("localhost", "client", "1234", "GW");
if(mysqli_connect_errno()) {
	echo "[:]";
}

$reply = array();
$result = mysqli_query($connect, $query);
while($row = mysqli_fetch_object($result)) {
	$reply[] = $row;
}

echo urldecode(json_encode($reply));
mysqli_close($connect);
?>
