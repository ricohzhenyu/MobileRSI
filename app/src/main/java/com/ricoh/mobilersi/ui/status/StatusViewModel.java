package com.ricoh.mobilersi.ui.status;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StatusViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public StatusViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Todo: RSI Status");
    }

    public LiveData<String> getText() {
        return mText;
    }
}