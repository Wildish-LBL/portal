package pl.psnc.dl.wf4ever.portal;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start class.
 * 
 * @see pl.psnc.dl.wf4ever.portal.Start#main(String[])
 */
public class WicketApplication
	extends WebApplication
{

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class< ? extends WebPage> getHomePage()
	{
		return HomeMockupPage.class;
	}


	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init()
	{
		super.init();

		mountPage("/wickethome", WicketHomePage.class);
		mountPage("/mockup", HomeMockupPage.class);
	}
}
