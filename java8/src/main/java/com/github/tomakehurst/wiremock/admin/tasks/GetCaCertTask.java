package com.github.tomakehurst.wiremock.admin.tasks;

import com.github.tomakehurst.wiremock.admin.AdminTask;
import com.github.tomakehurst.wiremock.admin.model.PathParams;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.BrowserProxySettings;
import com.github.tomakehurst.wiremock.common.KeyStoreSettings;
import com.github.tomakehurst.wiremock.core.Admin;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.http.ssl.X509KeyStore;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.cert.X509Certificate;
import java.util.Base64;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;

public class GetCaCertTask implements AdminTask {

    private static final Base64.Encoder BASE64_ENCODER = Base64.getMimeEncoder(64, new byte[]{'\r', '\n'});

    @Override
    public ResponseDefinition execute(Admin admin, Request request, PathParams pathParams) {
        BrowserProxySettings browserProxySettings = admin.getOptions().browserProxySettings();
        KeyStoreSettings caKeyStore = browserProxySettings.caKeyStore();
        try {
            X509KeyStore x509KeyStore = new X509KeyStore(caKeyStore.loadStore(), caKeyStore.password().toCharArray());
            X509Certificate certificate = x509KeyStore.getCertificateAuthority().certificateChain()[0];
            return new ResponseDefinitionBuilder()
                    .withStatus(HTTP_OK)
                    .withHeader("Content-Type", "application/x-pem-file")
                    .withBody(
                        "-----BEGIN CERTIFICATE-----\r\n" +
                        BASE64_ENCODER.encodeToString(certificate.getEncoded()) + "\r\n" +
                        "-----END CERTIFICATE-----"
                    )
                    .build();
        } catch (Exception e) {
            String message = "Failed to export certificate authority cert from " + caKeyStore.path();
            admin.getOptions().notifier().error(message, e);
            return new ResponseDefinition(HTTP_INTERNAL_ERROR, message);
        }
    }
}
