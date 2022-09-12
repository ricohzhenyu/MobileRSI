package com.ricoh.mobilersi.ui.help;

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
import com.ricoh.mobilersi.databinding.FragmentHelpBinding;

public class HelpFragment extends Fragment {

    final String urlHelp = "https://help-us.na.smart-integration.ricoh.com";
    private FragmentHelpBinding binding;
    private WebView helpWebView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HelpViewModel helpViewModel =
                new ViewModelProvider(this).get(HelpViewModel.class);

        binding = FragmentHelpBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textHelp;
        //helpViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle saveInstanceState) {
        super.onViewCreated(view, saveInstanceState);

        helpWebView = view.findViewById(R.id.helpWebView);

        //If webpage contains Javascript: Enable it
        helpWebView.getSettings().setJavaScriptEnabled(true);
        helpWebView.getSettings().setLoadWithOverviewMode(true); //adjust to screen automatically
        helpWebView.getSettings().setDomStorageEnabled(true);
        helpWebView.getSettings().setSaveFormData(true);
        helpWebView.getSettings().setDefaultTextEncodingName("UTF-8");
        helpWebView.getSettings().setAllowContentAccess(true);
        helpWebView.getSettings().setAllowFileAccess(true);


        helpWebView.setWebViewClient(new WebViewClient(){
            //open url using WebView (instead of jumping out to Android System-browser)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        helpWebView.loadUrl(urlHelp);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptThirdPartyCookies(helpWebView,true);
    }

/*
    @Override
    public void onHiddenChanged(boolean hidden) {
        if(hidden==false) {
            getActivity().setTitle("Help");
        }
    }
*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}