package cc.cubone.turbo.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

/**
 * The package broadcast.
 *
 * @see <a href="https://android.googlesource.com/platform/packages/apps/Launcher3/+/master">Launcher3</a>
 */
public class PackageBroadcast {

    public interface Callback {

        void onPackageAdded(String packageName);

        void onPackageChanged(String packageName);

        void onPackageRemoved(String packageName);

        void onPackagesAvailable(String[] packages, boolean replacing);

        void onPackagesUnavailable(String[] packages, boolean replacing);

    }

    public static class Receiver extends Broadcast.Receiver<Callback> {

        public Receiver(Context context) {
            super(context);
        }

        @Override
        public IntentFilter[] onCreateIntentFilters() {
            IntentFilter filter1 = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
            filter1.addAction(Intent.ACTION_PACKAGE_REMOVED);
            filter1.addAction(Intent.ACTION_PACKAGE_CHANGED);
            filter1.addDataScheme("package");
            IntentFilter filter2 = new IntentFilter();
            filter2.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
            filter2.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
            return new IntentFilter[]{filter1, filter2};
        }

        @Override
        public void onIntentReceived(Intent intent, Callback callback) {
            if (callback == null) return;

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
                    callback.onPackageChanged(packageName);
                } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                    if (!replacing) {
                        callback.onPackageRemoved(packageName);
                    }
                    // else, we are replacing the package, so a PACKAGE_ADDED will be sent
                    // later, we will update the package at this time
                } else if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                    if (!replacing) {
                        callback.onPackageAdded(packageName);
                    } else {
                        callback.onPackageChanged(packageName);
                    }
                }
            } else if (Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE.equals(action)) {
                // EXTRA_REPLACING is available Kitkat onwards. For lower devices, it is broadcasted
                // when moving a package or mounting/un-mounting external storage. Assume that
                // it is a replacing operation.
                final boolean ATLEAST_KITKAT = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
                final boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, ATLEAST_KITKAT);
                String[] packages = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
                callback.onPackagesAvailable(packages, replacing);
            } else if (Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.equals(action)) {
                // This intent is broadcasted when moving a package or mounting/un-mounting
                // external storage.
                // However on Kitkat this is also sent when a package is being updated, and
                // contains an extra Intent.EXTRA_REPLACING=true for that case.
                // Using false as default for Intent.EXTRA_REPLACING gives correct value on
                // lower devices as the intent is not sent when the app is updating/replacing.
                final boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
                String[] packages = intent.getStringArrayExtra(Intent.EXTRA_CHANGED_PACKAGE_LIST);
                callback.onPackagesUnavailable(packages, replacing);
            }
        }
    }
}
