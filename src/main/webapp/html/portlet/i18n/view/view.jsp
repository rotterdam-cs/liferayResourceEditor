<%@include file="init.jsp" %>
<div class="portletBlock">
    <div class="filterTranslations">
        <div class="filterTranslationsGroup">
            <div id="${namespace}filterTranslationsForm">
                <div class="form-line">
                    <div class="left">
                        <liferay-ui:message key="resource-editor.on-key" />
                    </div>
                    <div class="center">
                        <input type="text" id="${namespace}onKeyInput" />
                    </div>
                    <div class="right"></div>
                </div>
                <div class="form-line">
                    <div class="left">
                        <liferay-ui:message key="resource-editor.on-message" />
                    </div>
                    <div class="center">
                        <input type="text" id="${namespace}onMessageInput" />
                    </div>
                    <div class="right">
                        <select id="${namespace}onMessageSelect">
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
                        <select id="${namespace}bundlesSelect">
                            <option value="all"><liferay-ui:message key="resource-editor.all-bundles" /></option>
                            <c:forEach var="bundle" items="${bundles}">
                                <option value="${bundle}">${bundle}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="right">
                        <input type="button" id="${namespace}searchButton" value="<liferay-ui:message key='search' />" />
                    </div>
                </div>
            </div>
        </div>
        <div class="advancedGroup">

        </div>
    </div>

    <div id="${namespace}translations">
        <div id="${namespace}messageContainer"></div>
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
</div>