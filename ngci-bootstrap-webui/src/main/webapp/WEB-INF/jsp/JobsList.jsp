<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="java.io.*,java.util.*,java.sql.*"%>
<%@ page import="javax.servlet.http.*,javax.servlet.*"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Jobs</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="/bootstrap/css/bootstrap-3.3.7.css">
  <script src="/bootstrap/js/jquery-3.3.1.min.js"></script>
  <script src="/bootstrap/js/bootstrap-3.3.7.min.js"></script>

</head>
<body>
 <h1>Job List</h1>
<hr>
<table>
	<tr>
		<th>JobID</th>
		<th>Name</th>
		<th>progress</th>
		<th>Log Message</th>
		<th>Console Output</th>
	</tr>
<c:forEach items="${jobList}" var="job">
	<tr>
		<td>${job.value.jobId}</td>
		<td>${job.value.jobName}</td>
		<td><progress value="${job.value.progress}" max="100"></progress>${job.value.progress}</td>
		<td>
			<ol>
		    <c:forEach items="${job.value.messages}" var="message">
        		<li>${message}</li>
            </c:forEach>
            </ol>
		</td>
		<td>
			<ol>
		    <c:forEach items="${job.value.consoleMessages}" var="consolemessage">
        		<li>${consolemessage}</li>
            </c:forEach>
            </ol>
		</td>
	</tr>
</c:forEach>
</table>

<hr>

</body>
</html>