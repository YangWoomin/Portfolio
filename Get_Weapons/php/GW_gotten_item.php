<?php
$table = addslashes($_POST['table']);
$id = addslashes($_POST['id']);
$item_name = addslashes($_POST['item_name']);

$connect = mysqli_connect("localhost", "client", "1234", "GW");
if(mysqli_connect_errno()) {
	echo "{\"status\":\"SEV_ERR\"}";
}
$itemQuantityResult = mysqli_query($connect, "select quantity from $table where id = '$id' and item_name = '$item_name'");
$row = mysqli_fetch_row($itemQuantityResult);
$quantity;
if($row) {
	$quantity = $row[0];
}
else {
	if($insertResult = mysqli_query($connect, "insert into $table values('$id', '$item_name', 1)")) {
		echo "{\"status\":\"OK\"}";
	}
	else {
		echo "{\"status\":\"NO\"}";
	}
}
$quantity = $quantity + 1;
if($result = mysqli_query($connect, "update $table set quantity = $quantity where id = '$id' and item_name = '$item_name'")) {
	echo "{\"status\":\"OK\"}";
}
else {
	echo "{\"status\":\"NO\"}";
}
mysqli_close($connect);
?>
