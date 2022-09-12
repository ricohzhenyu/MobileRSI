package com.ricoh.mobilersi.ui.status;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ricoh.mobilersi.R;
import com.ricoh.mobilersi.databinding.FragmentStatusBinding;

public class StatusFragment extends Fragment {

    final String urlStatus = "https://na.smart-integration.status.ricoh.com";
    private FragmentStatusBinding binding;
    private WebView statusWebView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StatusViewModel statusViewModel =
                new ViewModelProvider(this).get(StatusViewModel.class);

        binding = FragmentStatusBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textStatus;
        //statusViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle saveInstanceState) {
        super.onViewCreated(view, saveInstanceState);

        statusWebView = view.findViewById(R.id.statusWebView);

        //If webpage contains Javascript: Enable it
        statusWebView.getSettings().setJavaScriptEnabled(true);
        statusWebView.getSettings().setLoadWithOverviewMode(true); //adjust to screen automatically
        statusWebView.getSettings().setDomStorageEnabled(true);
        statusWebView.getSettings().setSaveFormData(true);
        statusWebView.getSettings().setDefaultTextEncodingName("UTF-8");
        statusWebView.getSettings().setAllowContentAccess(true);
        statusWebView.getSettings().setAllowFileAccess(true);


        statusWebView.setWebViewClient(new WebViewClient(){
            //open url using WebView (instead of jumping out to Android System-browser)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        statusWebView.loadUrl(urlStatus);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptThirdPartyCookies(statusWebView,true);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}