package com.savoirfairelinux.portletfinder;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.IOException;
import java.util.Locale;

import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * Porlet Finder controller. This portlet is inspired by the work of Jitendra
 * Rajput (http://itsliferay.blogspot.com)
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
        // Get locale and company info
        Locale locale = PortalUtil.getLocale(request);
        long companyId = PortalUtil.getCompanyId(request);

        // Get a list of all the available portlets in the given company
        request.setAttribute(
            RQ_ATTR_PORTLET_LIST,
            PortletFinderUtil.convertToWrapper(PortletFinderUtil.getPortletList(companyId, locale), locale)
        );

        // Get the ID of the portlet on which we perform a search
        String portletId = ParamUtil.getString(request, PARAM_SELECTED_PORTLET);
        request.setAttribute(RQ_ATTR_SELECTED_PORTLET, portletId);

        try {
            PortletURL portletURL = response.createRenderURL();
            portletURL.setParameter(PARAM_SELECTED_PORTLET, portletId);

            request.setAttribute(
                RQ_ATTR_SEARCH_CONTAINER,
                PortletFinderUtil.getSearchContainer(
                    request,
                    portletURL,
                    companyId,
                    portletId
                )
            );
        } catch (Exception e) {
            throw new PortletException(e);
        }

        super.doView(request, response);
    }

}