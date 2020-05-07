package uren.com.filmktphanem.Fragments.Person;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uren.com.filmktphanem.AsyncFunctions.TMDBQueryProcess;
import uren.com.filmktphanem.Fragments.BaseFragment;
import uren.com.filmktphanem.Fragments.Library.ShowSelectedPhotoFragment;
import uren.com.filmktphanem.Fragments.Movies.MovieDetailFragment;
import uren.com.filmktphanem.Interfaces.OnEventListener;
import uren.com.filmktphanem.R;
import uren.com.filmktphanem.Utils.CommonUtils;
import uren.com.filmktphanem.adapters.MovieRecyclerViewAdapter;
import uren.com.filmktphanem.data.NetworkUtils;
import uren.com.filmktphanem.models.Cast;
import uren.com.filmktphanem.models.Crew;
import uren.com.filmktphanem.models.Movie;
import uren.com.filmktphanem.models.Person;
import uren.com.filmktphanem.models.PersonExternalInfo;

import static uren.com.filmktphanem.Constants.StringConstants.TYPE_NOW_PLAYING;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_POPULAR;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_TOP_250;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_TOP_RATED;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_TRENDING;
import static uren.com.filmktphanem.Constants.StringConstants.TYPE_UPCOMING;

public class PersonFragment extends BaseFragment {

    View mView;

    @BindView(R.id.imgvProfile)
    ImageView imgvProfile;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.llBirthday)
    LinearLayout llBirthday;
    @BindView(R.id.tvBirthday)
    TextView tvBirthday;
    @BindView(R.id.llDeathday)
    LinearLayout llDeathday;
    @BindView(R.id.tvDeathday)
    TextView tvDeathday;
    @BindView(R.id.llBirthPlace)
    LinearLayout llBirthPlace;
    @BindView(R.id.tvBirthPlace)
    TextView tvBirthPlace;
    @BindView(R.id.llBiograhpy)
    LinearLayout llBiograhpy;
    @BindView(R.id.tvBiograhpy)
    TextView tvBiograhpy;
    @BindView(R.id.btnViewAllMovies)
    Button btnViewAllMovies;

    @BindView(R.id.llfacebook)
    LinearLayout llfacebook;
    @BindView(R.id.tvFacebookAcc)
    TextView tvFacebookAcc;

    @BindView(R.id.llinstagram)
    LinearLayout llinstagram;
    @BindView(R.id.tvInstagramAcc)
    TextView tvInstagramAcc;

    @BindView(R.id.lltwitter)
    LinearLayout lltwitter;
    @BindView(R.id.tvTwitterAcc)
    TextView tvTwitterAcc;

    private int personId;
    private Person person;

    public PersonFragment(int personId) {
        this.personId = personId;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_person_detail, container, false);
            ButterKnife.bind(this, mView);
            initVariables();
            initListeners();
        }
        return mView;
    }

    private void initListeners() {
        imgvProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (person != null && person.getProfileUrl() != null)
                    mFragmentNavigation.pushFragment(new ShowSelectedPhotoFragment(person.getProfileUrl()));
            }
        });

        llfacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(NetworkUtils.getFacebookUrl() + person.getExternalInfo().getFacebook_id()));
                startActivity(browserIntent);
            }
        });

        lltwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(NetworkUtils.getTwitterUrl() + person.getExternalInfo().getTwitter_id()));
                startActivity(browserIntent);
            }
        });

        llinstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(NetworkUtils.getInstagramUrl() + person.getExternalInfo().getInstagram_id()));
                startActivity(browserIntent);
            }
        });

        btnViewAllMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentNavigation.pushFragment(new PersonMoviesFragment(person.getId(), person.getName()));
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void initVariables() {
        URL TMDBPersonDetailURL = NetworkUtils.buildPersonDetailUrl(personId, NetworkUtils.LANG_EN);
        new TMDBQueryTask().execute(TMDBPersonDetailURL);
    }

    private void showErrorMessage() {
        //recyclerView.setVisibility(View.INVISIBLE);
        //tvErrorMessage.setVisibility(View.VISIBLE);
    }

    public class TMDBQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];

            String TMDBResults = null;
            try {
                TMDBResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return TMDBResults;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null && !s.equals("")) {
                try {
                    parsePersonData(s);
                    URL TMDBPersonExternalURL = NetworkUtils.buildPersonExternalInfoUrl(personId);
                    new PersonExternalTask().execute(TMDBPersonExternalURL);

                } catch (JSONException e) {
                    e.printStackTrace();
                    showErrorMessage();
                }
            } else {
                showErrorMessage();
            }
        }
    }

    public class PersonExternalTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];

            String TMDBResults = null;
            try {
                TMDBResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return TMDBResults;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null && !s.equals("")) {
                try {
                    parsePersonExternalData(s);
                    bindPersonData();

                } catch (JSONException e) {
                    e.printStackTrace();
                    showErrorMessage();
                }
            } else {
                showErrorMessage();
            }
        }
    }

    private void bindPersonData() {
        tvName.setText(person.getName());

        if(!CommonUtils.isEmptyCheck(person.getBirthday()))
            tvBirthday.setText(person.getBirthday());
        else
            llBirthday.setVisibility(View.GONE);

        if(!CommonUtils.isEmptyCheck(person.getDeathday()))
            tvDeathday.setText(person.getDeathday());
        else
            llDeathday.setVisibility(View.GONE);

        if(!CommonUtils.isEmptyCheck(person.getPlace_of_birth()))
            tvBirthPlace.setText(person.getPlace_of_birth());
        else
            llBirthPlace.setVisibility(View.GONE);

        if(!CommonUtils.isEmptyCheck(person.getBiography()))
            tvBiograhpy.setText(person.getBiography());
        else
            llBiograhpy.setVisibility(View.GONE);


        if(!CommonUtils.isEmptyCheck(person.getExternalInfo().getFacebook_id()))
            tvFacebookAcc.setText("/".concat(person.getExternalInfo().getFacebook_id()));
        else
            llfacebook.setVisibility(View.GONE);

        if(!CommonUtils.isEmptyCheck(person.getExternalInfo().getInstagram_id()))
            tvInstagramAcc.setText("/".concat(person.getExternalInfo().getInstagram_id()));
        else
            llinstagram.setVisibility(View.GONE);

        if(!CommonUtils.isEmptyCheck(person.getExternalInfo().getTwitter_id()))
            tvTwitterAcc.setText("/".concat(person.getExternalInfo().getTwitter_id()));
        else
            lltwitter.setVisibility(View.GONE);

        if (person.getProfilePath() == "null") {
            Picasso.get().load(R.drawable.profile_placeholder).into(imgvProfile);
        } else {
            Glide.with(getContext())
                    .load(person.getProfileUrl())
                    .apply(RequestOptions.centerCropTransform())
                    .into(imgvProfile);

           // Picasso.get().load(person.getProfileUrl()).into(imgvProfile);
        }
    }

    private void parsePersonData(String personJSONString) throws JSONException {
        // Get JSON values
        JSONObject movieJSONObject = new JSONObject(personJSONString);

        person = new Person();
        person.setBirthday(movieJSONObject.getString("birthday"));
        person.setKnown_for_department(movieJSONObject.getString("known_for_department"));
        person.setDeathday(movieJSONObject.getString("deathday"));
        person.setName(movieJSONObject.getString("name"));
        person.setId(movieJSONObject.getInt("id"));
        person.setGender(movieJSONObject.getInt("gender"));
        person.setPlace_of_birth(movieJSONObject.getString("place_of_birth"));
        person.setProfilePath(movieJSONObject.getString("profile_path"));
        person.setImdb_id(movieJSONObject.getString("imdb_id"));
        person.setHomepage(movieJSONObject.getString("homepage"));
        person.setBiography(movieJSONObject.getString("biography"));
        person.setProfileUrl(person.getProfilePath());

        if(person.getDeathday().equals("null"))
            person.setDeathday("");
    }

    private void parsePersonExternalData(String personJSONString) throws JSONException {
        JSONObject movieJSONObject = new JSONObject(personJSONString);

        PersonExternalInfo externalInfo = new PersonExternalInfo();

        externalInfo.setTwitter_id(movieJSONObject.getString("twitter_id"));
        externalInfo.setFacebook_id(movieJSONObject.getString("facebook_id"));
        externalInfo.setInstagram_id(movieJSONObject.getString("instagram_id"));
        person.setExternalInfo(externalInfo);
    }
}
