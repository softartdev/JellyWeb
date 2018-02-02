package ru.datakrat.jellyweb;

import android.app.Application;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.net.ssl.HttpsURLConnection;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        setupSslConnections();
        Security.setProperty("ssl.SocketFactory.provider", "ru.datakrat.jellyweb.TLSSocketFactory");
    }

    private void setupSslConnections() {
        try {
            TLSSocketFactory tlsSocketFactory = new TLSSocketFactory();
            HttpsURLConnection.setDefaultSSLSocketFactory(tlsSocketFactory);
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
