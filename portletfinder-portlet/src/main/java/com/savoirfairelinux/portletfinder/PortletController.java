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

    private static final String PARAM_SELECTED_PORTLET = "portletSelect";

    private static final String RQ_ATTR_SELECTED_PORTLET = PARAM_SELECTED_PORTLET;
    private static final String RQ_ATTR_PORTLET_LIST = "portletList";
    private static final String RQ_ATTR_SEARCH_CONTAINER = "searchContainer";

	@Override
	public void doView(RenderRequest request, RenderResponse response) throws IOException, PortletException {
	    Locale locale = PortalUtil.getLocale(request);
	    long companyId = PortalUtil.getCompanyId(request);

	    String portletId = ParamUtil.getString(request, PARAM_SELECTED_PORTLET);
	    request.setAttribute(RQ_ATTR_SELECTED_PORTLET, portletId);

		request.setAttribute(
	        RQ_ATTR_PORTLET_LIST,
	        PortletFinderUtil.convertToWrapper(PortletFinderUtil.getPortletList(companyId, locale), locale)
        );

		try {
            request.setAttribute(
                RQ_ATTR_SEARCH_CONTAINER,
                PortletFinderUtil.getSearchContainer(request, response, PARAM_SELECTED_PORTLET, companyId, portletId)
            );
        } catch (Exception e) {
            throw new PortletException(e);
        }

		super.doView(request, response);
	}

}