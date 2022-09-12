package com.ricoh.mobilersi.ui.rsi;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ricoh.mobilersi.R;
import com.ricoh.mobilersi.databinding.FragmentRsiBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Locale;

public class RsiFragment extends Fragment {

    private FragmentRsiBinding binding;
    private WebView rsiWebView;


    private ValueCallback<Uri> mUploadCallbackBelow;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;

    private final static int FILE_CHOOSER_RESULT_CODE = 1000;
    private Uri imageUri = null;

    final String urlIndex = "https://www.na.smart-integration.ricoh.com/si-apps/pub/mobile-index.html";
    final String urlLogin = "https://www.na.smart-integration.ricoh.com/si-apps/pub/mobile-login.html";


    //*********************** Autofill related: Start ******************
    class JavaScriptInterface {
        @JavascriptInterface
        //Store form data when form-submit: called by Javascript of inject.js
        public void saveTenantFormData(String formData) {
            if(formData!=null && formData.length()>0) {
                //System.out.println("form data= " + formData);
                String[] sArray = formData.split(",");
                SharedPreferences sp = getActivity().getSharedPreferences("tenantFormDataSP", getContext().MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString("tenantId", sArray[0]);
                edit.putString("userId", sArray[1]);
                edit.putString("password", sArray[2]);
                edit.commit();
            }
        }
    }

    //read file to get Javascript source code
    private String buildJavascriptForTenantForm() throws IOException {
        StringBuilder buf = new StringBuilder();
        InputStream inject =  getResources().getAssets().open("inject.js");
        BufferedReader in = new BufferedReader(new InputStreamReader(inject, "UTF-8"));
        String str;
        while ((str = in.readLine()) != null) {
            buf.append(str);
        }
        in.close();

        return buf.toString();
    }

    //fill in form values
    private void fillFormData(WebView view) {
        //retrieve saved data
        SharedPreferences sp = getActivity().getSharedPreferences("tenantFormDataSP", getContext().MODE_PRIVATE);
        String tenantId = sp.getString("tenantId", "");
        String userId = sp.getString("userId", "");
        String password = sp.getString("password", "");
        System.out.println("tenantId=" + tenantId + ", userId=" + userId+ ", password=" + password);

        view.evaluateJavascript("javascript:document.querySelector('input[data-bind*=koTenantId]').value='2172914435';", null);
        view.evaluateJavascript("javascript:document.querySelector('input[data-bind*=koTenantId]').dispatchEvent(new KeyboardEvent('keydown'));", null);
        view.evaluateJavascript("javascript:document.querySelector('input[data-bind*=koUserId]').value='admin';", null);
        view.evaluateJavascript("javascript:document.querySelector('input[data-bind*=koUserId]').dispatchEvent(new KeyboardEvent('keydown'));", null);
        view.evaluateJavascript("javascript:document.querySelectorAll('input[data-bind*=koPassword]')[1].value='sdca0123';", null);
        view.evaluateJavascript("javascript:document.querySelectorAll('input[data-bind*=koPassword]')[1].dispatchEvent(new KeyboardEvent('keydown'));", null);

        try {

            view.loadUrl("javascript:" + buildJavascriptForTenantForm());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //*********************** Autofill related: End ******************



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RsiViewModel rsiViewModel =
                new ViewModelProvider(this).get(RsiViewModel.class);

        binding = FragmentRsiBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textRsi;
        //rsiViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }


    @Override
    public void onViewCreated(View view, Bundle saveInstanceState) {
        super.onViewCreated(view, saveInstanceState);
        rsiWebView = view.findViewById(R.id.rsiWebView);

        //If webpage contains Javascript: Enable it
        rsiWebView.getSettings().setJavaScriptEnabled(true);
        rsiWebView.getSettings().setLoadWithOverviewMode(true); //adjust to screen automatically
        rsiWebView.getSettings().setDomStorageEnabled(true);
        rsiWebView.getSettings().setSaveFormData(true);
        rsiWebView.getSettings().setDefaultTextEncodingName("UTF-8");
        rsiWebView.getSettings().setAllowContentAccess(true);
        rsiWebView.getSettings().setAllowFileAccess(true);

        rsiWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        rsiWebView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);

        rsiWebView.setWebContentsDebuggingEnabled(true);

        //"AndroidInterface" will be called by Javascript of inject.js
        rsiWebView.addJavascriptInterface(new JavaScriptInterface(), "AndroidInterface");


        rsiWebView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //System.out.println("onPageFinished, URL= " + url);

                //Auto-fill form data only when current page is login
                if(url.startsWith(urlLogin)) {
                    fillFormData(view);
                }
            }

            //open url using WebView (instead of jumping out to Android System-browser)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        rsiWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                b.setTitle("");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }

            //intercept event when clicking <input type="file" /> in HTML5
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]>
                    filePathCallback, FileChooserParams fileChooserParams) {
                mUploadCallbackAboveL = filePathCallback;
                prepareFileChooserMenu();
                return true;
            }
        });

        if(Build.VERSION.SDK_INT>=23) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }

        rsiWebView.loadUrl(urlIndex);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptThirdPartyCookies(rsiWebView,true);
    }


    File myFile = null;

    private void prepareFileChooserMenu() {
        Intent FileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        FileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        FileIntent.setType("*/*");

        //Create Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String fileName = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.US)) + ".jpg";
        //set storage location for camera photo
        myFile = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + fileName);
        try {
            myFile.createNewFile();
            //System.out.println("myFile1=" + myFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Compatible with Android 10+
        if(Build.VERSION.SDK_INT>=29) {
            if(getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                imageUri = FileProvider.getUriForFile(getActivity(), getContext().getPackageName() + ".fileProvider", myFile);
/*
                ContentValues values = new ContentValues();
                values.put("_data", myFile.getAbsolutePath());
                values.put("mime_type", "image/jpeg");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM");
                Uri localUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                imageUri = getContentResolver().insert(localUri, values);
*/
                //System.out.println("myFile2=" + imageUri.getPath());
            }
        }else {
            imageUri = Uri.fromFile(myFile);
        }
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        //Create Album(Gallery) intent
        Intent photoIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_TITLE, "Choose an action");
        chooser.putExtra(Intent.EXTRA_INTENT, photoIntent);
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{cameraIntent, FileIntent});

        startActivityForResult(chooser, FILE_CHOOSER_RESULT_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            //calling API
            if (mUploadCallbackBelow != null) {
                chooseBelowAPI(resultCode, data);
            } else if (mUploadCallbackAboveL != null) {
                chooseAboveAPI(resultCode, data);
            }
        }
    }

    /**
     *Callback function for Android API >= 21(Android 5.0)
     *@param resultCode: select the return code of the file or photo
     *@param data: select the return result of file or photo
     */
    private void chooseAboveAPI(int resultCode, Intent data) {
        if (getActivity().RESULT_OK == resultCode) {
            updatePhotos();
            //pick photo from Gallery(Album)
            if (data != null) {
                Uri[] results;
                Uri uriData = data.getData()==null?imageUri:data.getData();
                if (uriData != null) {
                    results = new Uri[]{uriData};
                    for (Uri uri : results) {
                        //System.out.println("###System return URI:" + uri.toString());
                    }
                    mUploadCallbackAboveL.onReceiveValue(results);
                } else {
                    mUploadCallbackAboveL.onReceiveValue(null);
                }
            } else {
                //capture image from Camera
                mUploadCallbackAboveL.onReceiveValue(new Uri[]{imageUri});
            }
        } else {
            mUploadCallbackAboveL.onReceiveValue(null);
        }
        mUploadCallbackAboveL = null;
    }


    /**
     *Callback function for Android API < 21(Android 5.0) - VERY OLD: but just in case
     *@param resultCode select the return code of the file or photo
     *@param data select the return result of file or photo
     */
    private void chooseBelowAPI(int resultCode, Intent data) {
        if (getActivity().RESULT_OK == resultCode) {
            updatePhotos();
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    mUploadCallbackBelow.onReceiveValue(uri);
                } else {
                    mUploadCallbackBelow.onReceiveValue(null);
                }
            } else {
                //Calling Camera
                mUploadCallbackBelow.onReceiveValue(imageUri);
            }
        } else {
            mUploadCallbackBelow.onReceiveValue(null);
        }
        mUploadCallbackBelow = null;
    }


    //Refresh image files in storage
    private void updatePhotos() {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(imageUri);
        getActivity().sendBroadcast(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}