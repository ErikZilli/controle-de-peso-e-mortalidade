package br.com.controlealojamento.app;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import java.io.File;
import java.io.FileNotFoundException;

// Entrega os arquivos de compartilhamento (cache/share) para outros apps, só leitura e só com permissão concedida.
public class ShareProvider extends ContentProvider {
    static final String AUTHORITY = "br.com.controlealojamento.app.share";

    static File dir(Context c) { File d = new File(c.getCacheDir(), "share"); d.mkdirs(); return d; }

    private File fileFor(Uri uri) throws FileNotFoundException {
        String name = uri.getLastPathSegment();
        if (name == null || name.contains("/") || name.contains("..")) throw new FileNotFoundException();
        File f = new File(dir(getContext()), name);
        if (!f.exists()) throw new FileNotFoundException();
        return f;
    }

    @Override public boolean onCreate() { return true; }
    @Override public String getType(Uri uri) { String n = uri.getLastPathSegment(); return n != null && n.endsWith(".pdf") ? "application/pdf" : "application/json"; }
    @Override public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException { return ParcelFileDescriptor.open(fileFor(uri), ParcelFileDescriptor.MODE_READ_ONLY); }

    @Override public Cursor query(Uri uri, String[] projection, String selection, String[] args, String sort) {
        File f;
        try { f = fileFor(uri); } catch (FileNotFoundException e) { return null; }
        String[] cols = projection != null ? projection : new String[]{OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE};
        java.util.List<String> keep = new java.util.ArrayList<>(); java.util.List<Object> values = new java.util.ArrayList<>();
        for (String c : cols) {
            if (OpenableColumns.DISPLAY_NAME.equals(c)) { keep.add(c); values.add(f.getName()); }
            else if (OpenableColumns.SIZE.equals(c)) { keep.add(c); values.add(f.length()); }
        }
        MatrixCursor cursor = new MatrixCursor(keep.toArray(new String[0]), 1);
        cursor.addRow(values.toArray());
        return cursor;
    }

    @Override public Uri insert(Uri uri, ContentValues values) { throw new UnsupportedOperationException(); }
    @Override public int delete(Uri uri, String selection, String[] args) { return 0; }
    @Override public int update(Uri uri, ContentValues values, String selection, String[] args) { return 0; }
}
