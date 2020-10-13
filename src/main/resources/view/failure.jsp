<!DOCTYPE html>
<html>
<head>
<title>Today's date</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<script>
    function pageRedirect() {
    	var host=window.location.hostname
        window.location.replace("http://"+host+"/realestateui/#/main");
    }      
    setTimeout("pageRedirect()", 2000);
</script>
<body>

	<div class="outer" style="box-shadow: 3px 3px 5px 6px #ccc; width: 37%; margin: 6% auto;">
		<a href="" class="exitStatus" style="float: right;margin-right: 5%;">Exit</a>
		<div class="innerStyle" style="padding: 15%;">
		<div class="title" style="text-align: center;">Payment Failed</div>
		<div class="inner" style="text-align: center;padding-bottom: 5%;">Your payment was Failed</div>
		</div>

	</div>
</body>
</html>