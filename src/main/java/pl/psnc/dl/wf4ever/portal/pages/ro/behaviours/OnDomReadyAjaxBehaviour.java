package pl.psnc.dl.wf4ever.portal.pages.ro.behaviours;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;

public abstract class OnDomReadyAjaxBehaviour extends AbstractDefaultAjaxBehavior {

    /** id. */
    private static final long serialVersionUID = -2017524102911395368L;

    /** components to refresh. */
    private WebMarkupContainer[] componentsToRefresh;


    public OnDomReadyAjaxBehaviour(WebMarkupContainer... componentsToRefresh) {
        this.componentsToRefresh = componentsToRefresh;
    }


    @Override
    protected void respond(AjaxRequestTarget target) {
        target.add(componentsToRefresh);
    }


    @Override
    public void renderHead(final Component component, final IHeaderResponse response) {
        super.renderHead(component, response);
        response.renderOnDomReadyJavaScript(getCallbackScript().toString());
    }
}
