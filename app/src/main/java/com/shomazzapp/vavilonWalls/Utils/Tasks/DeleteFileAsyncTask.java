package com.shomazzapp.vavilonWalls.Utils.Tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.widget.Toast;

import com.shomazzapp.vavilonWalls.Utils.Constants;

import java.io.File;

public class DeleteFileAsyncTask extends AsyncTask<File, Void, Void> {

    public interface AsyncResponse {
        void processFinish();
    }

    private AsyncResponse delegate;
    private final ProgressDialog progressDialog;
    private Context context;
    private Exception m_error = null;

    public DeleteFileAsyncTask(Context context, AsyncResponse delegate) {
        this.delegate = delegate;
        this.context = context;
        progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setMessage("Deleting wallpaper ...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancel(false);
            }
        });
        return;
    }

    @Override
    protected Void doInBackground(File... files) {
        try {
            files[0].delete();
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            m_error = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (m_error != null) {
            m_error.printStackTrace();
            Toast.makeText(context, Constants.ERROR_SETTING_WALL_MSG, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, Constants.SUCCES_MSG, Toast.LENGTH_SHORT).show();
            if (delegate != null)
                delegate.processFinish();
        }
        progressDialog.hide();
        return;
    }
}