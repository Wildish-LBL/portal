package pl.psnc.dl.wf4ever.portal.components;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.purl.wf4ever.rosrs.client.users.User;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.pages.users.ProfilePage;

/**
 * A component that allows to sign in and out.
 * 
 * @author piotrekhol
 * 
 */
public class LoginLink extends Panel {

    /** id. */
    private static final long serialVersionUID = 2324973592678492491L;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     */
    public LoginLink(String id) {
        super(id);
        if (MySession.get().isSignedIn()) {
            add(new SignedInFragment("content", "signed-in", this, new Model<User>(MySession.get().getUser())));
        } else {
            add(new SignedOutFragment("content", "signed-out", this));
        }
    }


    /**
     * Fragment for an authenticated user.
     * 
     * @author piotrekhol
     * 
     */
    final class SignedInFragment extends Fragment {

        /** id. */
        private static final long serialVersionUID = 7361267067022078437L;


        /**
         * Constructor.
         * 
         * @param id
         *            The component id
         * @param markupId
         *            The associated id of the associated markup fragment
         * @param markupProvider
         *            The component whose markup contains the fragment's markup
         * @param model
         *            The user model
         */
        public SignedInFragment(String id, String markupId, MarkupContainer markupProvider, IModel<User> model) {
            super(id, markupId, markupProvider, model);
            add(new Label("user-name", model.getObject().getUsername()));
            add(new BookmarkablePageLink<Void>("profile", ProfilePage.class));
            add(new Link<Void>("sign-out") {

                /** id. */
                private static final long serialVersionUID = 3967872116504606294L;


                @Override
                public void onClick() {
                    MySession.get().signOut();
                    throw new RestartResponseException(getApplication().getHomePage());
                }

            });
        }

    }


    /**
     * Fragment for an anonymous user.
     * 
     * @author piotrekhol
     * 
     */
    final class SignedOutFragment extends Fragment {

        /** id. */
        private static final long serialVersionUID = 7361267067022078437L;


        /**
         * Constructor.
         * 
         * @param id
         *            The component id
         * @param markupId
         *            The associated id of the associated markup fragment
         * @param markupProvider
         *            The component whose markup contains the fragment's markup
         */
        public SignedOutFragment(String id, String markupId, MarkupContainer markupProvider) {
            super(id, markupId, markupProvider);
            add(new BookmarkablePageLink<Void>("sign-in", ((PortalApplication) getApplication()).getSignInPageClass()));
        }

    }

}
