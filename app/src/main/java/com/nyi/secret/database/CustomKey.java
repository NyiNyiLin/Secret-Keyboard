package com.nyi.secret.database;

/**
 * Created by IN-3442 on 12-Oct-15.
 */
public class CustomKey {
    private  int KEY_CODE;
    private int KEY_CUSTOM_CODE;
    private String KEY_NAME;
    private String KEY_WORD;
    private  String KEY_ENCRYPT;
    private int KEY_PASSWORD;

    public void setKEY_ENCRYPT(String KEY_ENCRYPT) {
        this.KEY_ENCRYPT = KEY_ENCRYPT;
    }

    public CustomKey() {
    }

    public CustomKey(int KEY_CODE,int KEY_CUSTOM_CODE, String KEY_NAME, String KEY_WORD, String KEY_ENCRYPT, int KEY_PASSWORD) {
        this.KEY_CUSTOM_CODE = KEY_CUSTOM_CODE;
        this.KEY_CODE = KEY_CODE;
        this.KEY_NAME = KEY_NAME;
        this.KEY_WORD = KEY_WORD;
        this.KEY_ENCRYPT=KEY_ENCRYPT;
        this.KEY_PASSWORD = KEY_PASSWORD;
    }

    public CustomKey(String KEY_NAME, String KEY_WORD) {
        this.KEY_NAME = KEY_NAME;
        this.KEY_WORD = KEY_WORD;
    }

    public int getKEY_CODE() {
        return KEY_CODE;
    }

    public int getKEY_CUSTOM_CODE() {
        return KEY_CUSTOM_CODE;
    }

    public String getKEY_WORD() {
        return KEY_WORD;
    }

    public String getKEY_NAME() {
        return KEY_NAME;
    }

    public int getKEY_PASSWORD() {
        return KEY_PASSWORD;
    }

    public void setKEY_CODE(int KEY_CODE) {
        this.KEY_CODE = KEY_CODE;
    }

    public void setKEY_CUSTOM_CODE(int KEY_CUSTOM_CODE) {
        this.KEY_CUSTOM_CODE = KEY_CUSTOM_CODE;
    }

    public void setKEY_NAME(String KEY_NAME) {
        this.KEY_NAME = KEY_NAME;
    }

    public void setKEY_WORD(String KEY_WORD) {
        this.KEY_WORD = KEY_WORD;
    }

    public void setKEY_PASSWORD(int KEY_PASSWORD) {
        this.KEY_PASSWORD = KEY_PASSWORD;
    }

    public String getKEY_ENCRYPT() {
        return KEY_ENCRYPT;
    }
}
