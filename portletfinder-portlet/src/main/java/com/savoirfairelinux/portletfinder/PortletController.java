package com.savoirfairelinux.portletfinder;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import java.util.Locale;

/**
 * Porlet Finder controller. This portlet is inspired by the work of Jitendra Rajput (http://itsliferay.blogspot.com)
 *
 * @author Julien Boumard <julien.boumard@savoirfairelinux.com>
 *
 */
public class PortletController extends MVCPortlet {

	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws IOException, PortletException {
		Locale locale = PortalUtil.getLocale(renderRequest);
		Long companyId = PortalUtil.getCompanyId(renderRequest);
		renderRequest.setAttribute("portletList", PortletFinderUtil.convertToWrapper(PortletFinderUtil.getPortletList(companyId, locale), locale));
		renderRequest.setAttribute("searchContainer", PortletFinderUtil.getSearchContainer(renderRequest, renderResponse));
		String portletId = ParamUtil.getString(renderRequest, "portletSelect");
		renderRequest.setAttribute("portletSelect", portletId);
		this.include(viewTemplate, renderRequest, renderResponse);
	}

	/**
	 * Logger
	 */
	private static final Log LOGGER = LogFactoryUtil.getLog(PortletController.class);
}