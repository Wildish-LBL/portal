package pl.psnc.dl.wf4ever.portal;

import java.net.URISyntaxException;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

import pl.psnc.dl.wf4ever.portal.pages.ContactPage;
import pl.psnc.dl.wf4ever.portal.pages.ErrorPage;
import pl.psnc.dl.wf4ever.portal.pages.HelpPage;
import pl.psnc.dl.wf4ever.portal.pages.SparqlEndpointPage;
import pl.psnc.dl.wf4ever.portal.pages.all.AllRosPage;
import pl.psnc.dl.wf4ever.portal.pages.home.HomePage;
import pl.psnc.dl.wf4ever.portal.pages.users.AuthenticationPage;

/**
 * Simple test using the WicketTester.
 */
public class TestPages {

    /** the tester. */
    private WicketTester tester;


    /**
     * Set up.
     */
    @Before
    public void setUp() {
        tester = new WicketTester(new PortalApplication());
    }


    /**
     * Tests if the page loads.
     * 
     * @throws URISyntaxException
     *             RO list returned by RODL is incorrect
     * @throws ROSRSException
     *             the list of ROs could not be fetched from ROSRS
     */
    @Test
    public void homepageRendersSuccessfully()
            throws URISyntaxException, ROSRSException {
        //start and render the test page
        tester.startPage(HomePage.class);
        //assert rendered page class
        tester.assertRenderedPage(HomePage.class);

        tester.startPage(AllRosPage.class);
        tester.assertRenderedPage(AllRosPage.class);

        tester.startPage(SparqlEndpointPage.class);
        tester.assertRenderedPage(SparqlEndpointPage.class);

        tester.startPage(AuthenticationPage.class);
        tester.assertRenderedPage(AuthenticationPage.class);

        tester.startPage(ErrorPage.class);
        tester.assertRenderedPage(ErrorPage.class);

        tester.startPage(ContactPage.class);
        tester.assertRenderedPage(ContactPage.class);

        tester.startPage(HelpPage.class);
        tester.assertRenderedPage(HelpPage.class);
        //
        //        ROSRService rosrs = new ROSRService(((PortalApplication) tester.getApplication()).getRodlURI(), null);
        //        List<URI> ros = rosrs.getROList(true);
        //        if (!ros.isEmpty()) {
        //            URI ro = ros.get(0);
        //            PageParameters parameters = new PageParameters();
        //            parameters.add("ro", ro);
        //            tester.startPage(RoPage.class, parameters);
        //            tester.assertRenderedPage(RoPage.class);
        //        }
    }
}
