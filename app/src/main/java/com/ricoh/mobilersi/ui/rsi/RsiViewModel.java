package com.ricoh.mobilersi.ui.rsi;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RsiViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public RsiViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Todo: RSI WebView");
    }

    public LiveData<String> getText() {
        return mText;
    }
}