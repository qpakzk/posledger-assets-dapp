<%-- <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> --%>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- <%@ include file="common/taglibs.jsp" %> --%>

<!DOCTYPE html>
<html lang="ko" class="high">
<head>
    <title>PosLedger Assets Application</title>

    <link href="../../common/bootstrap.min.css" rel="stylesheet" type="text/css"></link>
    <script src="https://unpkg.com/sweetalert/dist/sweetalert.min.js"></script>

    <!-- Custom styles for this template -->
    <link href="../../common/shop-item.css" rel="stylesheet">
</head>

<body>

<!--<h2>Your token is ${accessToken}</h2>-->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
    <div class="container">
        <a class="navbar-brand" href="#">POSTECH</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarResponsive">
            <ul class="navbar-nav ml-auto">
                <li class="nav-item active">
                    <a class="nav-link" href="/main">Home
                        <span class="sr-only">(current)</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/index">Login</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/admin">토큰 타입 기능</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/standard">표준 기능</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/extension">확장 기능</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<!-- Page Content -->
<div class="container">

    <div class="row">

        <div class="col-lg-3">
            <h1 class="my-4">Signature Service</h1>
            <div class="list-group">
                <a href="/main" class="list-group-item">Make signature</a>
                <a href="/mysign?ownerKey=${sessionUser}" class="list-group-item">My Signature</a>
                <a href="/addUser" class="list-group-item"l>Upload File</a>
                <a href="#" class="list-group-item active">My Document</a>
            </div>
        </div>
        <!-- /.col-lg-3 -->

        <div class="col-lg-7">
            <div class="card card-outline-secondary my-4" style="width:800px">
                <div class="card-header">
                    <h1> Document List</h1>
                </div>
                <div class="card-body">
                    <%
                        //List<User_Doc> docList = (List<User_Doc>)request.getAttribute("docList");
                        //User_Doc doc;

                        String docList[] = (String[])request.getAttribute("docIdList");
                        String docPathList[] = (String[])request.getAttribute("docPathList");
                        String docNum[] = (String[])request.getAttribute("docNumList");
                        String tokenId[] = (String[])request.getAttribute("tokenIdList");
                        String sigStatus[] = (String[])request.getAttribute("sigStatus");
                        String ownerKey = (String)request.getAttribute("ownerKey");
                        String token="";
                        String sigProcess="";
                        String docid[] = new String[docList.length];

                        String queryDoc="";
                        int i=0;
                        for(i=0; i<docid.length; i++) {
                            docid[i] = "<a href=/mydoc?ownerKey=" + ownerKey + "&docid=" + docList[i] + "&docnum=" + docNum[i] + "&tokenid=" + tokenId[i] +">" + docPathList[i] + "</a>";
                            queryDoc = "<a href=/queryDoc?docid=" + docList[i] + "&docnum=" + docNum[i] + "&tokenid=" + tokenId[i] + ">" + "- Final Document " + "</a>";
                            if(sigStatus[i].equals("true"))
                                sigProcess= " <button type='button' class='btn btn-success'  style='width: 30pt; height:28pt; float:right;' onclick=checkStatus("+tokenId[i]+")>O</button> ";
                            else
                                sigProcess= " <button type='button' class='btn btn-danger'  style='width: 30pt; height:28pt; float:right;' onclick=checkStatus("+tokenId[i]+")>X</button> ";
                            token = " <input type=submit value='√' class='btn btn-outline-info' style='background-image:url(https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSY5Mu3vrHZi-N1ntwu6F0lTYc2IQekwho9WjK1gl5s_BxWwhI); style='width: 2pt; height:20pt; float:right;' onclick=checkStatus("+tokenId[i]+")> ";

                    %>
                    <table width="750px">
                        <tr>
                            <td>
                                <%=docid[i]%>
                                <%=queryDoc%>
                            </td>
                            <td align="right">
                                <%--<%=token%>&nbsp--%>
                                <%=sigProcess%>
                            </td>
                        </tr>
                    </table>
                    <hr>
                    <%
                        }
                    %>
                    <hr>
<%--                    <a href="#" class="btn btn-success">Leave a Review</a>--%>
                </div>
                <div class="card card-outline-secondary my-4">
                    <div class="card-header">

                    </div>
                    <div class="card-body" align="left">
                        <table width="750">
                            <tr>
                                <td>
                                    Page &nbsp;&nbsp;&nbsp;<input type="text" style="width : 100px" id="firstValue" class="btn-outline-info"> - <input type="text" style="width : 100px" id="secondValue" class="btn-outline-info"> tokenId<input type="text" style="width : 100px" id="docTokenId" class="btn-outline-info"> &nbsp
                                </td>
                                <td align="right">
                                    <input type="submit" class="btn btn-success"  value="divide" onclick="divide(this)">
                                    <input type="hidden" id="ownerKey" value="${sessionUser}">
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    tokenId <input type="text" style="width : 100px" id="docTokenIdForTransferFrom" class="btn-outline-info">
                                    newOwner <input type="text" style="width : 100px" id="newOwnerId" class="btn-outline-info"> &nbsp
                                </td>
                                <td align="right">
                                    <input type="submit" class="btn btn-success"  value="transferFrom" onclick="transferFrom(this)">
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    Signers <input type="text" style="width : 100px" id="signersOne" class="btn-outline-info"> &nbsp <input type="text" style="width : 100px" id="signersTwo" class="btn-outline-info"> tokenId <input type="text" style="width : 100px" id="docTokenIdForUpdate" class="btn-outline-info"> &nbsp index <input type="text" style="width : 100px" id="index" class="btn-outline-info">
                                </td>
                                <td align="right">
                                    &nbsp <input type="submit" class="btn btn-success"  value="update" onclick="update(this)">
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>
            <!-- /.card -->

        </div>
        <!-- /.col-lg-9 -->
    </div>
</div>

<%--
<c:forEach items="${docList}" var="docList">
    <a href="">${list}</a>
</c:forEach>
--%>
</body>
<script>

    function checkStatus(tokenId) {

        $.ajax({
            type: "POST",
            url: "/checkStatus",
            data: {
                "tokenId": tokenId
                //"strImg": dataURL
                //"test": "test string"
            },
            //dataType: "json",
            success: function (data) {
                if(data[2] == 'true')
                    swal({text: data[1], text: data[0], icon: "success", button: "close",});
                else
                    swal({text: data[1], text: data[0], icon: "error", button: "close",});
            },
            error: function (err) {
                swal("error" + err);
            }
        });
    }

    function divide() {

        ownerKey = document.getElementById("ownerKey").value;
        docTokenId = document.getElementById("docTokenId").value;
        firstValue = document.getElementById("firstValue").value;
        secondValue = document.getElementById("secondValue").value;

        $.ajax({
            type: "POST",
            url: "/divideDoc",
            data: {
                "ownerKey" : ownerKey,
                "docTokenId": docTokenId,
                "firstValue" : firstValue,
                "secondValue" : secondValue
                //"strImg": dataURL
                //"test": "test string"
            },
            //dataType: "json",
            success: function (data) {
                swal({ icon: "success", button: "close"});
            },
            error: function (err) {
                swal("error....." + err);
            }
        });
    }

    function transferFrom() {

        ownerKey = document.getElementById("ownerKey").value;
        newOwnerId = document.getElementById("newOwnerId").value;
        docTokenIdForTransferFrom = document.getElementById("docTokenIdForTransferFrom").value;

        $.ajax({
            type: "POST",
            url: "/transferFromDoc",
            data: {
                "ownerKey" : ownerKey,
                "newOwnerId" : newOwnerId,
                "docTokenIdForTransferFrom": docTokenIdForTransferFrom,
            },
            //dataType: "json",
            success: function (data) {
                swal({ icon: "success", button: "close"});
            },
            error: function (err) {
                swal("error....." + err);
            }
        });
    }

    function update() {

        ownerKey = document.getElementById("ownerKey").value;
        signersOne = document.getElementById("signersOne").value;
        signersTwo = document.getElementById("signersTwo").value;
        docTokenIdForUpdate = document.getElementById("docTokenIdForUpdate").value;
        index = document.getElementById("index").value;

        $.ajax({
            type: "POST",
            url: "/updateDoc",
            data: {
                "ownerKey" : ownerKey,
                "signersOne" : signersOne,
                "signersTwo" : signersTwo,
                "docTokenIdForUpdate": docTokenIdForUpdate,
                "index" : index
            },
            //dataType: "json",
            success: function (data) {
                swal({ icon: "success", button: "close"});
            },
            error: function (err) {
                swal("error....." + err);
            }
        });
    }
</script>
<script src="${ctx}/js/jquery-min.js"></script>

</html>