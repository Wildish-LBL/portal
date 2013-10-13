package pl.psnc.dl.wf4ever.portal.utils;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;

public class StaticImage extends WebComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	  * @param id wicket id on the page
	  * @param model reference the external URL from which the image is gotten
	  *          for ex.: "http://images.google.com/img/10293.gif"
	  */
	  public StaticImage(String id, IModel<?> urlModel)
	  {
	    super( id, urlModel );
	  }

	  protected void onComponentTag(ComponentTag tag)
	  {
	    super.onComponentTag( tag );
	    checkComponentTag( tag, "img" );
	    tag.put( "src", getDefaultModelObjectAsString() );
	  }
	  
	  public static final String checkIsImage(String input){
	    	String type="N/A";
			try {
				URL inputAsURL = new URL(input);
				URLConnection urlConn = inputAsURL.openConnection();
				String mimetype = urlConn.getContentType();
			    type = mimetype.split("/")[0];
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return (type.equals("image") ?  input :  "N/A" );
	    }


}
