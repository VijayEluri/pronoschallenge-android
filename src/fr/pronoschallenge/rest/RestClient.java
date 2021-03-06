package fr.pronoschallenge.rest;

import android.util.Log;
import fr.pronoschallenge.util.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RestClient {

    public static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if(!"".equals(line)) {
                    sb.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * @param url
     * @return
     */
    public static String get(String url) {
        return get(url, null, null);
    }

    /**
     * @param url
     * @return
     */
    public static String get(String url, String username, String password) {
        String result = null;

        HttpClient httpclient = new DefaultHttpClient();

        // Prepare a request object
        HttpGet httpget = new HttpGet(url);

        if (username != null && password != null) {
            String encodedCredentials = Base64.encodeBytes((username+":"+password).getBytes());
            httpget.setHeader("Authorization", "Basic " + encodedCredentials);
        }

        // Execute the request
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);
            // Examine the response status
            Log.i("PronosChallenge", response.getStatusLine().toString());

            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release

            if (entity != null) {

                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                result = convertStreamToString(instream);
                Log.i("PronosChallenge", result);

                // Closing the input stream will trigger connection release
                instream.close();
            }


        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }

    public static HttpResponse postForm(String url, HashMap<String, String> hm, String username, String password) {

        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpResponse response = null;

        if (username != null && password != null) {
            httpClient.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials(username, password));
        }

        HttpPost postMethod = new HttpPost(url);

        if (hm == null) return null;

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            Iterator<String> it = hm.keySet().iterator();
            String k, v;
            while (it.hasNext()) {
                k = it.next();
                v = hm.get(k);
                nameValuePairs.add(new BasicNameValuePair(k, v));
            }

            postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            response = httpClient.execute(postMethod);

            Log.i("PronosChallenge", "STATUS CODE: " + String.valueOf(response.getStatusLine().getStatusCode()));

        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }

        return response;
    }

    public static HttpResponse postData(String url, String data, String username, String password) {

        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpResponse response = null;

        HttpPost postMethod = new HttpPost(url);

        if (username != null && password != null) {
            //httpClient.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
            //        new UsernamePasswordCredentials(username, password));
            String encodedCredentials = Base64.encodeBytes((username+":"+password).getBytes());
            postMethod.setHeader("Authorization", "Basic " + encodedCredentials);
        }

        if (data == null) return null;

        try {
            postMethod.setEntity(new StringEntity(data));

            response = httpClient.execute(postMethod);

            Log.i("PronosChallenge", "STATUS CODE: " + String.valueOf(response.getStatusLine().getStatusCode()));

        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }

        return response;
    }
}