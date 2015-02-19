package com.yandex.disk.rest.example;

import android.content.Context;
import android.util.Log;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.yandex.disk.rest.HttpClient;
import com.yandex.disk.rest.TransportClient;
import com.yandex.sslpinning.core.CertificateEvaluationPolicy;
import com.yandex.sslpinning.core.CertificateEvaluatorWithContainer;
import com.yandex.sslpinning.core.CertificateUtil;
import com.yandex.sslpinning.core.PinningTrustManagerFactory;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * TODO remove certificate pinning from public release
 */
@Deprecated
public class TransportClientUtil {

    private static final String TAG = "TransportClientUtil";

    private static final int[] CERTIFICATES = new int[]{
         // TODO
         // R.raw.cert_id_1,
         // R.raw.cert_id_2
    };

    private HttpClient client;

    public TransportClientUtil(Context context)
            throws KeyManagementException, NoSuchAlgorithmException {
        this.client = new HttpClient();

        client.getClient().networkInterceptors().add(new StethoInterceptor());

        initPinning(context);
    }

    public static TransportClient getInstance(final Context context, final Credentials credentials)
            throws NoSuchAlgorithmException, KeyManagementException {
        TransportClientUtil clientUtil = new TransportClientUtil(context);
        return new TransportClient(credentials, clientUtil.client);
    }

    private void initPinning(final Context context)
            throws NoSuchAlgorithmException, KeyManagementException {
        initCertificates(context);

        TrustManager[] trustManagers = {PinningTrustManagerFactory.getInstance()};
        SSLContext sslContext = SSLContext.getInstance(SSLSocketFactory.TLS);
        sslContext.init(null, trustManagers, null);

        client.getClient().setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
        client.getClient().setSslSocketFactory(sslContext.getSocketFactory());
    }

    public static void initCertificates(final Context context) {
        CertificateEvaluatorWithContainer pinnedListContainer = PinningTrustManagerFactory.getInstance().getPinnedlist();
        pinnedListContainer.setEvaluationPolicy(CertificateEvaluationPolicy.PUBLIC_KEY);
        for (int certId : CERTIFICATES) {
            X509Certificate certificate = CertificateUtil.readCertificate(context.getResources().openRawResource(certId));
            Log.d(TAG, "Certificate retrieved for issuer " + certificate.getIssuerDN().toString());
            pinnedListContainer.addCertificate(certificate);
        }
    }
}
