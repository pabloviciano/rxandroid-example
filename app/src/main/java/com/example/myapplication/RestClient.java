package com.example.myapplication;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class RestClient {
    private OkHttpClient client;


    public List<Temperature> getTemperatures() throws IOException, JSONException {
        HttpUrl.Builder urlBuilder=  HttpUrl.parse("https://samples.openweathermap.org/data/2.5/forecast/forecast").newBuilder();
        urlBuilder.addQueryParameter("p", "London,us");
        urlBuilder.addQueryParameter("appid", "b1b15e88fa797225412429c1c50c122a1");
        Request request = new Request.Builder().url(urlBuilder.build()).build();
        Response response = getClient().newCall(request).execute();
        if(response.isSuccessful()){
            List<Temperature> list = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(response.body().string());
            JSONArray listArray = jsonObject.getJSONArray("list");
            for(int i = 0; i < listArray.length(); i++){
                JSONObject value = listArray.getJSONObject(i);
                value = value.getJSONObject("main");
                list.add(new Temperature(value.getInt("temp_min"), value.getInt("temp_max")));
            }
            return list;
        }else {
            throw new IOException();
        }
    }

    private OkHttpClient getClient() {
        if(client == null) {
            try {
                client = createClient();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
        }
        return client;
    }

    private OkHttpClient createClient() throws NoSuchAlgorithmException, KeyManagementException {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

        final TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };

        // Install the all-trusting trust manager
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        // Create an ssl socket factory with our all-trusting manager
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        okHttpClientBuilder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
        okHttpClientBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        okHttpClientBuilder.addInterceptor(
                new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        );
        return okHttpClientBuilder.build();
    }

    public class Temperature {
        private int temp_min;
        private int temp_max;

        public Temperature() {
        }

        public Temperature(int temp_min, int temp_max) {
            this.temp_min = temp_min;
            this.temp_max = temp_max;
        }

        public int getTemp_min() {
            return temp_min;
        }

        public void setTemp_min(int temp_min) {
            this.temp_min = temp_min;
        }

        public int getTemp_max() {
            return temp_max;
        }

        public void setTemp_max(int temp_max) {
            this.temp_max = temp_max;
        }
    }
}
