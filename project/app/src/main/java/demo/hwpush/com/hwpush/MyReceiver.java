package demo.hwpush.com.hwpush;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.huawei.hms.support.api.push.PushEventReceiver;
import com.huawei.hms.support.api.push.PushReceiver;

/*
 * 接收Push所有消息的广播接收器
 */
public class MyReceiver extends PushReceiver {

    public MyReceiver() {
        super();
    }

    @Override
    public void onToken(Context context, String s, Bundle bundle) {
        super.onToken(context, s, bundle);

        Log.d("token", s);

        MainActivity.mainActivity.updateDeviceToken(s);
    }

    @Override
    public void onToken(Context context, String s) {
        super.onToken(context, s);

        Log.d("token", s);
    }
}
