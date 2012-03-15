<%@include file="init.jsp" %>

<div class="portletBlock">
    <div id="${namespace}loadImg" class="none activeLoad">
        <img src="/html/themes/control_panel/images/application/loading_indicator.gif"/>
    </div>

    <div id="${namespace}mswContent" class="msw-table-container">

    </div>
    <div>
        <button class="save-button"><liferay-ui:message key="save"/></button>
        <button class="new-button"><liferay-ui:message key="new"/></button>
    </div>

    <div id="${namespace}paginContainer">
        <div id="${namespace}paginator">

        </div>
    </div>
</div>