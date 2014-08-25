package ru.interosite.vkapidemo;

import android.app.ListActivity;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Created by cyrusmith
 * All rights reserved
 * http://interosite.ru
 * info@interosite.ru
 */
public class MainActivity extends ListActivity {

    private static final String VK_APP_ID = "4520250";

    private class FriendsLoader extends AsyncTaskLoader<List<VKApiUser>> {

        private VKRequest request = null;

        public FriendsLoader(Context context) {
            super(context);
        }

        @Override
        public boolean cancelLoad() {

            if (request != null) {
                synchronized (this) {
                    if (request != null) {
                        request.cancel();
                    }
                }
            }

            return super.cancelLoad();
        }

        @Override
        public List<VKApiUser> loadInBackground() {

            if (request != null) {
                request.cancel();
            }

            synchronized (this) {
                request = VKApi.users().get();
            }

            final CountDownLatch latch = new CountDownLatch(1);

            final List<VKApiUser> users = new CopyOnWriteArrayList<VKApiUser>();

            request.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    latch.countDown();
                }

                @Override
                public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                    //TODO fil the array
                    latch.countDown();
                }

                @Override
                public void onError(VKError error) {
                    latch.countDown();
                }

                @Override
                public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                    //TODO
                }

            });

            try {
                latch.await();
                return users;
            } catch (InterruptedException e) {
                return null;
            }

        }

    }

    ;

    private final VKSdkListener sdkListener = new VKSdkListener() {

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            Log.d("VkDemoApp", "onAcceptUserToken " + token);
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            Log.d("VkDemoApp", "onReceiveNewToken " + newToken);
        }

        @Override
        public void onRenewAccessToken(VKAccessToken token) {
            Log.d("VkDemoApp", "onRenewAccessToken " + token);
        }

        @Override
        public void onCaptchaError(VKError captchaError) {
            Log.d("VkDemoApp", "onCaptchaError " + captchaError);
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            Log.d("VkDemoApp", "onTokenExpired " + expiredToken);
        }

        @Override
        public void onAccessDenied(VKError authorizationError) {
            Log.d("VkDemoApp", "onAccessDenied " + authorizationError);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        VKSdk.initialize(sdkListener, VK_APP_ID);

        if (!VKSdk.isLoggedIn()) {
            final Button loginButton = (Button) findViewById(R.id.login_button);
            loginButton.setVisibility(View.VISIBLE);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VKSdk.authorize(VKScope.FRIENDS, VKScope.PHOTOS);
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
    }

}