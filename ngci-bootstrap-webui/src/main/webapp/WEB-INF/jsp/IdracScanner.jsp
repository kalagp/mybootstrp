<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <script src="/js/jquery-3.3.1.min.js"></script>
  
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
  
  <script>
  var jobResults = [];
  var myTimer;
  
  function getJobDetails(jobId) 
  {
	  $.get("/bootstrap/jobdetails/" + jobId, function(data) {
		  jobResults = data;
		});
	  
	  if(jobResults.progress >= 100)
	  {
		  clearTimeout(myTimer);
	  }
	  console.log(jobResults);

	  if(jobResults.consoleMessages !== "undefined")
	  {
		  console.log("Number of idracs " + jobResults.consoleMessages.length);
		  console.log(jobResults.consoleMessages);
	  }
}

  </script>

  <title>iDRAC Scanner</title>
</head>
<body>

${pageContext.request.requestURL}
Job ID: ${jobid}

<div id="div1"><h2>Let jQuery AJAX Change This Text</h2></div>
<div id="text"></div>
<%
    if(request.getAttribute("jobid") != null)
    {
        Long jobId = Long.parseLong(request.getAttribute("jobid").toString());
%>
<script>
	$(document).ready(function() {
		myTimer = setInterval('getJobDetails(${jobid})', 5000);
	})
</script>	
<%        
    }
%>


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
        <tr>
          <td>row 1, cell 1</td>
          <td>row 1, cell 2</td>
          <td>row 1, cell 2</td>
        </tr>
      </tbody>
    </table>
  </div>
  
  <form action='/bootstrap/startjob' method='post'>
	<input type="hidden" name="job" value="com.dell.cpsd.bootstrap.classjobs.idracdiscovery.DiscoverIdrac">
	<input type="hidden" name="redirecturl" value="/bootstrap/scan">
    IP Addresses : <input name="ipaddress" type="text" value="10.234.122.11-12"><br>
    <input type='submit' value='Start Scan'>
  </form>
  
  <%--
  start job,
  get job detail /jobdetails/{jobid}
   --%>
</body>
</html>