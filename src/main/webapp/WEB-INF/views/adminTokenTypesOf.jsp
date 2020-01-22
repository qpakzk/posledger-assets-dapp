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
                <a href="/admin" class="list-group-item ">토큰 타입 등록</a>
                <a href="#" class="list-group-item active">전체 토큰 타입 목록 조회</a>
                <a href="/adminUpdateTokenType" class="list-group-item"l>토큰 타입 수정</a>
                <a href="/adminRetrieveTokenType" class="list-group-item">토큰 타입 조회</a>
                <a href="/adminEnrollAttributeOfTokenType" class="list-group-item">토큰 타입 속성 등록</a>
                <a href="/adminUpdateAttributeOfTokenType" class="list-group-item">토큰 타입 속성 수정</a>
                <a href="/adminRetrieveAttributeOfTokenType" class="list-group-item"l>토큰 타입 속성 조회</a>
                <a href="/adminDropAttributeTokenType" class="list-group-item">토큰 타입 속성 삭제</a>
                <a href="/adminDropTokenType" class="list-group-item">토큰 타입 삭제</a>
            </div>
        </div>
        <!-- /.col-lg-3 -->

        <div class="col-lg-9">
            <div class="card card-outline-secondary my-4">
                <div class="card-header">
                    <h1>Token Types </h1>
                </div>
                <div class="card-body">
                    <table width="780">
                        <tr>
                            <td align="right">
                                <input type="tokenTypesOf" class="btn btn-success" value="submit" onclick="tokenTypesOf()">
                            </td>
                        </tr>
                    </table>
                </div>
            </div>

        </div>
    </div>
</div>

</body>

<script >

    function tokenTypesOf() {

        $.ajax({
            type: "POST",
            url: "/tokenTypesOf",
            data: {
                "data" : "data"
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