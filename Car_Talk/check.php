<?php
$mac = addslashes($_POST['mac']);
$connect = mysqli_connect("localhost", "client", "1234", "embedded") or die ("Connecting to database failed.");
$result = mysqli_query($connect, "select id from usermac where mac = '$mac'");
mysqli_data_seek($result, 0);
$record = mysqli_fetch_array($result);
if($record != null) {
	echo $record[0];
}
else {
	echo "NO";
}
mysqli_close($connect);
?>
