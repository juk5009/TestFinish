<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ include file="../layout/header.jsp" %>

        <div class="align_center">
            <h1>회원수정 페이지</h1>
        </div>
        <hr>
        <div class="align_center">
            <form action="/user/${user.id}/update" method="post">
                <input type="password" name="password" value="${user.password}" placeholder="Enter password"
                    required><br />
                <input type="email" name="email" value="${user.email}" placeholder="Enter email" required><br />
                <button type="submit">회원수정</button>
            </form>
        </div>

        <%@ include file="../layout/footer.jsp" %>