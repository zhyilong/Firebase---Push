package demo.gcm.com.gcmdemo;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    public Handler handler = new Handler();
    public static  MainActivity mainActivity;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;

        textView = (TextView) findViewById(R.id.textView);

        StringBuilder builder = new StringBuilder();
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                builder.append("键：" + key + "-值：" + value + "\n");
            }
            textView.setText(builder.toString());
        }
    }

    public void onClicked(View view) {

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("GCM", token);
    }

    public void showMessage(final  String text) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
    }
}
