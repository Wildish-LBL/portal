/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.net.URI;
import java.util.Calendar;
import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;

import com.hp.hpl.jena.rdf.model.Statement;

/**
 * @author piotrhol
 *
 */
public class Annotation
	extends AggregatedResource
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8821418401311175036L;

	private URI bodyURI;

	private LoadableDetachableModel<List<Statement>> bodyModel;


	public Annotation(URI uri, Calendar created, String creator, String name, URI bodyURI)
	{
		super(uri, created, creator, name, 0);
		this.bodyURI = bodyURI;

		bodyModel = new LoadableDetachableModel<List<Statement>>() {

			private static final long serialVersionUID = 4142916952621994965L;


			@Override
			protected List<Statement> load()
			{
				return RoFactory.createAnnotationBody(getBodyURI());
			}
		};
	}


	public Annotation()
	{
		super();
	}


	/**
	 * @return the bodyURI
	 */
	public URI getBodyURI()
	{
		return bodyURI;
	}


	/* (non-Javadoc)
	 * @see pl.psnc.dl.wf4ever.portal.model.AggregatedResource#getDownloadURI()
	 */
	@Override
	public URI getDownloadURI()
	{
		return getURI();
	}


	/* (non-Javadoc)
	 * @see pl.psnc.dl.wf4ever.portal.model.AggregatedResource#isWorkflow()
	 */
	@Override
	public boolean isWorkflow()
	{
		return false;
	}


	@Override
	public String getSizeFormatted()
	{
		return "--";
	}


	public List<Statement> getBody()
	{
		return bodyModel.getObject();
	}
}
