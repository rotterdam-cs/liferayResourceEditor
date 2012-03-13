<%@ include file="../init-common.jsp" %>

<portlet:renderURL var="defaultURL" portletMode="view" windowState="normal"/>

<portlet:renderURL var="defaultAjaxURL" portletMode="view" windowState="${exclusive}"/>

<portlet:resourceURL var="resourceContentURL" id="resourceContent"/>

<portlet:resourceURL var="uploadResourcesURL" id="uploadResources"/>

<portlet:resourceURL var="deleteURL" id="delete"/>

<portlet:renderURL var="editURL">
    <portlet:param name="render" value="edit"/>
</portlet:renderURL>

<portlet:renderURL var="saveURL">
    <portlet:param name="render" value="save"/>
</portlet:renderURL>