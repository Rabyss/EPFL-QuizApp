package epfl.sweng.servercomm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

/**
 * 
 * Class that contains all the parameters
 * 
 */
public class RequestContext implements Serializable {

	private static final long serialVersionUID = -6771365916706236954L;
	private String mServerURL;
	private HashMap<String, String> mHeaders;
	private HttpEntity mEntity;

	public RequestContext(String serverURL, HttpEntity entity) {
		mServerURL = serverURL;
		mEntity = entity;
		mHeaders = new HashMap<String, String>();
	}

	public RequestContext(String serverURL) {
		mServerURL = serverURL;
		mEntity = null;
		mHeaders = new HashMap<String, String>();
	}

	public RequestContext() {
		mServerURL = null;
		mEntity = null;
		mHeaders = new HashMap<String, String>();
	}

	public void addHeader(String name, String value) {
		mHeaders.put(name, value);
	}

	public void addHeader(Header header) {
		mHeaders.put(header.getName(), header.getValue());
	}

	public void setServerURL(String serverURL) {
		mServerURL = serverURL;
	}

	public void setEntity(HttpEntity entity) {
		mEntity = entity;
	}

	public String getServerURL() {
		return mServerURL;
	}

	public HttpEntity getEntity() {
		return mEntity;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, String> getHeaders() {
		return (HashMap<String, String>) mHeaders.clone();
	}

	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.writeObject(mServerURL);
		stream.writeObject(mHeaders);
		stream.writeObject(EntityUtils.toString(mEntity));
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream stream) throws IOException {
		try {
			mServerURL = (String) stream.readObject();
			mHeaders = (HashMap<String, String>) stream.readObject();
			mEntity = new StringEntity((String) stream.readObject());
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		}

	}

}
