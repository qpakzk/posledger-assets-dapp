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
            <h1 class="my-4">Signature Service</h1>
            <div class="list-group">
                <a href="/main" class="list-group-item">Make signature</a>
                <a href="/mysign?ownerKey=${sessionUser}" class="list-group-item">My Signature</a>
                <a href="/addUser" class="list-group-item"l>Upload File</a>
                <a href="/mydoclist?ownerKey=${sessionUser}" class="list-group-item">My Document</a>

            </div>
        </div>
        <!-- /.col-lg-3 -->

        <div class="col-lg-9">

            <div class="card mt-4">


                <%--                <img class="card-img-top img-fluid" src="http://placehold.it/900x400" alt="">--%>
                <div class="card-body" align="center">
                    <table width="800" >
                        <tr align="center">
                            <td >
                                <button type="button" class="btn btn-outline-success" onclick="checkInfo(${tokenId})">Document Info</button>
                                <hr>
                            </td>
                        </tr>

                        <tr>
                            <td align="center">
                                <iframe style="width:60%; height:600px;" src="${docPath}"></iframe>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>

            <div class="card card-outline-secondary my-4">
                <div class="card-header">

                </div>
                <div class="card-body" align="right">

                    <form action="/doSign" method="post">
                        <input type="hidden" id="signer" name="signer" value="${sessionUser}">
                        <input type="hidden" id="ownerKey" name="ownerKey" value="${sessionUser}">
                        <input type="hidden" id="docNum" name="docNum" value=${docNum}>
                        <input type="hidden" id="docId" name="docId" value=${docId}>
                        <input type="hidden" id="tokenId" name="tokenId" value=${tokenId}>
                        <input type="hidden" id="docPath" name="docPath" value=${docPath}>
                        <input type="hidden" id="sigId" name="sigId" value=${sigId}>

                        <input type="submit" class="btn btn-success" value="sign">

                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
<script>

    function checkInfo(tokenId) {

        $.ajax({
            type: "POST",
            url: "/checkInfo",
            data: {
                "tokenId": tokenId
                //"strImg": dataURL
                //"test": "test string"
            },
            //dataType: "json",
            success: function (data) {
                swal({title: "Info", text: data, icon: "success", button: "close",});
            },
            error: function (err) {
                swal("error" + err);
            }
        });
    }
</script>

<script src="${ctx}/js/jquery-min.js"></script>
</html>