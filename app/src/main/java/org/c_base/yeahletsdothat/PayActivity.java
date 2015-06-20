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
import retrofit.RestAdapter;

public class PayActivity extends Activity {

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

    @OnClick(R.id.payButton)
    void pay() {
        Intent intent = new Intent(this, BraintreePaymentActivity.class);
        intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, paymentInfo.client_token);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://beta.yeahletsdothat.com/").build();

        yourUsersApi = restAdapter.create(YeAPI.class);

        setContentView(R.layout.activity_pay);

        ButterKnife.inject(this);

        if (getIntent() != null && getIntent().getData() != null) {
            final String where = getIntent().getData().toString();

            final String id = where.substring(where.indexOf("/yeah/") + 6, where.lastIndexOf("/"));
            idText.setText(id);

            final LoadToast loadToast = new LoadToast(PayActivity.this).setText("Lade Kampagne").show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Campaign camp = yourUsersApi.fetchCampaign(id);

                    String name = null;
                    for (final PaymentModule payment_method : camp.payment_methods) {
                        if (payment_method.module_name.equals("yldt_braintree")) {
                            name = payment_method.name;
                        }
                    }

                    paymentInfo = yourUsersApi.fetchPaymentInfo(id, name);

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

}
