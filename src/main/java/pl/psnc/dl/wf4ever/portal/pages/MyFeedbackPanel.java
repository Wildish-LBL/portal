/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages;

import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 * @author piotrhol
 *
 */
public class MyFeedbackPanel
	extends FeedbackPanel
{

	private static final long serialVersionUID = -4262356535601478890L;


	public MyFeedbackPanel(String id)
	{
		super(id);
	}


	@Override
	protected String getCSSClass(FeedbackMessage message)
	{
		switch (message.getLevel()) {
			case FeedbackMessage.DEBUG:
			case FeedbackMessage.WARNING:
				return "alert-message block-message warning";
			case FeedbackMessage.FATAL:
			case FeedbackMessage.ERROR:
				return "alert-message block-message error";
			case FeedbackMessage.INFO:
				return "alert-message block-message info";
			case FeedbackMessage.SUCCESS:
				return "alert-message block-message success";
			default:
				return "alert-message block-message warning";

		}
	}

}
