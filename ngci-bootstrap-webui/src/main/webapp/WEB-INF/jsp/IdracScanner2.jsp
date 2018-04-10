<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <style>
table tbody, table thead
{
    display: block;
}

table tbody 
{
   overflow: auto;
   height: 100px;
}

table 
{
    width: 350px; /* can be dynamic */
    border-collapse: collapse;
}

th
{
    width: 80px;
  background-color: #6699ff;
}

td, th 
{
    border: 1px solid black;
}

tr:nth-child(even) 
{
  background-color: #99ccff;
}
  </style>

  <title>iDRAC Scanner</title>
</head>
<body>

  <div>
    <table>
      <thead>
        <tr>
          <th>IP Address</th>
          <th>DNS Name</th>
          <th>Type</th>
        </tr>
      </thead>
      <tbody>
		<c:forEach items="${idracsdata}" var="idracs">
		    <tr>
			<c:forEach items="${idracs.value}" var="idracdata">
				<td>${idracdata.value}</td>
			</c:forEach>
			</tr>
		</c:forEach>
      </tbody>
    </table>
  </div>
  <br>
  <br>
  
  <form action='/bootstrap/startjob' method='post'>
	<input type="hidden" name="job" value="com.dell.cpsd.bootstrap.classjobs.idracdiscovery.DiscoverIdrac">
	<input type="hidden" name="redirecturl" value="/bootstrap/jobdetails/idracscan">
    IP Addresses : <input name="ipaddress" type="text" value="10.234.122.11-12"><br>
    <input type='submit' value='Start Scan'>
  </form>

</body>
</html>