<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="/resources/css/user/SignUp.css">
    <title>회원가입</title>
    <script>
        // as 전문가 등록 여부
        function toggleExpertInput() {
            const expertInput = document.getElementById('expertInput');
            if (document.getElementById('expert_yes').checked) {
                expertInput.style.display = 'block';
            } else {
                expertInput.style.display = 'none';
            }
        }

        // 프로필 미리 보기
        function previewProfile(event) {
            const input = event.target;
            if (input.files && input.files[0]) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    const preview = document.getElementById('profilePreview');
                    preview.src = e.target.result;
                    document.getElementById('profilePreviewContainer').style.border = '2px solid #A1A1A1'; // 테두리 추가
                }
                reader.readAsDataURL(input.files[0]);
            } else {
                // 파일이 선택되지 않았을 때 기본 프로필을 표시
                document.getElementById('profilePreview').src = '/resources/img/profile.png';
                document.getElementById('profilePreviewContainer').style.border = 'none'; // 테두리 제거
            }
        }

        document.getElementById('profilePreview').addEventListener('click', function() {
            document.getElementById('profile').click();
        });


    </script>
</head>
<body>
<%@ include file="../Header2.jsp" %>

<h1>회원가입</h1>
<form action="/register" method="post" enctype="multipart/form-data">
    <div>
        <label for="email" class="title">이메일 주소*</label>
        <input type="email" id="email" name="email" required placeholder="예: bidflap@bidflap.co.kr" class="input-style">
    </div>

    <div><br/>
        <label for="password" class="title">비밀번호*</label>
        <input type="password" id="password" name="password" required placeholder="영문, 숫자, 특수문자 포함 8자 이상" class="input-style">
    </div>

    <div><br/>
        <label for="nickname" class="title">닉네임*</label>
        <input type="text" id="nickname" name="nickname" required placeholder="닉네임을 입력하세요" class="input-style">
    </div>

    <div><br/>
        <label class="title">as 전문가 등록*</label>
        <div class="radio-container">
            <input type="radio" id="expertYes" name="expert" value="yes" onclick="toggleExpertInput()">
            <label for="expertYes">등록</label>
            <input type="radio" id="expertNo" name="expert" value="no" onclick="toggleExpertInput()" checked>
            <label for="expertNo">미등록</label>
        </div>
    </div>
    <div id="expertInput" style="display: none;">
        <textarea id="expertField" name="expertField" required placeholder="as 전문 분야 및 자기 소개를 입력하세요" class="input-style"></textarea>
    </div>

    <div><br/>
        <label for="accountNumber" class="title">계좌 정보*</label>
        <div class="bank-container">
            <select id="bank" name="bank" class="input-style">
                <option value="">은행을 선택하세요</option>
                <option value="국민">국민</option>
                <option value="신한">신한</option>
                <option value="우리">우리</option>
                <option value="하나">하나</option>
                <option value="농협">농협</option>
                <option value="수협">수협</option>
                <option value="산업">산업</option>
                <option value="카카오뱅크">카카오뱅크</option>
            </select>
            <input type="text" id="accountNumber" name="accountNumber" class="input-style" placeholder="계좌 번호를 입력하세요">
        </div>
    </div>

    <div id="profileContainer"><br/>
        <label for="profile" class="title">프로필 사진</label>
        <div id="profilePreviewContainer">
            <img id="profilePreview" src="/resources/img/profile.png" alt="프로필 미리보기">
        </div>
        <input type="file" id="profile" name="profile" accept="image/*" onchange="previewProfile(event)">
    </div>

    <div><br/>
        <label for="interest" class="title">관심 전자제품 카테고리</label>
        <input type="hidden" id="interest" name="interest">
    </div>

    <div class="category-list">
        <input type="checkbox" id="mobile" name="category" value="휴대폰">
        <label for="mobile">휴대폰</label>

        <input type="checkbox" id="tablet" name="category" value="태블릿">
        <label for="tablet">태블릿</label>

        <input type="checkbox" id="camera" name="category" value="카메라">
        <label for="camera">카메라</label>

        <input type="checkbox" id="pcParts" name="category" value="PC 부품">
        <label for="pcParts">PC 부품</label>

        <br/>
        <input type="checkbox" id="wearable" name="category" value="웨어러블">
        <label for="wearable">웨어러블</label>

        <input type="checkbox" id="pcNotebook" name="category" value="PC/노트북">
        <label for="pcNotebook">PC/노트북</label>

        <input type="checkbox" id="accessory" name="category" value="악세사리">
        <label for="accessory">악세사리</label>

        <input type="checkbox" id="etc" name="category" value="기타">
        <label for="etc">기타</label>
    </div>
    <br/>
    <div>
        <button type="submit">가입하기</button>
    </div>
</form>
</body>
</html>
