package com.narimene.temal.pedometer.pedometer.HR1755.models;

import android.os.Parcel;
import android.os.Parcelable;

public class BroadCastDataDebugBluetooth implements Parcelable {

    private String operation;
    private byte[] messageByteArray;
    private String messageLogs;


    public BroadCastDataDebugBluetooth(String operation, byte[] message, String messageLogs) {
        this.operation = operation;
        this.messageByteArray = message;
        this.messageLogs = messageLogs;
    }


    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public byte[] getMessageByteArray() {
        return messageByteArray;
    }

    public void setMessageByteArray(byte[] messageByteArray) {
        this.messageByteArray = messageByteArray;
    }

    public String getMessageLogs() {
        return messageLogs;
    }

    public void setMessageLogs(String messageLogs) {
        this.messageLogs = messageLogs;
    }

    protected BroadCastDataDebugBluetooth(Parcel in) {
        operation = in.readString();
        messageByteArray = in.createByteArray();
        messageLogs = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(operation);
        dest.writeByteArray(messageByteArray);
        dest.writeString(messageLogs);
    }

    @SuppressWarnings("unused")
    public static final Creator<BroadCastDataDebugBluetooth> CREATOR = new Creator<BroadCastDataDebugBluetooth>() {
        @Override
        public BroadCastDataDebugBluetooth createFromParcel(Parcel in) {
            return new BroadCastDataDebugBluetooth(in);
        }

        @Override
        public BroadCastDataDebugBluetooth[] newArray(int size) {
            return new BroadCastDataDebugBluetooth[size];
        }
    };
}