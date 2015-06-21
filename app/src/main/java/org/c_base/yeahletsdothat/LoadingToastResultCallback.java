package org.c_base.yeahletsdothat;

import net.steamcrafted.loadtoast.LoadToast;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoadingToastResultCallback<T>  implements Callback<T> {

    private final LoadToast loadToast;

    LoadingToastResultCallback(final LoadToast loadToast) {
        this.loadToast = loadToast;
    }

    @Override
    public void success(final T transactionResult, final Response response) {
        loadToast.success();
    }

    @Override
    public void failure(final RetrofitError error) {
        loadToast.error();
    }
}
