package org.c_base.yeahletsdothat;

import org.c_base.yeahletsdothat.model.Campaign;
import org.c_base.yeahletsdothat.model.PaymentInfo;
import org.c_base.yeahletsdothat.model.Transaction;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface YeAPI {
    @GET("/api/v1/campaigns/{id}")
    Campaign fetchCampaign(@Path("id") String campaignId);

    @GET("/api/v1/campaigns/{id}/pay_with/{name}")
    PaymentInfo fetchPaymentInfo(@Path("id") String campaignId, @Path("name") String paymentName);

    @POST("/api/v1/campaigns/{id}/transactions")
    Campaign payWithBraintree(@Body Transaction paymentPostData, @Path("id") String paymentName);
}
