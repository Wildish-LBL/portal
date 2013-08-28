package pl.psnc.dl.wf4ever.portal;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;

import pl.psnc.dl.wf4ever.portal.pages.AboutPage;
import pl.psnc.dl.wf4ever.portal.pages.Error404Page;
import pl.psnc.dl.wf4ever.portal.pages.HomePage;
import pl.psnc.dl.wf4ever.portal.pages.SparqlEndpointPage;
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
     */
    @Test
    public void homepageRendersSuccessfully() {
        tester.startPage(HomePage.class);
        //assert rendered page class
        tester.assertRenderedPage(HomePage.class);
    }


    /**
     * Tests if the page loads.
     */
    @Test
    public void sparqlEndpointRendersSuccessfully() {
        tester.startPage(SparqlEndpointPage.class);
        tester.assertRenderedPage(SparqlEndpointPage.class);
    }


    /**
     * Tests if the page loads.
     */
    @Test
    public void authenticationPageRendersSuccessfully() {
        tester.startPage(AuthenticationPage.class);
        tester.assertRenderedPage(AuthenticationPage.class);
    }


    /**
     * Tests if the page loads.
     */
    @Test
    public void errorPageRendersSuccessfully() {
        tester.startPage(Error404Page.class);
        tester.assertRenderedPage(Error404Page.class);
    }


    /**
     * Tests if the page loads.
     */
    @Test
    public void contactPageRendersSuccessfully() {
        tester.startPage(AboutPage.class);
        tester.assertRenderedPage(AboutPage.class);
    }


    /**
     * Tests if the page loads.
     */
    @Test
    public void helpRendersSuccessfully() {
        tester.startPage(AboutPage.class);
        tester.assertRenderedPage(AboutPage.class);
    }
}
