package pl.psnc.dl.wf4ever.portal.pages.ro;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.purl.wf4ever.rosrs.client.ResearchObject;

import pl.psnc.dl.wf4ever.portal.PortalApplication;

public class IvisibleAccessControlPanel extends Panel {

	/** id. */
	private static final long serialVersionUID = -3775797988389365540L;

	/** Logger. */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(IvisibleAccessControlPanel.class);

	private IModel<ResearchObject> roModel;
	PortalApplication app;

	public IvisibleAccessControlPanel(String id, IModel<ResearchObject> model) {
		super(id);
		this.roModel = model;
	}

}
