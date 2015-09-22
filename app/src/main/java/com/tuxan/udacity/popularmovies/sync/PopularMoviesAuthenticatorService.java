package com.tuxan.udacity.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class PopularMoviesAuthenticatorService extends Service{

    private PopularMoviesAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new PopularMoviesAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
