package org.c_base.yeahletsdothat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PayActivity extends Activity {

    private static final int REQUEST_CODE = 100;

    @InjectView(R.id.webView)
    WebView webView;

    @InjectView(R.id.idText)
    TextView idText;

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

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("https://beta.yeahletsdothat.com/")
                .build();

        yourUsersApi = restAdapter.create(YeAPI.class);

        setContentView(R.layout.activity_pay);


        ButterKnife.inject(this);

        webView.getSettings().setJavaScriptEnabled(true);

        if (getIntent() != null && getIntent().getData() != null) {
            final String where = getIntent().getData().toString();

            if (where.endsWith("/")) {
                campaignId = where.substring(where.indexOf("/yeah/") + 6, where.lastIndexOf("/"));
            } else {
                campaignId = where.substring(where.indexOf("/yeah/") + 6);
            }
            idText.setText(campaignId);

            final LoadToast loadToast = new LoadToast(PayActivity.this).setText("Lade Kampagne").show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Campaign camp = yourUsersApi.fetchCampaign(campaignId);


                    for (final PaymentModule payment_method : camp.payment_methods) {
                        if (payment_method.module_name.equals("yldt_braintree")) {
                            transactionName = payment_method.name;
                        }
                    }

                    paymentInfo = yourUsersApi.fetchPaymentInfo(campaignId, transactionName);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadToast.success();
                            perkSpinner.setAdapter(new PerkAdapter(PayActivity.this, camp.perks));
                            idText.setVisibility(View.VISIBLE);
                            perkSpinner.setVisibility(View.VISIBLE);
                            payButton.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }).start();


            webView.loadUrl(where + "?embedded=1");
        } else {
            idText.setText("NO ID");
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == BraintreePaymentActivity.RESULT_OK) {
                final LoadToast loadToast = new LoadToast(PayActivity.this).setText("sending").show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        final Transaction transaction = new Transaction();

                        transaction.payment_nonce = data.getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
                        transaction.amount = paymentAmount.getText().toString();
                        transaction.name = transactionName;

                        yourUsersApi.payWithBraintree(transaction, campaignId, new Callback<TransactionResult>() {
                                                          @Override
                                                          public void success(final TransactionResult transactionResult, final Response response) {
                                                              runOnUiThread(new Runnable() {
                                                                  @Override
                                                                  public void run() {
                                                                      loadToast.success();
                                                                  }
                                                              });
                                                          }

                                                          @Override
                                                          public void failure(final RetrofitError error) {
                                                              runOnUiThread(new Runnable() {
                                                                  @Override
                                                                  public void run() {
                                                                      loadToast.error();
                                                                  }
                                                              });
                                                          }
                                                      }

                        );

                    }
                }).start();
            }
        }

    }
}
