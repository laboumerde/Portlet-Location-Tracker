<%--
/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<portlet:defineObjects />
<theme:defineObjects />

<portlet:renderURL var="portletFinderUrl" />

<aui:form action="${portletFinderUrl}" method="post" name="fm">
    <aui:layout>

        <aui:column first="true">
            <aui:select name="portletSelect" label="portlet-select" useNamespace="true" inlineLabel="left">
                <aui:option label="select-a-portlet" value="" />

                <c:if test="${fn:length(portletList) > 0}">
                    <c:forEach items="${portletList}" var="portletW">
                        <c:set var="portletSelected" value="${(portletSelect == portletW.portlet.portletId)}" />
                        <option ${portletSelected ? 'selected="selected"' : ''} value="${portletW.portlet.portletId}">
                            ${portletW.selectLabel}
                        </option>
                    </c:forEach>
                </c:if>
            </aui:select>
        </aui:column>

        <aui:column columnWidth="20">
            <aui:button value="search" name="search"/>
        </aui:column>

    </aui:layout>
</aui:form>

<aui:script>
    AUI().use('aui-base', function(A){
        A.one("#<portlet:namespace />search").on('click',function(){
            submitForm(document.<portlet:namespace />fm);
        })
    });
</aui:script>

<liferay-ui:search-container hover="false"  searchContainer="${searchContainer}">
    <liferay-ui:search-container-results results="${searchContainer.results}" total="${searchContainer.total}" />
    <liferay-ui:search-container-row className="com.savoirfairelinux.portletfinder.model.PortletFinderLayoutWrapper" keyProperty="layoutId" modelVar="layoutObj">

        <liferay-ui:search-container-column-text name="page-name" property="layout.name"/>
        <liferay-ui:search-container-column-text name="group" value="${layoutObj.layout.getGroup().getDescriptiveName()}" />			
        <liferay-ui:search-container-column-text name="friendly-url" property="layout.friendlyURL"/>

        <c:set var="isPrivatePageLabel">
            <c:choose>
                <c:when test="${layoutObj.layout.isPrivateLayout()}">
                    <liferay-ui:message key="yes" />
                </c:when>
                <c:otherwise>
                    <liferay-ui:message key="no" />
                </c:otherwise>
            </c:choose>
        </c:set>

        <liferay-ui:search-container-column-text name="is-private-page" value="${isPrivatePageLabel}" />
        <liferay-ui:search-container-column-text name="page-url">
            <aui:a href="${layoutObj.pageURL}" label="go-to-page" target="_blank" />
        </liferay-ui:search-container-column-text>
        <liferay-ui:search-container-column-text name="portlet-instances">
            <c:out value="${layoutObj.portletInstances}" />
        </liferay-ui:search-container-column-text>
    </liferay-ui:search-container-row>

    <liferay-ui:search-iterator/>
</liferay-ui:search-container>