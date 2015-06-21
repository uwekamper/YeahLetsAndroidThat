package org.c_base.yeahletsdothat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import java.util.List;
import net.steamcrafted.loadtoast.LoadToast;
import org.c_base.yeahletsdothat.model.Campaign;
import retrofit.RestAdapter;
import retrofit.client.Response;

public class CampaignListActivity extends Activity {

    public static final String ENDPOINT = "https://beta.yeahletsdothat.com";
    @InjectView(R.id.content_list)
    RecyclerView recyclerView;

    private YeAPI yourUsersApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        ButterKnife.inject(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(ENDPOINT).build();

        yourUsersApi = restAdapter.create(YeAPI.class);

        final LoadToast show = new LoadToast(this).setText("lade kampagnen").show();
        yourUsersApi.fetchCampaigns(new LoadingToastResultCallback<List<Campaign>>(show) {
            @Override
            public void success(final List<Campaign> campaigns, final Response response) {
                super.success(campaigns, response);
                recyclerView.setAdapter(new CampaignAdapter(campaigns));
            }

        });
    }

    class CampaignAdapter extends RecyclerView.Adapter<CampaignViewHolder> {

        final List<Campaign> campaigns;

        CampaignAdapter(final List<Campaign> campaigns) {
            this.campaigns = campaigns;
        }

        @Override
        public CampaignViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
            final LayoutInflater from = LayoutInflater.from(viewGroup.getContext());
            final View inflate = from.inflate(R.layout.item_campaign, viewGroup, false);
            return new CampaignViewHolder(inflate);
        }

        @Override
        public void onBindViewHolder(final CampaignViewHolder viewHolder, final int i) {
            viewHolder.bind(campaigns.get(i));
        }

        @Override
        public int getItemCount() {
            return campaigns.size();
        }
    }

    class CampaignViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.title)
        TextView title;

        @InjectView(R.id.userName)
        TextView userName;

        private Campaign campaign;

        @OnClick(R.id.container)
        void onClick() {
            final Intent intent = new Intent(CampaignListActivity.this, PayActivity.class);
            intent.setData(Uri.parse(ENDPOINT + "/yeah/" + campaign.key+"/"));
            startActivity(intent);
        }

        public CampaignViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        public void bind(Campaign campaign) {
            this.campaign=campaign;
            title.setText(campaign.title);
            userName.setText(campaign.username);
        }

    }
}
