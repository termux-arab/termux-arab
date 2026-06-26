package com.termux.arab.model;

import android.os.Parcelable;
import android.os.Parcel;

public class Tool implements Parcelable {
    public String id;
    public String name;
    public String nameEn;
    public String description;
    public String packageName;
    public String command;
    public String icon;
    public String category;
    public int riskLevel; // 0=safe, 1=medium, 2=dangerous
    public boolean requiresRoot;
    public boolean needsArgs;
    public String example;

    public Tool() {}

    protected Tool(Parcel in) {
        id = in.readString();
        name = in.readString();
        nameEn = in.readString();
        description = in.readString();
        packageName = in.readString();
        command = in.readString();
        icon = in.readString();
        category = in.readString();
        riskLevel = in.readInt();
        requiresRoot = in.readByte() != 0;
        needsArgs = in.readByte() != 0;
        example = in.readString();
    }

    public static final Creator<Tool> CREATOR = new Creator<Tool>() {
        @Override public Tool createFromParcel(Parcel in) { return new Tool(in); }
        @Override public Tool[] newArray(int size) { return new Tool[size]; }
    };

    @Override public int describeContents() { return 0; }
    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id); dest.writeString(name); dest.writeString(nameEn);
        dest.writeString(description); dest.writeString(packageName);
        dest.writeString(command); dest.writeString(icon); dest.writeString(category);
        dest.writeInt(riskLevel); dest.writeByte((byte)(requiresRoot?1:0));
        dest.writeByte((byte)(needsArgs?1:0)); dest.writeString(example);
    }
}
