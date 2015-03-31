package com.savoirfairelinux.portletfinder;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.IOException;
import java.util.Locale;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * Porlet Finder controller. This portlet is inspired by the work of Jitendra Rajput (http://itsliferay.blogspot.com)
 *
 * @author Julien Boumard <julien.boumard@savoirfairelinux.com>
 *
 */
public class PortletController extends MVCPortlet {

	@Override
	public void doView(RenderRequest request, RenderResponse response) throws IOException, PortletException {
		Locale locale = PortalUtil.getLocale(request);
		Long companyId = PortalUtil.getCompanyId(request);
		request.setAttribute("portletList", PortletFinderUtil.convertToWrapper(PortletFinderUtil.getPortletList(companyId, locale), locale));
		request.setAttribute("searchContainer", PortletFinderUtil.getSearchContainer(request, response));
		String portletId = ParamUtil.getString(request, "portletSelect");
		request.setAttribute("portletSelect", portletId);

		super.doView(request, response);
	}

}