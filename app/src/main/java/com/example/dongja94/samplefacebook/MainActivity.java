package com.example.dongja94.samplefacebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.DefaultAudience;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    LoginButton loginButton;
    CallbackManager callbackManager;
    LoginManager loginManager;
    Button customLogin;
    AccessTokenTracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        loginButton = (LoginButton)findViewById(R.id.btn_login);
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken token = AccessToken.getCurrentAccessToken();
                if (token != null) {
                    Toast.makeText(MainActivity.this, "success : " + token.getToken(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });

        loginManager = LoginManager.getInstance();

        Button btn = (Button)findViewById(R.id.btn_other);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
            }
        });

        btn = (Button)findViewById(R.id.btn_me);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccessToken token = AccessToken.getCurrentAccessToken();
                if (token != null) {
                    GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            if (response.getError() == null) {
                                Toast.makeText(MainActivity.this, "data : " + response.getJSONObject().toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "error : " + response.getError().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    request.executeAsync();
                }
            }
        });

        customLogin = (Button)findViewById(R.id.btn_custom);
        customLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginOrLogout();
            }
        });

        tracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                displayCustom();
            }
        };

        btn = (Button)findViewById(R.id.btn_read_post);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readPost();
            }
        });

        btn = (Button)findViewById(R.id.btn_write_post);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writePost();
            }
        });
    }

    private void writePost() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token != null) {
            if (token.getPermissions().contains("publish_actions")) {
                Bundle parameters = new Bundle();
                parameters.putString("message","facebook api test");
                parameters.putString("link", "http://developers.facebook.com/docs/android");
                parameters.putString("picture", "https://raw.github.com/fbsamples/.../iossdk_logo.png");
                parameters.putString("name", "Hello Facebook");
                parameters.putString("description", "The 'Hello Facebook' sample  showcases simple …");
                GraphRequest request = new GraphRequest(token, "/me/feed", parameters, HttpMethod.POST, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if (response.getError() == null) {
                            Toast.makeText(MainActivity.this, "post id : " + response.getRawResponse(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "error : " + response.getError().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                request.executeAsync();
                return;
            }
        }
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                writePost();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        loginManager.setDefaultAudience(DefaultAudience.FRIENDS);
        loginManager.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
        loginManager.logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
    }

    private void readPost() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token != null) {
            if (token.getPermissions().contains("user_posts")) {
                GraphRequest request = new GraphRequest(token, "/me/feed", null, HttpMethod.GET, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if (response.getError() == null) {
                            Toast.makeText(MainActivity.this, "data : " + response.getRawResponse(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "error : " + response.getError().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                request.executeAsync();
                return;
            }
        }
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                readPost();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        loginManager.setDefaultAudience(DefaultAudience.FRIENDS);
        loginManager.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
        loginManager.logInWithReadPermissions(this, Arrays.asList("user_posts"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        tracker.startTracking();
        displayCustom();
    }

    @Override
    protected void onStop() {
        super.onStop();
        tracker.stopTracking();
    }

    private void displayCustom() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token != null) {
            customLogin.setText("logout");
        } else {
            customLogin.setText("login");
        }
    }

    private void loginOrLogout() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token == null) {
            loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {

                }
            });
            loginManager.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
            loginManager.setDefaultAudience(DefaultAudience.FRIENDS);
            loginManager.logInWithReadPermissions(this, null);
        } else {
            loginManager.logOut();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
