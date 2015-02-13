Portlet-Location-Tracker 
========================

## Savoir-faire Linux Modifications

A new portlet has been developped, please find below its modifications :
* new maven portlet : portletfinder-portlet
* dependency : liferay 6.2.1 and above
* New features
 * add the portlet id in the select box (will be easier to make a difference between liferay portlets and custom ones)
 * search is done also for portlet instances (portletid having the _INSTANCE_ string in the name)
 * added portlet instance information
* limitations
 * removed the jasper export

## Developper authorization do modify the content

> ---------- Forwarded message ----------
> From: Jitendra rajput <jit066124@gmail.com>
> Date: Thu, Dec 18, 2014 at 12:53 AM
> Subject: Re: [All About Liferay] New message received.
> To: Julien Boumard <julien.boumard@gmail.com>
> 
> 
> On Thu, Dec 18, 2014 at 3:03 AM, Blogger Contact Form <no-reply@blogger.com> wrote:
> 
> maven, liferay 6.0 and CE compl
> 
> 
> Hello Julien,
> 
> Good to know that my application helped you.. Yes you can use and modify the same according to your need.
> 
> If you have some good suggestion or enhancement for location tracker then please let me know. Suggestions are always welcome. 
> 
> Regrading your third point ..i am working on this application to support some of the major Liferay releases which covers both CE and EE.
> 
> If you like this app then please rate and provide your valuable comments on market place.
> 
> 
> -- 
> Thank You,
> Jitendra Rajput
> 


## original readme.md

<b>Steps for Liferay 6.1 GA 2 and Liferay 6.2 GA1</b>

Portlet Location Tracker - Find out where your portlet is placed.

This app will help developers as well as end user to locate the portlets placed on different pages.
App will search in entire portal for the portlet you are looking for and display possible results.
For big portal its difficult for Developers/QA and end users to locate the page. This application will help theme to track where portlet is placed . 



Here is the description about the functionalits available in Location Tracker Portlet

1) Find portlet

- By default app will display all the portlets available in company except system portlets.
- Entire list of portlet will be displayed using dropdown
- User can search for page locations by selecting any of the portlets from list.
- User can go to actual page as well from results it self.
- In results application will display more information about page (private or public page). It will also display group name in which page belongs.


2) Export to Excel 

- User can export search results in to Excel file . For Export to Excel application uses Jasper report.


<b>v1.1</b>

- In v1.1 release of this application new functionalities has been added.
- Action menu has been introduced to couple multiple actions.
- User can now remove any portlet from page using this application.
