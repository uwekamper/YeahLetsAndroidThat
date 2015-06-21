package org.c_base.yeahletsdothat;

import com.squareup.okhttp.Call;
import org.c_base.yeahletsdothat.model.Campaign;
import org.c_base.yeahletsdothat.model.PaymentInfo;
import org.c_base.yeahletsdothat.model.Transaction;
import org.c_base.yeahletsdothat.model.TransactionResult;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface YeAPI {
    @GET("/api/v1/campaigns/{id}")
    void fetchCampaign(@Path("id") String campaignId,Callback<Campaign> callback);

    @GET("/api/v1/campaigns/{id}/pay_with/{name}")
    void fetchPaymentInfo(@Path("id") String campaignId, @Path("name") String paymentName,Callback<PaymentInfo> callback);

    @POST("/api/v1/campaigns/{id}/transactions/")
    void payWithBraintree(@Body Transaction paymentPostData, @Path("id") String paymentName, Callback<TransactionResult> callback);
}
