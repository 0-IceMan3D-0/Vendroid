package dev.vendicated.vencord;

import android.content.Context;
import android.net.Uri;
import android.provider.OpenableColumns;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Third-party Vencord plugins, stored as .js files in the app's private storage.
 * The loader that registers them with Vencord is res/raw/third_party_plugins.js, built from the Vesktop fork.
 */
public class ThirdPartyPlugins {
    private static final Pattern PLUGIN_META = Pattern.compile("\\{\\s*\\[[\\w$]+\\.name\\]\\s*:\\s*\\{\\s*folderName\\s*:");

    private static File dir(Context ctx) {
        var dir = new File(ctx.getFilesDir(), "plugins");
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();
        return dir;
    }

    private static File pluginFile(Context ctx, String fileName) throws IOException {
        if (!fileName.endsWith(".js") || !new File(fileName).getName().equals(fileName))
            throw new IOException("Invalid plugin file name: " + fileName);
        return new File(dir(ctx), fileName);
    }

    private static String toFileName(String name) {
        name = name.replaceAll("[^\\w.-]", "_");
        if (name.isEmpty()) name = "plugin";
        return name.endsWith(".js") ? name : name + ".js";
    }

    /**
     * Vencord keeps the metadata of its plugins in an object it doesn't export, and its settings UI crashes
     * for plugins missing from it. Expose it so the loader can add third-party plugins to it.
     */
    public static String exposeVencordPluginMeta(String vencordScript) {
        var matcher = PLUGIN_META.matcher(vencordScript);
        if (!matcher.find()) {
            Logger.e("Couldn't find Vencord's PluginMeta, plugin settings of third-party plugins may not open");
            return vencordScript;
        }
        return vencordScript.substring(0, matcher.start()) + "globalThis.VencordPluginMeta=" + vencordScript.substring(matcher.start());
    }

    /** JSON array of { fileName, code } */
    public static String listJson(Context ctx) throws IOException, JSONException {
        var files = dir(ctx).listFiles((d, name) -> name.endsWith(".js"));
        var result = new JSONArray();
        if (files == null) return result.toString();

        Arrays.sort(files);
        for (var file : files) {
            try (var is = new FileInputStream(file)) {
                result.put(new JSONObject()
                        .put("fileName", file.getName())
                        .put("code", HttpClient.readAsText(is)));
            }
        }
        return result.toString();
    }

    public static String installFromUri(Context ctx, Uri uri) throws IOException {
        var name = "plugin.js";
        try (var cursor = ctx.getContentResolver().query(uri, new String[]{ OpenableColumns.DISPLAY_NAME }, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) name = cursor.getString(0);
        }

        var fileName = toFileName(name);
        try (var is = ctx.getContentResolver().openInputStream(uri)) {
            if (is == null) throw new IOException("Couldn't open " + uri);
            save(ctx, fileName, is);
        }
        return fileName;
    }

    public static String installFromUrl(Context ctx, String url) throws IOException {
        var parsed = new URL(url);
        if (!"https".equals(parsed.getProtocol())) throw new IOException("Only https:// links are allowed");

        var conn = (HttpURLConnection) parsed.openConnection();
        if (conn.getResponseCode() >= 300)
            throw new IOException("Download failed: " + conn.getResponseCode() + " " + conn.getResponseMessage());

        var fileName = toFileName(new File(Uri.parse(url).getPath()).getName());
        try (var is = conn.getInputStream()) {
            save(ctx, fileName, is);
        }
        return fileName;
    }

    private static void save(Context ctx, String fileName, InputStream is) throws IOException {
        var code = HttpClient.readAsText(is);
        try (var os = new FileOutputStream(pluginFile(ctx, fileName))) {
            os.write(code.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static void remove(Context ctx, String fileName) throws IOException {
        var file = pluginFile(ctx, fileName);
        if (file.exists() && !file.delete()) throw new IOException("Couldn't delete " + fileName);
    }
}
