package com.savoirfairelinux.portletfinder;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutConstants;
import com.liferay.portal.model.LayoutTypePortlet;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.PortletLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.comparator.PortletTitleComparator;
import com.savoirfairelinux.portletfinder.model.PortletFinderLayoutWrapper;
import com.savoirfairelinux.portletfinder.model.PortletFinderPortletWrapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Porlet Finder utils.
 * This portlet is inspired by the work of Jitendra Rajput (http://itsliferay.blogspot.com)
 *
 * @author Julien Boumard <julien.boumard@savoirfairelinux.com>
 *
 */
public class PortletFinderUtil
{
    /**
     * Get All portlets for Current Company
     *
     * @param companyId The company ID in which we search the portlets
     * @param locale The locale in which we search the portlets
     *
     * @return All portlets based on the locale and company
     */
    public static List<Portlet> getPortletList(long companyId , Locale locale)
    {
        List<Portlet> portletList = null;
        try
        {
            portletList = PortletLocalServiceUtil.getPortlets(companyId, false, false);
            portletList = ListUtil.sort(portletList, new PortletTitleComparator(locale));
        } catch (SystemException e)
        {
            LOGGER.error(e.getMessage(), e);
        }
        return portletList;
    }

	public static List<PortletFinderPortletWrapper> convertToWrapper(List<Portlet> portletList, Locale locale){
		List<PortletFinderPortletWrapper> portletWrapperList = new ArrayList<PortletFinderPortletWrapper>();
		for (Portlet portlet : portletList) {
			PortletFinderPortletWrapper portletWrapper= new PortletFinderPortletWrapper(portlet);
			portletWrapper.setLocale(locale);
			portletWrapperList.add(portletWrapper);
		}
		return portletWrapperList;
	}

    /**
     * Get Portlet based on portlet id
     * @param portletId
                                 * @return a Portlet instance based on the ID.
     */
    public static Portlet getPortlet(String portletId)
    {
        return PortletLocalServiceUtil.getPortletById(portletId);
    }

    /**
     * Get Portlet locations
     * @param groupList
     * @param privateLayout
     * @param portletId
     * @return Returns the layouts containing the portlets
     */
    public static List<Layout> doGetPortletLocation(List<Group> groupList, boolean privateLayout, String portletId){
        List<Layout> portletDetailsList = new ArrayList<Layout>();
        if (Validator.isNotNull(groupList) && !groupList.isEmpty()){
            for (Group group : groupList){
                long scopeGroupId = group.getGroupId();
                long groupId = getGroupId(group);
                Set<Layout> groupLayoutSet = new HashSet<Layout>();
                try{
                    List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(groupId, privateLayout, LayoutConstants.TYPE_PORTLET);
                    for (Layout layout : layouts){
                        LayoutTypePortlet layoutTypePortlet = (LayoutTypePortlet) layout.getLayoutType();
                        List<Portlet> layoutPortlets =  layoutTypePortlet.getAllPortlets();
                        for (Portlet portlet : layoutPortlets){
                            if(portletId.equals(getOriginalPortletId(portlet.getPortletId()))){
                                if (PortalUtil.getScopeGroupId(layout, portletId) == scopeGroupId){
                                    groupLayoutSet.add(layout);
                                }
                            }
                        }
                    }
                    portletDetailsList.addAll(groupLayoutSet);
                } catch (PortalException e){
                    LOGGER.error(e.getMessage());
                } catch (SystemException e){
                    LOGGER.error(e.getMessage());
                }
            }
        }
        return portletDetailsList;
	}

    /**
     * Get All Groups for Current Company
     * @param companyId
     * @return a list of the groups for current company
     */
    public static List<Group> getCompanyGroups(long companyId){
        List<Group> groupList = new ArrayList<Group>();
        try{
            groupList = GroupLocalServiceUtil.getCompanyGroups(companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
        } catch (SystemException e){
            LOGGER.error(e.getMessage(), e);
        }
        return groupList;
    }

    /**
     * Get Group Id
     * @param group
     * @return the group ID of the group
     */
    public static long getGroupId(Group group)
    {
        long groupId = group.getGroupId();
        try{
            if (group.isLayout()){
                Layout scopeLayout = LayoutLocalServiceUtil.getLayout(group.getClassPK());
                groupId = scopeLayout.getGroupId();
            }
        } catch (PortalException e){
                    LOGGER.error(e.getMessage());
        } catch (SystemException e){
                    LOGGER.error(e.getMessage());
        }
        return groupId;
    }

    /**
     * Get Portlet Title
     * @param portlet
     * @param locale
     * @param request
     * @return the portlet Title
     */
    public static String getPortletTitle(Portlet portlet , Locale locale , HttpServletRequest request){
        String portletTile = PortalUtil.getPortletTitle(portlet,request.getSession().getServletContext(),locale);
        return portletTile;
    }

    /**
     * Get locations for selected portlet
     *
     * @param companyId The ID of the company in which we search for the portlet
     * @param portletId The portlet to find's ID
     *
     * @return The layouts containing the portlets
     */
    public static List<Layout> findPortlet(long companyId, String portletId)
    {
        List<Layout> layoutList = new ArrayList<Layout>();
        LOGGER.debug("Searching for portlet ID : " + portletId);

        if(Validator.isNotNull(portletId)){
            Portlet portlet = getPortlet(portletId);
            List<Group> groupList = getCompanyGroups(companyId);
            List<Layout> publiclayoutList = doGetPortletLocation(groupList, false, portlet.getPortletId());
            List<Layout> privateLayoutList = doGetPortletLocation(groupList, true, portlet.getPortletId());

            if(Validator.isNotNull(publiclayoutList) && !publiclayoutList.isEmpty()){
                layoutList.addAll(publiclayoutList);
            }
            if(Validator.isNotNull(privateLayoutList) && !privateLayoutList.isEmpty()){
                layoutList.addAll(privateLayoutList);
            }
        }

        return layoutList;
    }

    /**
     * Get Search Container Object
     *
     * @param renderRequest The portlet render request
     * @param renderResponse The portlet render response
     * @param portletIdParam The portlet ID request parameter name
     * @param companyId The ID of the company in which we search for the portlet
     * @param portletId The ID of the portlet we are searching for
     *
     * @return The search Container with the layouts containing the portlets
     *
     * @throws SystemException
     * @throws PortalException
     */
    public static SearchContainer<PortletFinderLayoutWrapper> getSearchContainer(
            RenderRequest renderRequest,
            RenderResponse renderResponse,
            String portletIdParam,
            long companyId,
            String portletId) throws PortalException, SystemException {

        SearchContainer<PortletFinderLayoutWrapper> searchContainer;
        List<Layout> layoutList = findPortlet(companyId, portletId);
        PortletURL portletURL = renderResponse.createRenderURL();
        portletURL.setParameter(portletIdParam, portletId);

        searchContainer = new SearchContainer<PortletFinderLayoutWrapper>(
            renderRequest,
            null,
            null,
            SearchContainer.DEFAULT_CUR_PARAM,
            SearchContainer.DEFAULT_DELTA,
            portletURL,
            null,
            "no-locations-were-found"
        );

        if (Validator.isNotNull(layoutList)) {
            String portalURL = ((ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY)).getPortalURL();
            List<Layout> results = ListUtil.subList(layoutList, searchContainer.getStart(), searchContainer.getEnd());
            List<PortletFinderLayoutWrapper> wrappedResults = new ArrayList<PortletFinderLayoutWrapper>(results.size());

            // Wrap all the results so they can be consumed in the JSP
            for(Layout l : results) {
                String pageURL = PortletFinderUtil.getPageURL(
                    l.isPrivateLayout(),
                    l.getFriendlyURL(),
                    l.getGroup().getFriendlyURL(),
                    portalURL
                );

                String portletInstances = getPortletInstances(l, portletId);

                wrappedResults.add(new PortletFinderLayoutWrapper(l, pageURL, portletInstances));
            }

            searchContainer.setResults(wrappedResults);
            searchContainer.setTotal(layoutList.size());
        }
        return searchContainer;
    }

    /**
     * Get Page URL where portlet is placed
     *
     * @param isPrivateLayout
     * @param friendlyURL
     * @param groupFriendlyURL
     * @param portalURL The URL of the portal
     *
     * @return The URL of the page where the portlet is placed
     */
    private static String getPageURL(boolean isPrivateLayout, String friendlyURL,String groupFriendlyURL , String portalURL) {
        StringBuilder sb = new StringBuilder();
        sb.append(portalURL);
        if (isPrivateLayout){
            sb.append(PortalUtil.getPathFriendlyURLPrivateGroup());
        }else{
            sb.append(PortalUtil.getPathFriendlyURLPublic());
        }
        sb.append(groupFriendlyURL);
        sb.append(friendlyURL);
        return sb.toString();
    }

    /**
     * Get the group descriptive name associated to the layout
     * @param layout
     * @return the groupe descriptive name
     */
    public static String getGroupDescriptiveName(Layout layout) {
        String groupName = StringPool.BLANK;
        if (Validator.isNotNull(layout)) {
            try {
                groupName = layout.getGroup().getDescriptiveName();
            } catch (PortalException e) {
                LOGGER.error(e.getMessage(), e);
            } catch (SystemException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return groupName;
    }

    /**
     * Retrieves the original portlet ID
     * @param portletId
     * @return the orgininal portlet ID, without its instance
     */
    public static String getOriginalPortletId(String portletId){
        String res = portletId;
        if(portletId.contains(INSTANCE)){
            res = StringUtil.extractFirst(portletId, INSTANCE);
        }
        return res;
    }

	/**
	 * @param layout page layout
	 * @param portletId portlet Id
	 * @return the portlet full Ids in the page
	 */
	private static String getPortletInstances(Layout layout, String portletId){
		StringBuilder res = new StringBuilder();

		if (layout != null && layout.getLayoutType() instanceof LayoutTypePortlet){
			LayoutTypePortlet layoutTypePortlet = (LayoutTypePortlet) layout.getLayoutType();
			try {
				List<Portlet> portlets = layoutTypePortlet.getPortlets();
				for (Portlet portlet : portlets) {
					if(portlet.getInstanceId() != null && getOriginalPortletId(portlet.getPortletId()).equals(portletId)){
						if(!(res.length() == 0)) {
							res.append(StringPool.COMMA_AND_SPACE);
						}
						res.append(portlet.getInstanceId());
					}
				}
			} catch (SystemException ex) {
				LOGGER.error(ex);
			}
		}

		return res.toString();
	}

    /**
     * Instance String in the portlet name
     */
    public final static String INSTANCE = "_INSTANCE_";

    /**
     * Logger
     */
    private static final Log LOGGER = LogFactoryUtil.getLog(PortletFinderUtil.class);
}
