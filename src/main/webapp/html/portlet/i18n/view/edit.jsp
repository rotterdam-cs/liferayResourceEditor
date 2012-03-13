<%@include file="init-edit.jsp" %>

<h3><liferay-ui:message key="edit"/></h3>
<liferay-ui:message key="key"/>:
<input id="${namespace}key" name="key" type="text"  value=""><span class="error none"><liferay-ui:message key="required"/></span><br>
<liferay-ui:message key="resources"/>:
<table id="${namespace}content">
    <c:forEach items="${locales}" var="localeItem">
        <tr>
            <td>
                <input class="resource-value" type="text" name="value">
            </td>
            <td>
                <input class="resource-locale" type="text" name="locale" value="${localeItem}" disabled="">
            </td>
        </tr>
    </c:forEach>
</table>

<form action="${saveURL}" id="${namespace}fm" method="POST">
    <input type="hidden" name="data">
</form>

<button onclick="window.location.href='${defaultURL}'"><liferay-ui:message key="back"/></button>
<button class="save-button"><liferay-ui:message key="save"/></button>




