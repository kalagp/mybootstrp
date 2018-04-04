var app = angular.module('myApp', ["ngResource","ngRoute","ngCookies"]);

app.controller('mainCtrl', function($scope,$resource,$http,$httpParamSerializer,$cookies,$timeout)
{
    if($cookies.get("access_token"))
    {
        console.log("there is access token");
        console.log("access_token :" + $cookies.get("access_token") );
        console.log("refresh_token :" + $cookies.get("refresh_token") );
        console.log("validity :" + $cookies.get("validity") );
        $http.defaults.headers.common.Authorization= 'Bearer ' + $cookies.get("access_token");
    }
    else
    {
        //obtainAccessToken($scope.refreshData);
        console.log("there is no access token");
        console.log("access_token :" + $cookies.get("access_token") );
        console.log("refresh_token :" + $cookies.get("refresh_token") );
        console.log("validity :" + $cookies.get("validity") );
//        window.location.href = "login.html";
    }
    
    
    $scope.login = function() 
    {   
        console.log("Login called");
        $scope.error = "";
        //obtainAccessToken($scope.loginData);
        var loginData = {grant_type:"password", username: "", password: ""};
        if ($scope.loginData.username != null)
        {
            loginData.username = $scope.loginData.username;
            loginData.password = $scope.loginData.password;
        }
    
        var req = 
        {
            method: 'POST',
            url: "oauth/token",
            headers: 
            {
                "Content-type": "application/x-www-form-urlencoded; charset=utf-8",
                "Authorization": "Basic " + btoa("ClientIdUI:secret")
            },
            data: $httpParamSerializer(loginData)
        }
        $http(req).then
        (
            function success(response)
            {
                console.log(response.data.access_token);
                $http.defaults.headers.common.Authorization= 'Bearer ' + response.data.access_token;
                var expireDate = new Date (new Date().getTime() + (1000 * response.data.expires_in));
                $cookies.put("access_token", response.data.access_token, {'expires': expireDate});
                $cookies.put("refresh_token", response.data.refresh_token, {'expires': expireDate});
                $cookies.put("validity", response.data.expires_in);

                $scope.error = "Login Successful";
                
                window.location.href="showrest.html";
            },
            function error(response)
            {
                $scope.loginData.username = "";
                $scope.loginData.password = "";
                $scope.error = "Login incorrect";
                console.log(response);
                console.log(response.statusText);
                window.location.href = "login.html";
            }
        );
    }


    function refreshAccessToken()
    {
        var refreshData = {grant_type:"refresh_token", refresh_token: ""};
        console.log("Refresh data");

        if($cookies.get("refresh_token"))
        {
            refreshData.refresh_token = $cookies.get("refresh_token");
        }
   
        var req = 
        {
            method: 'POST',
            url: "oauth/token",
            headers: 
            {
                "Content-type": "application/x-www-form-urlencoded; charset=utf-8",
                "Authorization": "Basic " + btoa("ClientIdUI:secret")
            },
            data: $httpParamSerializer(refreshData)
        }
        $http(req).then
        (
            function success(response)
            {
                console.log(response);
                console.log(response.data.access_token);
                $http.defaults.headers.common.Authorization= 'Bearer ' + response.data.access_token;
                var expireDate = new Date (new Date().getTime() + (1000 * response.data.expires_in));
                $cookies.put("access_token", response.data.access_token, {'expires': expireDate});
                $cookies.put("refresh_token", response.data.refresh_token, {'expires': expireDate});
                $cookies.put("validity", response.data.expires_in);
            },
            function error(response)
            {
                console.log(response);
                console.log(response.statusText);
                console.log("error");
                window.location.href = "login.html";
            }
        );
    }
 

    $scope.getData = function() 
    {   
        console.log("Get data called");
        console.log("access_token :" + $cookies.get("access_token") );
        console.log($scope.restapi.place);
        refreshAccessToken();

        $scope.error = "";
    
        var req = 
        {
            method: 'GET',
            url: $scope.restapi.place
            //data: $httpParamSerializer(loginData)
        }
        $http(req).then
        (
            function success(response)
            {
                $scope.dataResult = JSON.stringify(response,null,"    ");
                //$scope.dataResult = data;

                $scope.error = "Login Successful";
            },
            function error(response)
            {
                $scope.dataResult = "";
                $scope.error = response.statusText;
                console.log("response");
                window.location.href = "login.html";
            }
        );
    }

});