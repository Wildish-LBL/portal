package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.pages.ro.roexplorer.behaviours.IAjaxLinkListener;

final class SnapshotListener implements IAjaxLinkListener, Serializable {

    /** id. */
    private static final long serialVersionUID = 3029656930713733870L;
    /**
     * 
     */
    private final RoPage roPage;


    /**
     * @param roPage
     */
    SnapshotListener(RoPage roPage) {
        this.roPage = roPage;
    }


    @Override
    public void onAjaxLinkClicked(AjaxRequestTarget target) {
        this.roPage.createSnapshot(target);
    }
}