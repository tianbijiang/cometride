<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>Welcome to CometRide</title>
  <style>
  body {
    color: #ffffff;
    background-color: #c7c7c7;
    font-family: Arial, sans-serif;
    font-size:14px;
  }
  a {
    color: #38b801;
  }
  .textColumn, .linksColumn {
    padding: 2em;
  }
  .textColumn {
    position: absolute;
    top: 0px;
    right: 50%;
    bottom: 0px;
    left: 0px;

    text-align: right;
    padding-top: 11em;
    background-color: #57e716;
    background-image: -moz-radial-gradient(left top, circle, #a6e589 0%, #57e716 60%);
    background-image: -webkit-gradient(radial, 0 0, 1, 0 0, 500, from(#a6e589), to(#57e716));
  }
  .textColumn p {
    width: 75%;
    float:right;
  }
  .linksColumn {
    position: absolute;
    top:0px;
    right: 0px;
    bottom: 0px;
    left: 50%;

    background-color: #c7c7c7;
  }
  .linksColumn h2 {
    color: #4c4c4c;
  }

  h1 {
    font-size: 500%;
    font-weight: normal;
    margin-bottom: 0em;
  }
  h2 {
    font-size: 200%;
    font-weight: normal;
    margin-bottom: 0em;
  }
  ul {
    padding-left: 1em;
    margin: 0px;
  }
  li {
    margin: 1em 0em;
  }
  </style>
</head>
<body>

  <div class="textColumn">
    <h1>Welcome!</h1>
    <p>This is a test index page for the CometRide deployment system.</p>
  </div>
  
  <div class="linksColumn"> 
    <h2>What's Next?</h2>
    <p>You can test out the endpoint served up from a Jersey app by appending '/example' to the end of this url. Really. Go try it now!
	<br>
	<br>
	<p>If you'd like to test out the DB connection, navigate to "/example/dbtest".</p>
  </div>

</body>
</html>
