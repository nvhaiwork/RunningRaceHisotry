package com.runningracehisotry.webservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.runningracehisotry.utilities.LogUtil;

public class ServiceApi {
    private IWsdl2CodeEvents eventHandler;
    private int timeOut = 60000;
    private String logTag = "ServiceApi";

//    public static final String SERVICE_URL = "http://prr.zodinet.com";
    public static final String SERVICE_URL = "http://runningracehistory.com";

    // User API
    public static final String API_REGISTER = "/user/register";
    public static final String API_OAUTH_REGISTER = "/oauth/register";
    public static final String API_LOGIN = "/user/login";
    public static final String API_GET_USER_PROFILE = "/user/getUserProfile";
    public static final String API_UPDATE_USER_PROFILE = "/user/";
    public static final String API_GET_ALL_RUNNERS = "/user";
    public static final String API_FORGOT_PASSWORD = "/user/forgotPassword";
    public static final String API_UPLOAD_IMAGE = "/upload";

    // Shoes API
    public static final String API_ADD_SHOES = "/shoes";
    public static final String API_GET_ALL_SHOES_WITH_RELATE_OBJ = "/shoes";
    public static final String API_GET_ALL_SHOES = "/shoes/getAllShoes";
    public static final String API_UPDATE_SHOES = "/shoes/";
    public static final String API_DELETE_SHOES_BY_ID = "/shoes/";
    public static final String API_GET_SHOES_BY_ID = "/shoes/";

    // Race API
    public static final String API_GET_RACE_BY_ID = "/race/";
    public static final String API_ADD_RACE = "/race";
    public static final String API_UPDATE_RACE = "/race/";
    public static final String API_DELETE_RACE_BY_ID = "/race/";
    public static final String API_GET_RACES_BY_TYPE = "/race/getRacesByType";
    //quynt3 declare
    public static final String API_GET_FRIEND_RUNNER = "/user/friendRunner";

    // GROUP API
    public static final String API_GET_ALL_GROUP = "/group";
    public static final String API_GET_GROUP_OF_USER = "/groupMember/getByGroupId";
    public static final String API_GET_ABOUT_US = "/content/getContent?name=AboutUs";
    public static final String API_GET_LIKE_OF_RACE = "/like/getByRaceId";

    public static final String API_ADD_GROUP = "/group";
    public static final String API_ADD_GROUP_MEMBER = "/groupMember";
    public static final String API_ADD_LIKE_OF_RACE = "/like";
    public static final String API_REMOVE_LIKE_OF_RACE = "/like/unlike";
    public ServiceApi(IWsdl2CodeEvents eventHandler) {
        this.eventHandler = eventHandler;
    }

    /**
     * Register
     *
     * @param name
     * @param password
     * @param email
     * @throws Exception
     */
    public void Register(String name, String password, String email) throws Exception {
        if (this.eventHandler == null) {
            throw new Exception("Async Methods Requires IWsdl2CodeEvents");
        }
        RegisterAsync(name, password, email);
    }

    private void RegisterAsync(final String name, final String password, final String email) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return callApiRegister(name, password, email);
            }

            @Override
            protected void onPostExecute(String result) {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null) {
                    eventHandler.Wsdl2CodeFinished(ServiceConstants.METHOD_REGISTER, result);
                }
            }
        }.execute();
    }

    private String callApiRegister(String name, String password, String email) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("email", email));

        try {
            HttpPost httpPost = new HttpPost(SERVICE_URL + API_REGISTER);
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            LogUtil.i(logTag, "Register URI : " + httpPost.getURI());

            HttpParams timeoutParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(timeoutParams, timeOut);
            HttpConnectionParams.setSoTimeout(timeoutParams, timeOut);
            HttpClient httpClient = new DefaultHttpClient(timeoutParams);
            HttpResponse response = httpClient.execute(httpPost);

            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            eventHandler.Wsdl2CodeFinishedWithException(e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Login
     *
     * @param name
     * @param password
     * @throws Exception
     */
    public void Login(String name, String password) throws Exception {
        if (this.eventHandler == null) {
            throw new Exception("Async Methods Requires IWsdl2CodeEvents");
        }
        LoginAsync(name, password);
    }

    private void LoginAsync(final String name, final String password) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return callApiLogin(name, password);
            }

            @Override
            protected void onPostExecute(String result) {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null) {
                    eventHandler.Wsdl2CodeFinished(ServiceConstants.METHOD_LOGIN, result);
                }
            }
        }.execute();
    }

    private String callApiLogin(String name, String password) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("password", password));

        try {
            HttpPost httpPost = new HttpPost(SERVICE_URL + API_LOGIN);
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            LogUtil.i(logTag, "Login URI : " + httpPost.getURI());

            HttpParams timeoutParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(timeoutParams, timeOut);
            HttpConnectionParams.setSoTimeout(timeoutParams, timeOut);
            HttpClient httpClient = new DefaultHttpClient(timeoutParams);
            HttpResponse response = httpClient.execute(httpPost);

            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            eventHandler.Wsdl2CodeFinishedWithException(e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * getUserProfile return profile of current user.
     * edit by NTQ
     * @throws Exception
     */
    public void getUserProfile(String name, String password) throws Exception {
        if (this.eventHandler == null) {
            throw new Exception("Async Methods Requires IWsdl2CodeEvents");
        }
        getUserProfileAsync(name, password);
    }

    private void getUserProfileAsync(final String name, final String password) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return callApiGetUserProfile(name, password);
            }

            @Override
            protected void onPostExecute(String result) {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null) {
                    eventHandler.Wsdl2CodeFinished(ServiceConstants.METHOD_GET_USER_PROFILE, result);
                }
            }
        }.execute();
    }

    private String callApiGetUserProfile(String name, String password) {
        try {
            HttpGet httpGet = new HttpGet(SERVICE_URL + API_GET_USER_PROFILE);
            String token = name + ":" + password;
            httpGet.setHeader("Authorization", "Basic "+ Base64.encodeToString(token.getBytes(), Base64.NO_WRAP));
            LogUtil.i("WebService", "getUserProfile URI : " + httpGet.getURI());

            HttpParams timeoutParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(timeoutParams, timeOut);
            HttpConnectionParams.setSoTimeout(timeoutParams, timeOut);
            HttpClient httpClient = new DefaultHttpClient(timeoutParams);

            ResponseHandler<String> res=new BasicResponseHandler();
            String response = httpClient.execute(httpGet, res);

            return response;
        } catch (IOException e) {
            eventHandler.Wsdl2CodeFinishedWithException(e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * updateUserProfile
     *
     * @param email
     * @param fullName
     * @param userId
     * @param name
     * @param profileImage
     * @throws Exception
     */
    public void updateUserProfile(String email, String fullName, String userId, String name,
                                  String profileImage) throws Exception {
        if (this.eventHandler == null) {
            throw new Exception("Async Methods Requires IWsdl2CodeEvents");
        }
        updateUserProfileAsync(email, fullName, userId, name, profileImage);
    }

    private void updateUserProfileAsync(final String email, final String fullName, final String userId,
                                        final String name, final String profileImage) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return callApiUpdateUserProfile(email, fullName, userId, name, profileImage);
            }

            @Override
            protected void onPostExecute(String result) {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null) {
                    eventHandler.Wsdl2CodeFinished(ServiceConstants.METHOD_UPDATE_USER_PROFILE, result);
                }
            }
        }.execute();
    }

    private String callApiUpdateUserProfile(String email, String fullName, String userId,
                                            String name, String profileImage) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("full_name", fullName));
        params.add(new BasicNameValuePair("id", userId));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("profile_image", profileImage));

        try {
            HttpPost httpPost = new HttpPost(SERVICE_URL + API_UPDATE_USER_PROFILE + userId);
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            LogUtil.i("WebService", "updateUserProfile URI : " + httpPost.getURI());

            HttpParams timeoutParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(timeoutParams, timeOut);
            HttpConnectionParams.setSoTimeout(timeoutParams, timeOut);
            HttpClient httpClient = new DefaultHttpClient(timeoutParams);
            HttpResponse response = httpClient.execute(httpPost);

            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            eventHandler.Wsdl2CodeFinishedWithException(e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * getAllRunners
     *
     * @throws Exception
     */
    public void getAllRunners() throws Exception {
        if (this.eventHandler == null) {
            throw new Exception("Async Methods Requires IWsdl2CodeEvents");
        }
        getAllRunnersAsync();
    }

    private void getAllRunnersAsync() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return callApiGetAllRunners();
            }

            @Override
            protected void onPostExecute(String result) {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null) {
                    eventHandler.Wsdl2CodeFinished(ServiceConstants.METHOD_GET_ALL_RUNNERS, result);
                }
            }
        }.execute();
    }

    private String callApiGetAllRunners() {
        try {
            HttpGet httpGet = new HttpGet(SERVICE_URL + API_GET_ALL_RUNNERS);
            LogUtil.i("WebService", "getAllRunners URI : " + httpGet.getURI());

            HttpParams timeoutParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(timeoutParams, timeOut);
            HttpConnectionParams.setSoTimeout(timeoutParams, timeOut);
            HttpClient httpClient = new DefaultHttpClient(timeoutParams);
            HttpResponse response = httpClient.execute(httpGet);

            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            eventHandler.Wsdl2CodeFinishedWithException(e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * addShoes
     *
     * @param brand
     * @param imageUrl
     * @param milesHistories
     * @param model
     * @throws Exception
     */
    public void addShoes(String brand, String imageUrl, String milesHistories,
                         String model) throws Exception {
        if (this.eventHandler == null) {
            throw new Exception("Async Methods Requires IWsdl2CodeEvents");
        }
        addShoesAsync(brand, imageUrl, milesHistories, model);
    }

    private void addShoesAsync(final String brand, final String imageUrl,
                               final String milesHistories, final String model) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return callApiAddShoes(brand, imageUrl, milesHistories, model);
            }

            @Override
            protected void onPostExecute(String result) {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null) {
                    eventHandler.Wsdl2CodeFinished(ServiceConstants.METHOD_ADD_SHOES, result);
                }
            }
        }.execute();
    }

    private String callApiAddShoes(String brand, String imageUrl,
                                   String milesHistories, String model) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("brand", brand));
        params.add(new BasicNameValuePair("image_url", imageUrl));
        params.add(new BasicNameValuePair("miles_histories", milesHistories));
        params.add(new BasicNameValuePair("model", model));

        try {
            HttpPost httpPost = new HttpPost(SERVICE_URL + API_ADD_SHOES);
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            LogUtil.i(logTag, "addShoes URI : " + httpPost.getURI());

            HttpParams timeoutParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(timeoutParams, timeOut);
            HttpConnectionParams.setSoTimeout(timeoutParams, timeOut);
            HttpClient httpClient = new DefaultHttpClient(timeoutParams);
            HttpResponse response = httpClient.execute(httpPost);

            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            eventHandler.Wsdl2CodeFinishedWithException(e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * getAllShoesWithRelateObj
     *
     * @throws Exception
     */
    public void getAllShoesWithRelateObj() throws Exception {
        if (this.eventHandler == null) {
            throw new Exception("Async Methods Requires IWsdl2CodeEvents");
        }
        getAllShoesWithRelateObjAsync();
    }

    private void getAllShoesWithRelateObjAsync() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return callApiGetAllShoesWithRelateObj();
            }

            @Override
            protected void onPostExecute(String result) {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null) {
                    eventHandler.Wsdl2CodeFinished(
                            ServiceConstants.METHOD_GET_ALL_SHOES_WITH_RELATE_OBJ, result);
                }
            }
        }.execute();
    }

    private String callApiGetAllShoesWithRelateObj() {
        try {
            HttpGet httpGet = new HttpGet(SERVICE_URL + API_GET_ALL_SHOES_WITH_RELATE_OBJ);
            LogUtil.i("WebService", "getAllShoesWithRelateObj URI : " + httpGet.getURI());

            HttpParams timeoutParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(timeoutParams, timeOut);
            HttpConnectionParams.setSoTimeout(timeoutParams, timeOut);
            HttpClient httpClient = new DefaultHttpClient(timeoutParams);
            HttpResponse response = httpClient.execute(httpGet);

            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            eventHandler.Wsdl2CodeFinishedWithException(e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * getShoesById
     * @param shoesId
     * @throws Exception
     */
    public void getShoesById(String shoesId) throws Exception {
        if (this.eventHandler == null) {
            throw new Exception("Async Methods Requires IWsdl2CodeEvents");
        }
        getShoesByIdAsync(shoesId);
    }

    private void getShoesByIdAsync(final String shoesId) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return callApiGetShoesById(shoesId);
            }

            @Override
            protected void onPostExecute(String result) {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null) {
                    eventHandler.Wsdl2CodeFinished(ServiceConstants.METHOD_GET_SHOES_BY_ID, result);
                }
            }
        }.execute();
    }

    private String callApiGetShoesById(String shoesId) {
        try {
            HttpGet httpGet = new HttpGet(SERVICE_URL + API_GET_SHOES_BY_ID + shoesId);
            LogUtil.i("WebService", "getShoesById URI : " + httpGet.getURI());

            HttpParams timeoutParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(timeoutParams, timeOut);
            HttpConnectionParams.setSoTimeout(timeoutParams, timeOut);
            HttpClient httpClient = new DefaultHttpClient(timeoutParams);
            HttpResponse response = httpClient.execute(httpGet);

            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            eventHandler.Wsdl2CodeFinishedWithException(e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * getAllShoes
     *
     * @throws Exception
     */
    public void getAllShoes() throws Exception {
        if (this.eventHandler == null) {
            throw new Exception("Async Methods Requires IWsdl2CodeEvents");
        }
        getAllShoesAsync();
    }

    private void getAllShoesAsync() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return callApiGetAllShoes();
            }

            @Override
            protected void onPostExecute(String result) {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null) {
                    eventHandler.Wsdl2CodeFinished(ServiceConstants.METHOD_GET_ALL_SHOES, result);
                }
            }
        }.execute();
    }

    private String callApiGetAllShoes() {
        try {
            HttpGet httpGet = new HttpGet(SERVICE_URL + API_GET_ALL_SHOES);
            LogUtil.i("WebService", "getAllShoes URI : " + httpGet.getURI());

            HttpParams timeoutParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(timeoutParams, timeOut);
            HttpConnectionParams.setSoTimeout(timeoutParams, timeOut);
            HttpClient httpClient = new DefaultHttpClient(timeoutParams);
            HttpResponse response = httpClient.execute(httpGet);

            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            eventHandler.Wsdl2CodeFinishedWithException(e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * updateShoes
     * @param brand
     * @param shoesId
     * @param imageUrl
     * @param milesHistories
     * @param milesOnShoes
     * @param model
     * @param userId
     * @throws Exception
     */
    public void updateShoes(String brand, String shoesId, String imageUrl, String milesHistories,
                            String milesOnShoes, String model, String userId) throws Exception {
        if (this.eventHandler == null) {
            throw new Exception("Async Methods Requires IWsdl2CodeEvents");
        }
        updateShoesAsync(brand, shoesId, imageUrl, milesHistories, milesOnShoes, model, userId);
    }

    private void updateShoesAsync(final String brand, final String shoesId, final String imageUrl,
                                  final String milesHistories, final String milesOnShoes,
                                  final String model, final String userId) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return callApiUpdateShoes(brand, shoesId, imageUrl,
                        milesHistories, milesOnShoes, model, userId);
            }

            @Override
            protected void onPostExecute(String result) {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null) {
                    eventHandler.Wsdl2CodeFinished(ServiceConstants.METHOD_UPDATE_SHOES, result);
                }
            }
        }.execute();
    }

    private String callApiUpdateShoes(String brand, String shoesId, String imageUrl,
                                      String milesHistories, String milesOnShoes, String model, String userId) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("brand", brand));
        params.add(new BasicNameValuePair("id", shoesId));
        params.add(new BasicNameValuePair("image_url", imageUrl));
        params.add(new BasicNameValuePair("miles_histories", milesHistories));
        params.add(new BasicNameValuePair("miles_on_shoes", milesOnShoes));
        params.add(new BasicNameValuePair("model", model));
        params.add(new BasicNameValuePair("user_id", userId));

        try {
            HttpPut httpPut = new HttpPut(SERVICE_URL + API_UPDATE_SHOES + shoesId);
            httpPut.setEntity(new UrlEncodedFormEntity(params));
            LogUtil.i(logTag, "updateShoes URI : " + httpPut.getURI());

            HttpParams timeoutParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(timeoutParams, timeOut);
            HttpConnectionParams.setSoTimeout(timeoutParams, timeOut);
            HttpClient httpClient = new DefaultHttpClient(timeoutParams);
            HttpResponse response = httpClient.execute(httpPut);

            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            eventHandler.Wsdl2CodeFinishedWithException(e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * deleteShoesById
     * @param shoesId
     * @throws Exception
     */
    public void deleteShoesById(String shoesId) throws Exception {
        if (this.eventHandler == null) {
            throw new Exception("Async Methods Requires IWsdl2CodeEvents");
        }
        deleteShoesByIdAsync(shoesId);
    }

    private void deleteShoesByIdAsync(final String shoesId) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return callApiDeleteShoesById(shoesId);
            }

            @Override
            protected void onPostExecute(String result) {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null) {
                    eventHandler.Wsdl2CodeFinished(ServiceConstants.METHOD_DELETE_SHOES_BY_ID, result);
                }
            }
        }.execute();
    }

    private String callApiDeleteShoesById(String shoesId) {
        try {
            HttpDelete httpDelete = new HttpDelete(SERVICE_URL + API_DELETE_SHOES_BY_ID + shoesId);
            LogUtil.i(logTag, "deleteShoesById URI : " + httpDelete.getURI());

            HttpParams timeoutParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(timeoutParams, timeOut);
            HttpConnectionParams.setSoTimeout(timeoutParams, timeOut);
            HttpClient httpClient = new DefaultHttpClient(timeoutParams);
            HttpResponse response = httpClient.execute(httpDelete);

            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            eventHandler.Wsdl2CodeFinishedWithException(e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * getRaceByType
     */

    /**
     * getRaceById
     * @param raceId
     * @throws Exception
     */
    public void getRaceById(String raceId) throws Exception {
        if (this.eventHandler == null) {
            throw new Exception("Async Methods Requires IWsdl2CodeEvents");
        }
        getRaceByIdAsync(raceId);
    }

    private void getRaceByIdAsync(final String raceId) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return callApiGetRaceById(raceId);
            }

            @Override
            protected void onPostExecute(String result) {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null) {
                    eventHandler.Wsdl2CodeFinished(ServiceConstants.METHOD_GET_RACE_BY_ID, result);
                }
            }
        }.execute();
    }

    private String callApiGetRaceById(String raceId) {
        try {
            HttpGet httpGet = new HttpGet(SERVICE_URL + API_GET_RACE_BY_ID + raceId);
            LogUtil.i("WebService", "getUserProfile URI : " + httpGet.getURI());

            HttpParams timeoutParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(timeoutParams, timeOut);
            HttpConnectionParams.setSoTimeout(timeoutParams, timeOut);
            HttpClient httpClient = new DefaultHttpClient(timeoutParams);
            HttpResponse response = httpClient.execute(httpGet);

            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            eventHandler.Wsdl2CodeFinishedWithException(e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * addRace
     * @param bibUrl
     * @param city
     * @param eventType
     * @param finisherDateTime
     * @param medalUrl
     * @param raceName
     * @param personUrl
     * @param raceDate
     * @param shoesId
     * @param state
     * @param website
     * @throws Exception
     */
    public void addRace(String bibUrl, String city, String eventType, String finisherDateTime,
                        String medalUrl, String raceName, String personUrl, String raceDate,
                        String shoesId, String state, String website) throws Exception {
        if (this.eventHandler == null) {
            throw new Exception("Async Methods Requires IWsdl2CodeEvents");
        }
        addRaceAsync(bibUrl, city, eventType, finisherDateTime, medalUrl, raceName,
                personUrl, raceDate, shoesId, state, website);
    }

    private void addRaceAsync(final String bibUrl, final String city, final String eventType,
                              final String finisherDateTime, final String medalUrl, final String raceName,
                              final String personUrl, final String raceDate,
                              final String shoesId, final String state, final String website) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return callApiAddRace(bibUrl, city, eventType, finisherDateTime, medalUrl,
                        raceName, personUrl, raceDate, shoesId, state, website);
            }

            @Override
            protected void onPostExecute(String result) {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null) {
                    eventHandler.Wsdl2CodeFinished(ServiceConstants.METHOD_ADD_RACE, result);
                }
            }
        }.execute();
    }

    private String callApiAddRace(String bibUrl, String city, String eventType, String finisherDateTime,
                                  String medalUrl, String raceName, String personUrl, String raceDate,
                                  String shoesId, String state, String website) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("bib_url", bibUrl));
        params.add(new BasicNameValuePair("city", city));
        params.add(new BasicNameValuePair("event_type", eventType));
        params.add(new BasicNameValuePair("finisher_date_time", finisherDateTime));
        params.add(new BasicNameValuePair("medal_url", medalUrl));
        params.add(new BasicNameValuePair("race_name", raceName));
        params.add(new BasicNameValuePair("person_url", personUrl));
        params.add(new BasicNameValuePair("race_date", raceDate));
        params.add(new BasicNameValuePair("shoes_id", shoesId));
        params.add(new BasicNameValuePair("state", state));
        params.add(new BasicNameValuePair("website", website));

        try {
            HttpPost httpPost = new HttpPost(SERVICE_URL + API_ADD_RACE);
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            LogUtil.i("WebService", "addRace URI : " + httpPost.getURI());

            HttpParams timeoutParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(timeoutParams, timeOut);
            HttpConnectionParams.setSoTimeout(timeoutParams, timeOut);
            HttpClient httpClient = new DefaultHttpClient(timeoutParams);
            HttpResponse response = httpClient.execute(httpPost);

            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            eventHandler.Wsdl2CodeFinishedWithException(e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * updateRace
     * @param bibUrl
     * @param city
     * @param createAt
     * @param eventType
     * @param finisherDateTime
     * @param raceId
     * @param medalUrl
     * @param raceName
     * @param personUrl
     * @param raceDate
     * @param shoesId
     * @param state
     * @param updateAt
     * @param userId
     * @param website
     * @throws Exception
     */
    public void updateRace(String bibUrl, String city, String createAt, String eventType,
                           String finisherDateTime, String raceId, String medalUrl, String raceName,
                           String personUrl, String raceDate, String shoesId, String state,
                           String updateAt, String userId, String website) throws Exception {
        if (this.eventHandler == null) {
            throw new Exception("Async Methods Requires IWsdl2CodeEvents");
        }
        updateRaceAsync(bibUrl, city, createAt, eventType, finisherDateTime, raceId, medalUrl, raceName,
                personUrl, raceDate, shoesId, state, updateAt, userId, website);
    }

    private void updateRaceAsync(final String bibUrl, final String city, final String createAt,
                                 final String eventType, final String finisherDateTime,
                                 final String raceId, final String medalUrl, final String raceName,
                                 final String personUrl, final String raceDate,
                                 final String shoesId, final String state, final String updateAt,
                                 final String userId, final String website) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return callApiUpdateRace(bibUrl, city, createAt, eventType, finisherDateTime,
                        raceId, medalUrl, raceName, personUrl, raceDate,
                        shoesId, state, updateAt, userId, website);
            }

            @Override
            protected void onPostExecute(String result) {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null) {
                    eventHandler.Wsdl2CodeFinished(ServiceConstants.METHOD_UPDATE_RACE, result);
                }
            }
        }.execute();
    }

    private String callApiUpdateRace(String bibUrl, String city, String createAt, String eventType,
                                     String finisherDateTime, String raceId, String medalUrl, String raceName,
                                     String personUrl, String raceDate, String shoesId, String state,
                                     String updateAt, String userId, String website) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("bib_url", bibUrl));
        params.add(new BasicNameValuePair("city", city));
        params.add(new BasicNameValuePair("create_at", createAt));
        params.add(new BasicNameValuePair("event_type", eventType));
        params.add(new BasicNameValuePair("finisher_date_time", finisherDateTime));
        params.add(new BasicNameValuePair("id", raceId));
        params.add(new BasicNameValuePair("medal_url", medalUrl));
        params.add(new BasicNameValuePair("race_name", raceName));
        params.add(new BasicNameValuePair("person_url", personUrl));
        params.add(new BasicNameValuePair("race_date", raceDate));
        params.add(new BasicNameValuePair("shoes_id", shoesId));
        params.add(new BasicNameValuePair("state", state));
        params.add(new BasicNameValuePair("update_at", updateAt));
        params.add(new BasicNameValuePair("user_id", userId));
        params.add(new BasicNameValuePair("website", website));

        try {
            HttpPut httpPut = new HttpPut(SERVICE_URL + API_UPDATE_RACE + raceId);
            httpPut.setEntity(new UrlEncodedFormEntity(params));
            LogUtil.i("WebService", "updateRace URI : " + httpPut.getURI());

            HttpParams timeoutParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(timeoutParams, timeOut);
            HttpConnectionParams.setSoTimeout(timeoutParams, timeOut);
            HttpClient httpClient = new DefaultHttpClient(timeoutParams);
            HttpResponse response = httpClient.execute(httpPut);

            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            eventHandler.Wsdl2CodeFinishedWithException(e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * deleteRaceById
     * @param raceId
     * @throws Exception
     */
    public void deleteRaceById(String raceId) throws Exception {
        if (this.eventHandler == null) {
            throw new Exception("Async Methods Requires IWsdl2CodeEvents");
        }
        deleteRaceByIdAsync(raceId);
    }

    private void deleteRaceByIdAsync(final String raceId) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return callApiDeleteRaceById(raceId);
            }

            @Override
            protected void onPostExecute(String result) {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null) {
                    eventHandler.Wsdl2CodeFinished(ServiceConstants.METHOD_DELETE_RACE_BY_ID, result);
                }
            }
        }.execute();
    }

    private String callApiDeleteRaceById(String raceId) {

        try {
            HttpDelete httpDelete = new HttpDelete(SERVICE_URL + API_DELETE_RACE_BY_ID + raceId);
            LogUtil.i("WebService", "deleteRaceById URI : " + httpDelete.getURI());

            HttpParams timeoutParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(timeoutParams, timeOut);
            HttpConnectionParams.setSoTimeout(timeoutParams, timeOut);
            HttpClient httpClient = new DefaultHttpClient(timeoutParams);
            HttpResponse response = httpClient.execute(httpDelete);

            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            eventHandler.Wsdl2CodeFinishedWithException(e);
            e.printStackTrace();
        }
        return null;
    }

    // API 19 to 28
    // API 27

    /**
     * author: NTQ
     * forgot Password
     * @param name
     * @param password
     * @param email
     * @throws Exception
     */
    public void forgotPassword(String name, String password, String email) throws Exception {
        if (this.eventHandler == null) {
            throw new Exception("Async Methods Requires IWsdl2CodeEvents callForgotPassword");
        }
        forgotPasswordAsync(name, password, email);
    }

    /**
     * author: NTQ
     * Asyntask forgotPAssword
     * @param name
     * @param password
     * @param email
     */
    private void forgotPasswordAsync(final String name, final String password, final String email) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return callForgotPassword(name, password, email);
            }

            @Override
            protected void onPostExecute(String result) {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null) {
                    eventHandler.Wsdl2CodeFinished(ServiceConstants.METHOD_REGISTER, result);
                }
            }
        }.execute();
    }

    private String callForgotPassword(String name, String password, String email) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", email));

        try {
            HttpPost httpPost = new HttpPost(SERVICE_URL + API_FORGOT_PASSWORD);
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            LogUtil.i(logTag, "callForgotPassword URI : " + httpPost.getURI());

            HttpParams timeoutParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(timeoutParams, timeOut);
            HttpConnectionParams.setSoTimeout(timeoutParams, timeOut);
            HttpClient httpClient = new DefaultHttpClient(timeoutParams);
            HttpResponse response = httpClient.execute(httpPost);

            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            eventHandler.Wsdl2CodeFinishedWithException(e);
            e.printStackTrace();
        }
        return null;
    }
}