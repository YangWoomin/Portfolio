<?php
$id = addslashes($_POST['id']);
$connect = mysqli_connect("localhost", "client", "1234", "GW");
if(mysqli_connect_errno()) {
	echo "[:]";
}

$rowNumResult = mysqli_query($connect, "select * from userinfo");
$rowNum = mysqli_num_rows($rowNumResult);
$reply = array();
if($rowNum < 22) {
	$result1 = mysqli_query($connect, "select id, rank_point, @vRank := @vRank + 1 as ranking from userinfo as p, (select @vRank := 0) as r order by rank_point desc");
	while($row1 = mysqli_fetch_object($result1)) {
		$reply[] = $row1;
	}
}
else {
	$rankingResult = mysqli_query($connect, "select ranking from (select id, rank_point, @vRank := @vRank + 1 as ranking from userinfo as p, (select @vRank := 0) as r order by rank_point desc) as cnt where id = '$id'");
	$rankingRow = mysqli_fetch_row($rankingResult);
	$ranking = $rankingRow[0];
	$start;
	$end;
	$diff = 0;
	if($ranking - 10 <= 1) {
		$start = 1;
		$diff = 11 - $ranking;
	}
	else {
		$start = $ranking - 10;
	}
	if($diff != 0) {
		$end = $ranking + $diff + 10;
	}
	else {
		if($ranking + 10 > $rowNum) {
			$end = $rowNum;
			$start = $start - (10 - ($rowNum - $ranking));
		}
		else {
			$end = $ranking + 10;
		}
	}
	$result2 = mysqli_query($connect, "select id, rank_point, ranking from (select id, rank_point, @vRank := @vRank + 1 as ranking from userinfo as p, (select @vRank := 0) as r order by rank_point desc) as cnt where ranking >= $start and ranking <= $end order by ranking asc");
	while($row2 = mysqli_fetch_object($result2)) {
		$reply[] = $row2;
	}
}

echo urldecode(json_encode($reply));
mysqli_close($connect);
?>
