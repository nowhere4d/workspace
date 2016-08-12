<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html class="no-js">
  <head>
    <meta charset="utf-8">
    <title></title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width">
    <!-- Place favicon.ico and apple-touch-icon.png in the root directory -->
    <!-- build:css(.) styles/vendor.css -->
    <!-- bower:css -->
    <link rel="stylesheet" href="webjars/bootstrap/3.3.6/dist/css/bootstrap.min.css" />
    <!-- endbower -->
    <!-- endbuild -->
    
    <!-- build:css(.tmp) styles/main.css -->
    <link rel="stylesheet" href="styles/user.css">
    <link rel="stylesheet" href="styles/main.css">
    <link rel="stylesheet" href="styles/sb-admin-2.css">
    <link rel="stylesheet" href="styles/timeline.css">
    <link rel="stylesheet" href="webjars/metisMenu/1.1.3/dist/metisMenu.min.css">
    <link rel="stylesheet" href="webjars/angular-loading-bar/0.7.1/build/loading-bar.min.css">
    <link rel="stylesheet" href="webjars/font-awesome/4.3.0/css/font-awesome.min.css" type="text/css">
    <!-- endbuild -->
    
    <!-- build:js(.) scripts/vendor.js -->
    <!-- bower:js -->
    <script src="webjars/jquery/2.2.4/dist/jquery.min.js"></script>
    <script src="webjars/angular/1.2.28/angular.min.js"></script>
    <script src="webjars/bootstrap/3.3.6/dist/js/bootstrap.min.js"></script>
    <script src="webjars/angular-ui-router/0.2.18/release/angular-ui-router.min.js"></script>
    <script src="webjars/json3/3.3.2/lib/json3.min.js"></script>
    <script src="webjars/oclazyload/1.0.9/dist/ocLazyLoad.min.js"></script>
    <script src="webjars/angular-loading-bar/0.7.1/build/loading-bar.min.js"></script>
    <script src="webjars/angular-bootstrap/0.12.1/ui-bootstrap-tpls.min.js"></script>
    <script src="webjars/metisMenu/1.1.3/dist/metisMenu.min.js"></script>
    <script src="webjars/Chart.js/1.0.2/Chart.min.js"></script>
    <!-- endbower -->
    <!-- endbuild -->
    
    <!-- build:js({.tmp,app}) scripts/scripts.js -->
        <script src="scripts/app.js"></script>
        <script src="js/sb-admin-2.js"></script>
    <!-- endbuild -->



    <script>
       (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
       (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
       m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
       })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
       ga('create', 'UA-XXXXX-X');
       ga('send', 'pageview');
    </script>
    <!-- Custom CSS -->

    <!-- Custom Fonts -->

    <!-- Morris Charts CSS -->
    <!-- <link href="styles/morrisjs/morris.css" rel="stylesheet"> -->


    </head>
   
    <body>

    <div ng-app="sbAdminApp">

        <div ui-view></div>

    </div>

    </body>

</html>