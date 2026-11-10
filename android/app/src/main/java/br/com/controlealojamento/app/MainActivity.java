package br.com.controlealojamento.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;

// Casca do app: abre o sistema web (assets/index.html) numa WebView. Os dados ficam no armazenamento do próprio app.
public class MainActivity extends Activity {
    private WebView web;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        web = new WebView(this);
        setContentView(web);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(false);
        s.setTextZoom(100);
        web.setWebViewClient(new WebViewClient());
        web.addJavascriptInterface(new Bridge(), "AndroidBridge");
        web.loadUrl("file:///android_asset/index.html");
    }

    // Voltar do Android: primeiro volta de tela dentro do app; na primeira tela, fecha.
    @Override public void onBackPressed() {
        web.evaluateJavascript("window.__back ? window.__back() : false", new ValueCallback<String>() {
            @Override public void onReceiveValue(String v) { if (!"true".equals(v)) finish(); }
        });
    }

    private class Bridge {
        // Recebe o PDF em base64, grava no cache e abre a janela de compartilhar (WhatsApp etc.).
        @JavascriptInterface public void sharePdf(final String base64, final String fileName, final String message) {
            runOnUiThread(new Runnable() { @Override public void run() {
                try {
                    File[] old = ShareProvider.dir(MainActivity.this).listFiles();
                    if (old != null) for (File f : old) f.delete();
                    String safe = fileName.replaceAll("[\\/:*?\"<>|]", "-").trim();
                    FileOutputStream out = new FileOutputStream(new File(ShareProvider.dir(MainActivity.this), safe));
                    out.write(Base64.decode(base64, Base64.DEFAULT)); out.close();
                    Uri uri = new Uri.Builder().scheme("content").authority(ShareProvider.AUTHORITY).appendPath(safe).build();
                    Intent send = new Intent(Intent.ACTION_SEND);
                    send.setType("application/pdf");
                    send.putExtra(Intent.EXTRA_STREAM, uri);
                    send.putExtra(Intent.EXTRA_TEXT, message);
                    send.putExtra(Intent.EXTRA_SUBJECT, safe);
                    send.setClipData(ClipData.newRawUri(safe, uri));
                    send.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(send, "Compartilhar registro"));
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Não foi possível compartilhar o PDF", Toast.LENGTH_LONG).show();
                }
            } });
        }
    }
}
