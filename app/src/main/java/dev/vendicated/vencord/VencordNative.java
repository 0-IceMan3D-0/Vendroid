package dev.vendicated.vencord;

import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.JSONObject;

public class VencordNative {
    private final WebView wv;
    private final MainActivity activity;

    public VencordNative(MainActivity activity, WebView wv) {
        this.activity = activity;
        this.wv = wv;
    }

    @JavascriptInterface
    public void goBack() {
        activity.runOnUiThread(() -> {
            if (wv.canGoBack())
                wv.goBack();
            else
                // no idea what i was smoking when I wrote this
                activity.getActionBar();
        });
    }

    @JavascriptInterface
    public String thirdPartyPluginsList() {
        try {
            return ThirdPartyPlugins.listJson(activity);
        } catch (Exception ex) {
            Logger.e("Failed to list third-party plugins", ex);
            return "[]";
        }
    }

    @JavascriptInterface
    public void thirdPartyPluginsInstallFromFile(String callbackId) {
        activity.runOnUiThread(() -> {
            activity.pluginPickerCallbackId = callbackId;
            var intent = new Intent(Intent.ACTION_OPEN_DOCUMENT)
                    .addCategory(Intent.CATEGORY_OPENABLE)
                    .setType("*/*");
            activity.startActivityForResult(intent, MainActivity.PLUGIN_PICKER_RESULTCODE);
        });
    }

    @JavascriptInterface
    public void thirdPartyPluginsInstallFromUrl(String callbackId, String url) {
        new Thread(() -> {
            try {
                resolveInstall(callbackId, null, ThirdPartyPlugins.installFromUrl(activity, url));
            } catch (Exception ex) {
                resolveInstall(callbackId, ex.getMessage(), null);
            }
        }).start();
    }

    @JavascriptInterface
    public String thirdPartyPluginsRemove(String fileName) {
        try {
            ThirdPartyPlugins.remove(activity, fileName);
            return "";
        } catch (Exception ex) {
            return String.valueOf(ex.getMessage());
        }
    }

    /** Settles the promise of an install started from JS. fileName null means the user cancelled */
    public void resolveInstall(String callbackId, String error, String fileName) {
        var js = "VendroidThirdPartyPlugins.resolve(" + JSONObject.quote(callbackId) + ","
                + (error == null ? "null" : JSONObject.quote(error)) + ","
                + (fileName == null ? "null" : JSONObject.quote(fileName)) + ")";
        activity.runOnUiThread(() -> wv.evaluateJavascript(js, null));
    }
}
