package demo.hwpush.com.hwpush;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.TokenResult;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements HuaweiApiClient.ConnectionCallbacks, HuaweiApiClient.OnConnectionFailedListener  {

    Handler handler = new Handler();
    HuaweiApiClient client;

    TextView devToken;
    Switch isIgoneCer;
    TextView netResult;
    EditText netUrl;
    Button sendBtn;

    public static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;

        devToken = (TextView) findViewById(R.id.textView5);
        netResult = (TextView) findViewById(R.id.textView7);
        isIgoneCer = (Switch) findViewById(R.id.switch1);
        netUrl = (EditText) findViewById(R.id.editText2);
        sendBtn = (Button) findViewById(R.id.button);

        netResult.setMovementMethod(ScrollingMovementMethod.getInstance());

        startHuaweiApiClient();
    }

    public void onClicked(View view){
        sendBtn.setText(netUrl.getText().toString());
        get(netUrl.getText().toString());
    }

    public void updateDeviceToken(final String token){

        handler.post(new Runnable() {
            @Override
            public void run() {
                devToken.setText(token);
            }
        });
    }

    public void updateHttpResult(final String result){

        handler.post(new Runnable() {
            @Override
            public void run() {
                netResult.setText(result);
            }
        });
    }

    void startHuaweiApiClient() {
        client = new HuaweiApiClient.Builder(this)
                .addApi(HuaweiPush.PUSH_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        client.connect();
    }

    @Override
    public void onConnected() {
        getTokenAsyn();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("push", String.valueOf(connectionResult.getErrorCode()));
    }

    private void getTokenAsyn() {
        if(!client.isConnected()) {
            Log.i("push", "获取token失败，原因：HuaweiApiClient未连接");
            client.connect();
            return;
        }

        PendingResult<TokenResult> tokenResult = HuaweiPush.HuaweiPushApi.getToken(client);
        tokenResult.setResultCallback(new ResultCallback<TokenResult>() {
            @Override
            public void onResult(TokenResult result) {
            }
        });
    }

    void get(String url) {
        OkHttpClient.Builder builder = null;

        if (!isIgoneCer.isChecked()) {
            builder = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS);
        }
        else {
            builder = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .sslSocketFactory(overlockCard().getSocketFactory())
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String s, SSLSession sslSession) {
                            return true;
                        }
                    });

        }

        OkHttpClient client = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Gson gson = new Gson();
                updateHttpResult("Failure  " +  gson.toJson(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();
                updateHttpResult(response.code() + "   " + gson.toJson(response));
            }
        });
    }

    //信任所有证书
    private SSLContext overlockCard() {
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        }};
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts,
                    new java.security.SecureRandom());
            return sslContext;
        } catch (Exception e) {
            Log.d("ok", "ssl出现异常");
        }

        return null;
    }
}
