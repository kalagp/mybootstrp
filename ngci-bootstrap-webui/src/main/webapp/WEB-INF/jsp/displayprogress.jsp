<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <link rel="stylesheet" href="/bootstrap/css/bootstrap-3.3.7.css">
  <script src="/bootstrap/js/jquery-3.3.1.min.js"></script>
  <script src="/bootstrap/js/bootstrap-3.3.7.min.js"></script>
<title>Welcome</title>
</head>
<body>

	<div class="container">
		<h2>JOB Id ${jobid}</h2>
		<h2>progress ${progress}</h2>
		<h2>messages ${messages}</h2>
        <progress value="${progress}" max="100"></progress>
	</div>

</body>
</html>