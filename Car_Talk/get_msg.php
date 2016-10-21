<?php
$id = addslashes($_POST['id']);
$connect = mysqli_connect("localhost", "client", "1234", "embedded") or die ("Connecting to database failed.");
$result = mysqli_query($connect, "select msg from usermsg where id = '$id'");
$reply = array();
while($row = mysqli_fetch_object($result)) {
	$reply[] = $row;
}
echo urldecode(json_encode($reply));
mysqli_close($connect);
?>
