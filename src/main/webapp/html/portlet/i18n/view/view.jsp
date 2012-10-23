<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@include file="init.jsp" %>

<div class="portletBlock">
    <div class="filterTranslations well" style="width:612px;">
        <div class="filterTranslationsGroup">
            <div id="${namespace}filterTranslationsForm">
                <div class="form-line">
                    <div class="left">
                        <liferay-ui:message key="resource-editor.on-key" />
                    </div>
                    <div class="center">
                        <input type="text" id="${namespace}onKeyInput" style="height:20px;width:200px;padding:0px;" />
                    </div>
                    <div class="right"></div>
                </div>
                <div class="form-line">
                    <div class="left">
                        <liferay-ui:message key="resource-editor.on-message" />
                    </div>
                    <div class="center">
                        <input type="text" id="${namespace}onMessageInput" style="margin-left: 5px; width:200px;height:20px;padding:0px;" />
                    </div>
                    <div class="right">
                        <select id="${namespace}onMessageSelect" style="height:20px;width:70px;">
                            <c:forEach var="locale" items="${locales}">
                                <option value="${locale}">${locale}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="form-line">
                    <div class="left">
                        <liferay-ui:message key="resource-editor.in-bundle" />
                    </div>
                    <div class="center">
                        <select id="${namespace}bundlesSelect" style="margin-left: 5px; width:203px;height:20px;">
                            <option value="all"><liferay-ui:message key="resource-editor.all-bundles" /></option>
                            <c:forEach var="bundle" items="${bundles}">
                                <option value="${bundle}">${bundle}</option>
                            </c:forEach>
                        </select>
                    </div>                    
                </div>
                <div class="form-line">
                	<div class="left"></div>
                	<div class="center">
                        <input type="button" id="${namespace}searchButton" value="<liferay-ui:message key='search' />" style="width:50px"/>
                    </div>
                </div>
            </div>
        </div>
        <div class="advancedGroup">

        </div>
    </div>

    <div id="${namespace}translations" style="width:650px;">    	
        <div id="${namespace}messageContainer"></div>
        <div id="${namespace}loadImg" class="none activeLoad">
            <img src="/html/themes/control_panel/images/application/loading_indicator.gif"/>
        </div>

        <div id="${namespace}mswContent" class="msw-table-container">

        </div>     
        <div id="${namespace}paginContainer">
            <div id="${namespace}paginator">

            </div>
        </div>
        <div>
            <button class="save-button"><liferay-ui:message key="save"/></button>
            <button class="new-button"><liferay-ui:message key="new"/></button>
        </div>
    </div>
</div>