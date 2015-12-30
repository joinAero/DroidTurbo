package cc.cubone.turbo.receiver;

public interface PackageCallback {

    void onPackageAdded(String packageName);
    void onPackageChanged(String packageName);
    void onPackageRemoved(String packageName);

    void onPackagesAvailable(String[] packages, boolean replacing);
    void onPackagesUnavailable(String[] packages, boolean replacing);

}
