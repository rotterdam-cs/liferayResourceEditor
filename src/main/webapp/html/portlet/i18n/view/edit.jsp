<%@include file="init-edit.jsp" %>
<form action="${saveURL}" id="${namespace}fm" method="POST" style="width:300px;">
<legend style="border-color: -moz-use-text-color -moz-use-text-color #DDDDDD; font-weight: normal; padding: 0px; margin-bottom:15px;" ><liferay-ui:message key="edit"/></legend>
<label style="font-size: 12px;"><liferay-ui:message key="key"/>:</label>
<input id="${namespace}key" name="key" type="text" value="" style="height:20px;width:200px;padding:0px;" /><span class="error none"><liferay-ui:message key="required"/></span><br/><br/>
<label style="font-size: 12px;"><liferay-ui:message key="resources"/>:</label>
<table id="${namespace}content">
    <c:forEach items="${locales}" var="localeItem">
        <tr>
            <td>
                <input class="resource-value" type="text" name="value" style="height:20px;width:200px;padding:0px;margin-bottom:2px;" />
            </td>
            <td>
                <input class="resource-locale" type="text" name="locale" value="${localeItem}" disabled="" style="height:20px;padding:0px;margin-bottom:2px;" />
            </td>
        </tr>
    </c:forEach>
</table>

    <input type="hidden" name="data"/>
</form>
<br/>
<button onclick="window.location.href='${defaultURL}'"><liferay-ui:message key="back"/></button>
<button class="save-button"><liferay-ui:message key="save"/></button>