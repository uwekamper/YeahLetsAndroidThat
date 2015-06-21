package org.c_base.yeahletsdothat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import net.steamcrafted.loadtoast.LoadToast;
import org.c_base.yeahletsdothat.model.Campaign;
import org.c_base.yeahletsdothat.model.PaymentInfo;
import org.c_base.yeahletsdothat.model.PaymentModule;
import org.c_base.yeahletsdothat.model.Transaction;
import org.c_base.yeahletsdothat.model.TransactionResult;
import retrofit.RestAdapter;
import retrofit.client.Response;

public class PayActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 100;

    @InjectView(R.id.webView)
    WebView webView;

    @InjectView(R.id.perkSpinner)
    Spinner perkSpinner;

    @InjectView(R.id.paymentAmount)
    EditText paymentAmount;

    private YeAPI yourUsersApi;

    @InjectView(R.id.payButton)
    Button payButton;
    private PaymentInfo paymentInfo;
    private String transactionName;
    private String campaignId;

    @OnClick(R.id.payButton)
    void pay() {
        Intent intent = new Intent(this, BraintreePaymentActivity.class);
        intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, paymentInfo.client_token);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null && getIntent().getData() != null) {

            final Uri data = getIntent().getData();
            final String where = data.toString();

            final String endpoint = data.getScheme() + "://" + data.getHost();
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(endpoint).build();

            yourUsersApi = restAdapter.create(YeAPI.class);

            setContentView(R.layout.activity_pay);

            ButterKnife.inject(this);

            webView.getSettings().setJavaScriptEnabled(true);

            if (where.endsWith("/")) {
                campaignId = where.substring(where.indexOf("/yeah/") + 6, where.lastIndexOf("/"));
            } else {
                campaignId = where.substring(where.indexOf("/yeah/") + 6);
            }

            final LoadToast loadToast = new LoadToast(PayActivity.this).setText("Lade Kampagne").show();

            yourUsersApi.fetchCampaign(campaignId, new LoadingToastResultCallback<Campaign>(loadToast) {

                @Override
                public void success(final Campaign campaign, final Response response) {
                    final Campaign camp = campaign;
                    for (final PaymentModule payment_method : camp.payment_methods) {
                        if (payment_method.module_name.equals("yldt_braintree")) {
                            transactionName = payment_method.name;
                        }
                    }

                    yourUsersApi.fetchPaymentInfo(campaignId, transactionName, new LoadingToastResultCallback<PaymentInfo>(loadToast) {
                        @Override
                        public void success(final PaymentInfo paymentInfo, final Response response) {
                            super.success(paymentInfo, response);
                            PayActivity.this.paymentInfo = paymentInfo;
                            perkSpinner.setAdapter(new PerkAdapter(camp.perks));
                            perkSpinner.setVisibility(View.VISIBLE);
                            payButton.setVisibility(View.VISIBLE);
                        }
                    });

                }

            });

            webView.loadUrl(where + "?embedded=1");
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == BraintreePaymentActivity.RESULT_OK) {
                final LoadToast loadToast = new LoadToast(PayActivity.this).setText("sending").show();
                final Transaction transaction = new Transaction();

                transaction.payment_nonce = data.getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
                transaction.amount = paymentAmount.getText().toString();
                transaction.name = transactionName;

                yourUsersApi.payWithBraintree(transaction, campaignId, new LoadingToastResultCallback<TransactionResult>(loadToast) {
                    @Override
                    public void success(final TransactionResult transactionResult, final Response response) {
                        super.success(transactionResult, response);
                        webView.reload();
                    }
                });

            }
        }
    }

}