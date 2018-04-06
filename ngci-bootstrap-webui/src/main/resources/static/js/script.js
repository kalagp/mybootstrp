var open = window.XMLHttpRequest.prototype.open; 
var send = window.XMLHttpRequest.prototype.send;

var o = XMLHttpRequest.prototype.open;
XMLHttpRequest.prototype.open = function(){
  var res = o.apply(this, arguments);
  this.setRequestHeader('X-test-header', "test test test");
  return res;
}

window.onload = function()
{
    console.log(getCookie("access_token"));
}

function doLogin() 
{
    var xhttp = new XMLHttpRequest();
    status = 0;
    xhttp.onreadystatechange = function() 
    {   
        if (this.readyState == 4) 
        {
            status = this.status;
        }
        if (this.readyState == 4 && this.status == 200) 
        {
          document.getElementById("status").innerHTML = this.responseText;
          var json = JSON.parse(this.responseText);
    console.log(json);
    console.log("Token : " + json.access_token);
    console.log("Expires : " + json.expires_in);
    console.log(json.access_token);
    setCookie("access_token", json.access_token, json.expires_in);
    setCookie("validity", json.expires_in, json.expires_in);
        }
        else if (this.readyState == 4) 
        {
          document.getElementById("status").innerHTML = "Sorry status = " + status;
        }
    };

    var uname = document.getElementById("uname").value;
    var passwd = document.getElementById("passwd").value;

    xhttp.open("POST", "http://localhost:8100/oauth/token", true);
    xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xhttp.setRequestHeader("Authorization", "Basic " + btoa("ClientIdUI:secret"));
    xhttp.send("grant_type=password&username=" + uname + "&password=" + passwd );
}


function getCookie(name) 
{
    var v = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
    return v ? v[2] : null;
}

function setCookie(name, value, expiry=60) 
{
    var d = new Date;
    d.setTime(d.getTime() + 1000*expiry);
    document.cookie = name + "=" + value + ";path=/;expires=" + d.toGMTString();
}

function deleteCookie(name) 
{ 
    setCookie(name, '', -1); 
}