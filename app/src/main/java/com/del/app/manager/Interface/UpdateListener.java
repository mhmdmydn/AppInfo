package com.del.app.manager.Interface;

public interface UpdateListener {
        void onJsonDataReceived(int newVersionCode, String changeLog, String URLdown);
        void onError(String error);
}
