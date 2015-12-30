package cc.cubone.turbo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

/**
 * The package listener.
 *
 * @see <a href="https://android.googlesource.com/platform/packages/apps/Launcher3/+/master">Launcher3</a>
 */
public class PackageListener {

    private Context mContext;
    private PackageCallback mCallback;
    private PackageReceiver mReceiver;

    public PackageListener(Context context) {
        mContext = context;
    }

    public void register(PackageCallback callback) {
        mCallback = callback;

        if (mReceiver != null) return;
        synchronized (this) {
            if (mReceiver != null) return;
            mReceiver = new PackageReceiver();
            IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
            filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
            filter.addDataScheme("package");
            mContext.registerReceiver(mReceiver, filter);
            filter = new IntentFilter();
            filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
            filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
            mContext.registerReceiver(mReceiver, filter);
        }
    }

    public void unregister() {
        if (mReceiver == null) return;
        synchronized (this) {
            if (mReceiver == null) return;
            mContext.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    private class PackageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mCallback == null) return;

            String action = intent.getAction();
            if (action == null) return;

            if (Intent.ACTION_PACKAGE_CHANGED.equals(action)
                    || Intent.ACTION_PACKAGE_REMOVED.equals(action)
                    || Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                final String packageName = intent.getData().getSchemeSpecificPart();
                final boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);

                if (packageName == null || packageName.length() == 0) {
                    // they sent us a bad intent
                    return;
                }
                if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
                    mCallback.onPackageChanged(packageName);
                } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                    if (!replacing) {
                        mCallback.onPackageRemoved(packageName);
                    }
                    // else, we are replacing the package, so a PACKAGE_ADDED will be sent
                    // later, we will update the package at this time
                } else if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                    if (!replacing) {
                        mCallback.onPackageAdded(packageName);
                    } else {
                        mCallback.onPackageChanged(packageName);
                    }
                }
            } else if (Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE.equals(action)) {
                // EXTRA_REPLACING is available Kitkat onwards. For lower devices, it is broadcasted
                // when moving a package or mounting/un-mounting external storage. Assume that
                // it is a replacing operation.
                final boolean ATLEAST_KITKAT = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
                final boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, ATLEAST_KITKAT);
                String[] packages = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
                mCallback.onPackagesAvailable(packages, replacing);
            } else if (Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.equals(action)) {
                // This intent is broadcasted when moving a package or mounting/un-mounting
                // external storage.
                // However on Kitkat this is also sent when a package is being updated, and
                // contains an extra Intent.EXTRA_REPLACING=true for that case.
                // Using false as default for Intent.EXTRA_REPLACING gives correct value on
                // lower devices as the intent is not sent when the app is updating/replacing.
                final boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
                String[] packages = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
                mCallback.onPackagesUnavailable(packages, replacing);
            }
        }
    }

}
