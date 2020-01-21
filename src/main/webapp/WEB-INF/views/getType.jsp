<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko" class="high">
<head>
    <title>PosLedger Assets Application</title>
    <meta charset="UTF-8">
    <link href="../../common/bootstrap.min.css" rel="stylesheet" type="text/css"></link>
    <script src="https://unpkg.com/sweetalert/dist/sweetalert.min.js"></script>
    <script src="${ctx}/js/jquery-min.js"></script>
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
                    <a class="nav-link" href="#">Home
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
                <a href="/standard" class="list-group-item">표준 토큰 생성</a>
                <a href="/standard/burn" class="list-group-item">토큰 삭제</a>
                <a href="/standard/getType" class="list-group-item active">토큰 타입 조회</a>
                <a href="/standard/balanceOf" class="list-group-item">토큰 개수 조회</a>
                <a href="/standard/ownerOf" class="list-group-item">토큰 소유자 조회</a>
                <a href="/standard/transferFrom" class="list-group-item">토큰 소유자 변경</a>
                <a href="/standard/approve" class="list-group-item">토큰 피승인자 설정</a>
                <a href="/standard/setApprovalForAll" class="list-group-item">운영자 추가/삭제</a>
                <a href="/standard/getApproved" class="list-group-item">토큰 피승인자 조회</a>
                <a href="/standard/isApprovedForAll" class="list-group-item">운영자 여부 조회</a>
            </div>
        </div>
        <div class="col-lg-9">
            <div class="card card-outline-secondary my-4">
                <div class="card-header">
                    <h1>토큰 타입 조회</h1>
                    <h2>표준 SDK의 getType 함수</h2>
                </div>
                <div class="card-body">
                    <table width="780">
                        <tr>
                            <td>
                                토큰 ID: <input type="text" id="tokenId">
                            </td>
                            <td align="right">
                                <input type="submit" class="btn btn-success" value="submit" onclick="getType()">
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
<script>
    function getType() {
        tokenId = document.getElementById("tokenId").value;
        $.ajax({
            type: "POST",
            url: "/standard/getType",
            data: {
              "tokenId": tokenId
            },
            success: function(data) {
                swal({title: "Success", text: data, icon: "success", button: "close"});
            },
            error: function(err) {
                swal("error " + err);
            }
        });

    }
</script>
</html>