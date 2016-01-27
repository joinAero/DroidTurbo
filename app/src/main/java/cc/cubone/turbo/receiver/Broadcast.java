package cc.cubone.turbo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class Broadcast {

    public static void send(Context context, String action, Bundle extras) {
        Intent intent = new Intent(action);
        if (extras != null) {
            intent.putExtras(extras);
        }
        send(context, intent);
    }

    public static void send(Context context, Intent intent) {
        if (context == null) return;
        context.sendBroadcast(intent);
    }

    public static abstract class Receiver<Callback> {

        private Context mContext;
        private Callback mCallback;
        private BroadcastReceiver mReceiver;

        public Receiver(Context context) {
            mContext = context;
        }

        public boolean isRegistered() {
            return mReceiver != null;
        }

        public void register(Callback callback) {
            mCallback = callback;

            if (isRegistered()) return;
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    onIntentReceived(intent, mCallback);
                }
            };

            register(onCreateIntentFilter());

            IntentFilter[] filters = onCreateIntentFilters();
            if (filters != null) {
                for (IntentFilter filter : filters) {
                    register(filter);
                }
            }
        }

        private void register(IntentFilter filter) {
            if (filter != null) mContext.registerReceiver(mReceiver, filter);
        }

        public void unregister() {
            if (!isRegistered()) return;
            mContext.unregisterReceiver(mReceiver);
            mReceiver = null;
        }

        public IntentFilter onCreateIntentFilter() {
            return null;
        }

        public IntentFilter[] onCreateIntentFilters() {
            return null;
        }

        public abstract void onIntentReceived(Intent intent, Callback callback);

    }

}
