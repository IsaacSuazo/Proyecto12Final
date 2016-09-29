package com.examisaac.isaac.proyecto12final;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Created by ISAAC on 28/9/16.
 */

public class MainFragmento extends Fragment {

    //objetos para el sdk de facebook
    private AccessToken accessToken;
    private TextView textView;
    private ProfilePictureView profilePictureView;
    private CallbackManager callbackManager;

    //objetos para el banner
    private AdView adView;

    //el callback para el boton de login
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {

        //para cuando el login es exitoso
        @Override
        public void onSuccess(LoginResult loginResult) {
            accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            if(profile != null){
                textView.setText("Bienvenido "+ profile.getName() + "!");
                profilePictureView.setProfileId(profile.getId());
            }
        }

        //para cuando se cansela
        @Override
        public void onCancel() {
            Toast.makeText(getActivity().getApplicationContext(), "No se pudo hacer login", Toast.LENGTH_SHORT).show();
        }

        //para cuando es erroneo
        @Override
        public void onError(FacebookException error) {
            Toast.makeText(getActivity().getApplicationContext(), "Error! No se pudo hacer login", Toast.LENGTH_SHORT).show();
        }
    };

    //constructor
    public MainFragmento(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //para el sdk de facebook
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        AppEventsLogger.activateApp(getActivity().getApplicationContext());
        //para instanciar el callback manager
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_fragmento, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //boton para el login
        LoginButton loginButton = (LoginButton)view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        loginButton.setFragment(this);
        //para el callback del boton
        loginButton.registerCallback(callbackManager, callback);

        //enlazamos los objetos a la vista
        textView = (TextView) view.findViewById(R.id.tvBienvenido);
        profilePictureView = (ProfilePictureView)view.findViewById(R.id.profilePicture);

        //para el token del login
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken == null){
                    textView.setText("Sesion no iniciada");
                    profilePictureView.setProfileId("");
                }
            }
        };

        //para iniciar la sesion al abrir la aplicacion
        Profile profile = Profile.getCurrentProfile();
        if(profile != null){
            textView.setText("Bienvenido "+ profile.getName() + "!");
            profilePictureView.setProfileId(profile.getId());
        }else{
            textView.setText("Sesion no iniciada");
            profilePictureView.setProfileId("");
        }

        //para el unir el banner a la vista
        adView = (AdView) view.findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Resume the AdView.
        if(adView != null){
            adView.resume();
        }
    }

    @Override
    public void onPause() {
        // Pause the AdView.
        if(adView != null){
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        // Destroy the AdView.
        if(adView != null){
            adView.destroy();
        }
        super.onDestroy();
    }

    //para el resultado de la actividad
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
