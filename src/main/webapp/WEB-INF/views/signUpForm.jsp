
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
            <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcT0uh4h5i5PLv0UDZsykVCyC-2XvVnlQ2IJzxnHVHwO5G9-C4oZ" id="icon" alt="User Icon" />
        </div>


        <!-- Login Form -->
        <form method="post" action="/erc721/ownerOf" enctype="multipart/form-data">
            <input type="text" id="tokenId" class="fadeIn second" name="tokenId" placeholder="tokenId">
            <input type="file" id="certfile" class="fadeIn third" name="certfile" placeholder="certfile"><br>
            <input type="password" id="certiPassword" class="fadeIn third" name="certiPassword" placeholder="certiPassword"><br>
            <input type="submit" class="fadeIn fourth" value="Register">
        </form>

        <div id="formFooter">
            <a class="underlineHover" href="/assets/index">Login</a>
        </div>

    </div>
</div>



</body>

<script src="${ctx}/js/jquery-min.js"></script>
</html>
