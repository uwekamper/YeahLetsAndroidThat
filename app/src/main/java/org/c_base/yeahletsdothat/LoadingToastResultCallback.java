package org.c_base.yeahletsdothat;

import android.app.Activity;
import net.steamcrafted.loadtoast.LoadToast;
import org.c_base.yeahletsdothat.model.TransactionResult;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

class LoadingToastResultCallback implements Callback<TransactionResult> {

    private Activity payActivity;
    private final LoadToast loadToast;

    LoadingToastResultCallback(final Activity payActivity, final LoadToast loadToast) {
        this.payActivity = payActivity;
        this.loadToast = loadToast;
    }

    @Override
    public void success(final TransactionResult transactionResult, final Response response) {
        payActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                successOnUIThread();
            }
        });
    }

    private void successOnUIThread() {
        loadToast.success();
    }

    @Override
    public void failure(final RetrofitError error) {
        payActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                failOnUIThread();
            }
        });
    }

    private void failOnUIThread() {
        loadToast.error();
    }
}
