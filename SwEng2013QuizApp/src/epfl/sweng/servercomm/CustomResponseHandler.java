package epfl.sweng.servercomm;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

public class CustomResponseHandler implements ResponseHandler<ServerResponse> {

	@Override
	public ServerResponse handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		HttpEntity entity = response.getEntity();
		String strEntity = entity == null ? null : EntityUtils.toString(entity);
		return new ServerResponse(strEntity, statusCode);
	}

}
