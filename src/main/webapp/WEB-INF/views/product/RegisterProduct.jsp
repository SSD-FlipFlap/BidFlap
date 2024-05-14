<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="/resources/css/product/RegisterProduct.css">
    <title>상품 정보</title>
</head>
<body>
<%@ include file="../Header2.jsp"%>
<h1>상품정보</h1>
<form action="" method="post">
    <div class="product-pic"><br/>
        <div class="camera">
            <img id="cameraImg" src="/resources/img/CameraImg.svg">
        </div>
    </div>

    <div class="product-name">상품명
        <input type="text" id="productName" name="productName" class="input-style">
    </div>

    <div class="category">카테고리</div>

    <div class="product-category">
        <input type="checkbox" id="mobile" name="category" value="mobile">
        <label for="mobile">휴대폰</label>

        <input type="checkbox" id="tablet" name="category" value="tablet">
        <label for="tablet">태블릿</label>

        <input type="checkbox" id="camera" name="category" value="camera">
        <label for="camera">카메라</label>

        <input type="checkbox" id="pc_parts" name="category" value="pc_parts">
        <label for="pc_parts">PC 부품</label>

        <br/>
        <input type="checkbox" id="wearable" name="category" value="웨어러블">
        <label for="wearable">웨어러블</label>

        <input type="checkbox" id="pc_notebook" name="category" value="pc_notebook">
        <label for="pc_notebook">PC/노트북</label>

        <input type="checkbox" id="accessory" name="category" value="accessory">
        <label for="accessory">악세사리</label>

        <input type="checkbox" id="etc" name="category" value="etc">
        <label for="etc">기타</label>
    </div>
    <br/>

    <div class="line">    </div>

    <!-- 가격 입력 -->
    <div class="price">가격
        <input type="text" id="price" name="price" class="input-style">
    </div>

    <!-- 설명 -->
    <div class="description">설명</div>

    <!-- 설명 칸-->
    <div class="rectangle-180">
        <textarea class="description-text" name="description" rows="4" cols="50">
        <%
            String description = request.getParameter("description");
            if (description != null) {
                out.print(description);
            }
        %>
        </textarea>
    </div>

    <div>
        <button type="submit">등록</button>
    </div>
</form>
</body>
</html>