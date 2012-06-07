package pl.psnc.dl.wf4ever.portal;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;

import pl.psnc.dl.wf4ever.portal.pages.home.HomePage;

/**
 * Simple test using the WicketTester.
 */
public class TestHomePage {

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
        //start and render the test page
        tester.startPage(HomePage.class);

        //assert rendered page class
        tester.assertRenderedPage(HomePage.class);
    }
}
