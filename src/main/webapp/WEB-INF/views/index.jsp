<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- <%@ include file="common/taglibs.jsp" %> --%>


<!DOCTYPE html>
<html lang="ko" class="high">
<head>
    <link href="../../common/index_style.css" rel="stylesheet" type="text/css"></link>
    <title>PosLedger Assets Application</title>
</head>

<body>

<div class="wrapper fadeInDown">
    <div id="formContent">
        <!-- Tabs Titles -->

        <div class="fadeIn first">
            <img src="https://t1.daumcdn.net/cfile/tistory/194B6A384F6922CB11" id="icon" alt="User Icon" />
        </div>

        <form method="post" action="/oauth/token" enctype="multipart/form-data">
            <input type="file" id="certfile" class="fadeIn third" name="certfile" placeholder="certfile"><br>
            <input type="password" id="certiPassword" class="fadeIn third" name="certiPassword" placeholder="certiPassword"><br>
            <input type="submit" class="fadeIn fourth" value="Log In">
        </form>

        <div id="formFooter">
            <a class="underlineHover" href="/signUpForm">Sign Up</a>
        </div>

    </div>
</div>



</body>

<script src="${ctx}/js/jquery-min.js"></script>
</html>
