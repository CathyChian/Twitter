package com.codepath.apps.restclienttemplate.models;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.FragmentReplyBinding;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcel;
import org.parceler.Parcels;

import okhttp3.Headers;
// ...

public class ReplyDialogFragment extends DialogFragment {

    public static final String TAG = "ReplyDialogFragment";
    public static final int MAX_TWEET_LENGTH = 280;
    FragmentReplyBinding binding;
    private TwitterClient client;
    public ReplyDialogListener listener;
    String screenName, id;

    public ReplyDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ReplyDialogFragment newInstance(String screenName, String id) {
        ReplyDialogFragment frag = new ReplyDialogFragment();
        Bundle args = new Bundle();
        args.putString("screenName", screenName);
        args.putString("id", id);
        frag.setArguments(args);
        frag.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        return frag;
    }

    public interface ReplyDialogListener {
        void onReplyResult(int requestCode, int resultCode, @Nullable Intent data);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReplyBinding.inflate(getActivity().getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        client = TwitterApp.getRestClient(getActivity());
        listener = (ReplyDialogListener) getActivity();

        // Fetch arguments from bundle
        screenName = getArguments().getString("screenName", "username");
        id = getArguments().getString("id", "null");

        binding.etCompose.setHint("Replying to " + screenName);
        binding.etCompose.setText(screenName + " ");

        // Show soft keyboard automatically and request focus to field
        binding.etCompose.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // 2. Setup a callback when the "Done" button is pressed on keyboard
        // Set click listener on button
        binding.btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View view) {
            final String tweetContent = binding.etCompose.getText().toString();
            if (tweetContent.isEmpty()) {
                Toast.makeText(getActivity(), "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show();
                return;
            }
            if (tweetContent.length() > MAX_TWEET_LENGTH) {
                Toast.makeText(getActivity(), "Sorry, your tweet is too long", Toast.LENGTH_LONG).show();
                return;
            }
            // Make an API call to Twitter to publish the tweet
            client.replyTweet(tweetContent, id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.i(TAG, "onSuccess to reply");
                    try {
                        Tweet tweet = Tweet.fromJson(json.jsonObject);
                        Log.i(TAG, "Published tweet says: " + tweet.body);
                        Intent intent = new Intent();
                        intent.putExtra("tweet", Parcels.wrap(tweet));
                        // set result code and bundle data for response
                        getActivity().setResult(getActivity().RESULT_OK, intent);
                        listener.onReplyResult(20, getActivity().RESULT_OK, intent);
                        // close the activity and pass data to parent
                        dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e(TAG, "onFailure to reply. Content: " + tweetContent + " Response: " + response + ", Status code: " + statusCode, throwable);
                }
            });
            }
        });
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }
}