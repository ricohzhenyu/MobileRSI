package com.ricoh.mobilersi.ui.status;

import android.webkit.WebView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StatusViewModel extends ViewModel {

    private final MutableLiveData<WebView> mWebView;

    public StatusViewModel() {
        mWebView = new MutableLiveData<>();
    }

    public LiveData<WebView> getWebView() {
        return mWebView;
    }
}