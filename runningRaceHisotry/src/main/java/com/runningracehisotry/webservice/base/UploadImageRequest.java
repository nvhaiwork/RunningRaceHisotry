package com.runningracehisotry.webservice.base;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.parse.entity.mime.MultipartEntity;
import com.parse.entity.mime.content.FileBody;
import com.runningracehisotry.RunningRaceApplication;
import com.runningracehisotry.webservice.ServiceApi;
import com.runningracehisotry.webservice.ServiceConstants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.net.URI;
import java.net.URL;

/**
 * Created by manh on 04/09/15.
 */
public class UploadImageRequest extends BasePostRequest {
    private Uri imageUri;

    public UploadImageRequest(Uri imageUri) {
        super(ServiceApi.SERVICE_URL + ServiceApi.API_UPLOAD_IMAGE);
        this.imageUri  = imageUri;
    }

    @Override
    public void run() {
        String responseString = null;

        try{
            URL url = new URL(getAddress());
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());

            mHttpPost.setURI(uri);

            MultipartEntity entity = new MultipartEntity();
            File sourceFile = new File(getRealPathFromURI(imageUri));

            // Adding file data to http body
            entity.addPart("file", new FileBody(sourceFile));
            mHttpPost.setEntity(entity);

            HttpResponse response = getHttpClient().execute(mHttpPost);
            HttpEntity r_entity = response.getEntity();

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // Server response
                responseString = EntityUtils.toString(r_entity);
            } else {
                responseString = "Error occurred! Http Status Code: "
                        + statusCode;
            }

            onCompleted(responseString);

        } catch (Exception ex) {
            onFailed(ex);
        }  finally {
            onEnded();
        }
    }

    @Override
    protected String getRequestName() {
        return ServiceConstants.METHOD_UPLOAD_IMAGE;
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = RunningRaceApplication.getInstance().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}
