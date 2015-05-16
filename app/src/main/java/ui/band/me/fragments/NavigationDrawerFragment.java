package ui.band.me.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import ui.band.me.R;
import ui.band.me.adapters.DrawerRecyclerAdapter;
import ui.band.me.extras.DrawerItemInfo;
import ui.band.me.extras.RoundedTransformation;
import ui.band.me.listeners.RecyclerTouchListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment {

    private static final String TWITTER_KEY = "5qruaMPBhd6T6XKrd6UfnRniG";
    private static final String TWITTER_SECRET = "9gcTJhyDH0mSW7A2BBVAp7arDwsdN4UGbGutt05m1oybCiTxTQ";
    protected TwitterLoginButton loginButton;
    protected TwitterAuthConfig authConfig;

    public static final String PREF_FILE_NAME = "bandMePref";
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;

    private View mContainerView;

    private RecyclerView recyclerView;
    private DrawerRecyclerAdapter recyclerAdapter;


    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));
        if (savedInstanceState != null) {
            mFromSavedInstanceState = true;
        }

        authConfig = new TwitterAuthConfig(TWITTER_KEY,
                TWITTER_SECRET);
        Fabric.with(this.getActivity(), new TwitterCore(authConfig));


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        this.recyclerView = (RecyclerView) layout.findViewById(R.id.drawer_list);
        this.recyclerAdapter = new DrawerRecyclerAdapter(getActivity(),getData());
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //TODO: meter a iniciar as atividades
                //startActivity(new Intent(getActivity(),SubActivity.class));
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));


        loginButton = (TwitterLoginButton) layout.findViewById(R.id.login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = result.data;
                Log.d("Twitter",session.getUserName());
                TwitterApiClient twitterApi = TwitterCore.getInstance().getApiClient();
                twitterApi.getAccountService().verifyCredentials(null,null,new Callback<User>() {
                            @Override
                            public void success(Result<User> userResult) {
                                User user = userResult.data;
                                showImage(layout,user.profileImageUrlHttps);
                            }
                            @Override
                            public void failure(TwitterException e) {
                                Log.d("Twitter", "failure");
                            }
                        }
                );
            }
            @Override
            public void failure(TwitterException exception) {
                Log.d("Twitter","Result failure");
                exception.printStackTrace();
            }
        });


        return layout;

    }

    public static List<DrawerItemInfo> getData() {
        List<DrawerItemInfo> data = new ArrayList<>();
        //TODO: mudar isto para as cenas que queremos
        int[] icons = {R.drawable.abc_btn_check_material,R.drawable.abc_btn_check_material};
        String[] titles = {"Item melaco 1","Item melaco 2"};
        for(int i = 0; i < titles.length && i < icons.length;i++) {
            DrawerItemInfo info = new DrawerItemInfo();
            info.iconId = icons[i];
            info.title = titles[i];
            data.add(info);
        }
        return data;
    }

    private void showImage(View layout, String url) {
        Log.d("Twitter",url);
        ImageView profile_picture = (ImageView) layout.findViewById(R.id.profile_image);
        Picasso.with(this.getActivity()).load(url)
                .transform(new RoundedTransformation(100, 0))
                .fit()
                .into(profile_picture);
    }


    public void setUp(int fragment_id, DrawerLayout drawerLayout, final Toolbar toolbar) {
        this.mContainerView = getActivity().findViewById(fragment_id);
        this.mDrawerLayout = drawerLayout;
        this.mDrawerToggle = new ActionBarDrawerToggle(getActivity(),drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    saveToPreferences(getActivity(),KEY_USER_LEARNED_DRAWER,mUserLearnedDrawer+"");
                }

                getActivity().invalidateOptionsMenu();

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if(slideOffset<0.6)
                    toolbar.setAlpha(1-slideOffset);
            }
        };
        if(!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mContainerView);
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName,preferenceValue);
        editor.apply();
    }

    public static String readFromPreferences(Context context, String preferenceName, String defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME,context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName,defaultValue);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result to the login button.
        Log.d("Fragment","Activity result");
        Log.d("Data",data.toString());
        loginButton.onActivityResult(requestCode, resultCode,data);
    }


}
