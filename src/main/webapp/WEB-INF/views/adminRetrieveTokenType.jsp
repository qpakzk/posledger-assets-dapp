<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- <%@ include file="../../WEB-INF/views/common/taglibs.jsp" %> -->
<%-- <%@ include file="/src/main/webapp/js/jquery-min.js" %>--%>

<!DOCTYPE html>
<html lang="ko" class="high">
<head>
    <title>PosLedger Assets Application</title>
    <meta charset="UTF-8">

    <link href="../../common/bootstrap.min.css" rel="stylesheet" type="text/css"></link>
    <script src="https://unpkg.com/sweetalert/dist/sweetalert.min.js"></script>
    <!-- Custom styles for this template -->
    <link href="../../common/shop-item.css" rel="stylesheet">
</head>
<body onload="init()">


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

<div class="container">

    <div class="row">

        <div class="col-lg-3">
            <h1 class="my-4">Token Type</h1>
            <div class="list-group">
                <a href="/admin" class="list-group-item ">enrollTokenType</a>
                <a href="/adminTokenTypesOf" class="list-group-item">tokenTypesOf</a>
                <a href="/adminUpdateTokenType" class="list-group-item"l>updateTokenType</a>
                <a href="#" class="list-group-item active">retrieveTokenType</a>
                <a href="/adminEnrollAttributeOfTokenType" class="list-group-item">enrollAttributeOfTokenType</a>
                <a href="/adminUpdateAttributeOfTokenType" class="list-group-item">updateAttributeOfTokenType</a>
                <a href="/adminRetrieveAttributeOfTokenType" class="list-group-item"l>retrieveAttributeOfTokenType</a>
                <a href="/adminDropAttributeTokenType" class="list-group-item">dropAttributeTokenType</a>
                <a href="/adminDropTokenType" class="list-group-item">dropTokenType</a>
            </div>
        </div>
        <!-- /.col-lg-3 -->

        <div class="card card-outline-secondary my-4">
            <div class="card-header">
                <h1>Retrieve Token Type </h1>



                <%--                    <input type="text" id="pages" class="fadeIn third" name="pages" placeholder="pages"><br>--%>
                <%--                    <input type="text" id="signers" class="fadeIn second" name="signers" placeholder="signers"><br>--%>
                <%--                    <input type="text" id="signatures" class="fadeIn third" name="signatures" placeholder="signatures"><br>--%>
                <%--                    <input type="text" id="tokenType" class="fadeIn third" name="tokenType" placeholder="tokenType"><br>--%>
                <div align="right">
                    <input name="addButton" class="btn btn-success" type="button" style="cursor:hand" onClick="insRowForXAttr()" value="Add XAttr">
                </div>
            </div>
            <div class="card-body" algin="right">
                    <table width="400" border="0" cellspacing="0" cellpadding="0">
                        <%--                        <tr>--%>
                        <%--                            <td colspan="2" align="left" bgcolor="#FFFFFF">--%>
                        <%--                                <table width="100%" border="0" cellpadding="0" cellspacing="0">--%>

                        <tr>
                            <td height="25">
                                <table id="addTable" width="400" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF" border="0">
                                </table>
                            </td>
                            <td height="25">
                                <table id="addTable2" width="400" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF" border="0">

                                </table>
                            </td>
                        </tr>
                        <tr>

                        </tr>
                        <%--                            </td>--%>
                        <%--                        </tr>--%>
                    </table>
                    <input type="hidden" id="ownerKey" name="ownerKey" value="${sessionUser}">
                    <input type="hidden" id="xattrCount" name="xattrCount">
                    <input type="hidden" id="uriCount" name="uriCount">
                    <table width="780">
                        <tr>
                            <td>
                                token Type <input type="text" name="tokenType" id="tokenType" class="btn-outline-info">
                            </td>
                            <td align="right">
                                <input type="submit" class="btn btn-success" value="submit" onclick="retrieveTokenType()">
                            </td>
                        </tr>
                    </table>
                <hr>
                <%--                    <a href="#" class="btn btn-success">Leave a Review</a>--%>
            </div>
        </div>
    </div>
</div>

</body>

<script >

    function retrieveTokenType() {

        tokenType = document.getElementById("tokenType").value;

        $.ajax({
            type: "POST",
            url: "/retrieveTokenType",
            data: {
                "tokenType" : tokenType
            },
            //dataType: "json",
            success: function(data) {
                swal({text: data, icon: "success", button: "close"});
            },
            error: function(err) {
                swal("error" + err);
            }
        });
    }
</script>

<script src="${ctx}/js/jquery-min.js"></script>
</html>