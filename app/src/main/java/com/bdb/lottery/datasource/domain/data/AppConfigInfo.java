package com.bdb.lottery.datasource.domain.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by XB on 12/31/20
 */
public class AppConfigInfo implements Parcelable {

    private String domains;

    private VersionInfo versionInfo;

    public AppConfigInfo() {

    }

    protected AppConfigInfo(Parcel in) {
        domains = in.readString();
        versionInfo = in.readParcelable(AppConfigInfo.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(domains);
        dest.writeParcelable(versionInfo, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AppConfigInfo> CREATOR = new Creator<AppConfigInfo>() {
        @Override
        public AppConfigInfo createFromParcel(Parcel in) {
            return new AppConfigInfo(in);
        }

        @Override
        public AppConfigInfo[] newArray(int size) {
            return new AppConfigInfo[size];
        }
    };

    public void setDomains(String domains){
        this.domains = domains;
    }

    public String getDomains(){
        return this.domains;
    }

    public void setVersionInfo(VersionInfo versionInfo){
        this.versionInfo = versionInfo;
    }

    public VersionInfo getVersionInfo(){
        return this.versionInfo;
    }

    public static class VersionInfo implements Parcelable {

        private String androidUpgradeContent;

        private boolean androidUpgradeForce;

        private int androidVersionCode;

        private String androidVersionName;

        private String apkUrl;

        protected VersionInfo(Parcel in) {
            androidUpgradeContent = in.readString();
            androidUpgradeForce = in.readByte() != 0;
            androidVersionCode = in.readInt();
            androidVersionName = in.readString();
            apkUrl = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(androidUpgradeContent);
            dest.writeByte((byte) (androidUpgradeForce ? 1 : 0));
            dest.writeInt(androidVersionCode);
            dest.writeString(androidVersionName);
            dest.writeString(apkUrl);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<VersionInfo> CREATOR = new Creator<VersionInfo>() {
            @Override
            public VersionInfo createFromParcel(Parcel in) {
                return new VersionInfo(in);
            }

            @Override
            public VersionInfo[] newArray(int size) {
                return new VersionInfo[size];
            }
        };

        public String getAndroidUpgradeContent() {
            return androidUpgradeContent;
        }

        public void setAndroidUpgradeContent(String androidUpgradeContent) {
            this.androidUpgradeContent = androidUpgradeContent;
        }

        public boolean isAndroidUpgradeForce() {
            return androidUpgradeForce;
        }

        public void setAndroidUpgradeForce(boolean androidUpgradeForce) {
            this.androidUpgradeForce = androidUpgradeForce;
        }

        public int getAndroidVersionCode() {
            return androidVersionCode;
        }

        public void setAndroidVersionCode(int androidVersionCode) {
            this.androidVersionCode = androidVersionCode;
        }

        public String getAndroidVersionName() {
            return androidVersionName;
        }

        public void setAndroidVersionName(String androidVersionName) {
            this.androidVersionName = androidVersionName;
        }

        public String getApkUrl() {
            return apkUrl;
        }

        public void setApkUrl(String apkUrl) {
            this.apkUrl = apkUrl;
        }

    }

}
