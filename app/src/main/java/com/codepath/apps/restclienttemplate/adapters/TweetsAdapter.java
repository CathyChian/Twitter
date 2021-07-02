package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.ComposeDialogFragment;
import com.codepath.apps.restclienttemplate.models.ReplyDialogFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    public static final String TAG = "TweetsAdapter";

    Context context;
    List<Tweet> tweets;
    ItemTweetBinding binding;
    TwitterClient client;
    //Tweet tweet;
    int position;

    // Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // For each row, inflate a layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemTweetBinding.inflate(LayoutInflater.from(context), parent, false);
        View view = binding.getRoot();
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet = tweets.get(position);
        // Bind the tweet with the view holder
        holder.setCurrentTweet(tweet);
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }


    // Define a view holder
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvBody;
        TextView tvName;
        TextView tvScreenName;
        TextView tvTime;
        TextView tvRetweetCount;
        TextView tvLikeCount;
        ImageView ivProfileImage;
        ImageView ivMedia;
        ImageView ivReply;
        ImageView ivRetweet;
        ImageView ivLike;

        Tweet currTweet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tvBody = binding.tvBody;
            this.tvName = binding.tvName;
            this.tvScreenName = binding.tvScreenName;
            this.tvTime = binding.tvTime;
            this.tvRetweetCount = binding.tvRetweetCount;
            this.tvLikeCount = binding.tvLikeCount;
            this.ivProfileImage = binding.ivProfileImage;
            this.ivMedia = binding.ivMedia;
            this.ivReply = binding.ivReply;
            this.ivRetweet = binding.ivRetweet;
            this.ivLike = binding.ivLike;
            client = TwitterApp.getRestClient(context);
        }

        public void setCurrentTweet(Tweet tweet){
            this.currTweet = tweet;
        }
        public void bind() {
            if(currTweet != null) {
                Log.d(TAG, currTweet.body);
                Glide.with(context).load(currTweet.user.profileImageUrl).circleCrop().into(ivProfileImage);
                tvBody.setText(currTweet.body);
                tvName.setText(currTweet.user.name);
                tvScreenName.setText(currTweet.user.screenName);
                tvTime.setText(currTweet.getRelativeTimeAgo());

                if (currTweet.mediaUrl != null) {
                    Log.d(TAG, "Loading image: " + currTweet.mediaUrl);
                    Glide.with(context)
                            .load(currTweet.mediaUrl)
                            .transform(new RoundedCornersTransformation(60, 0))
                            .into(ivMedia);
                    ivMedia.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, "No image: " + currTweet.mediaUrl);
                    ivMedia.setVisibility(View.GONE);
                }

                Glide.with(context).load(R.drawable.ic_vector_reply).into(ivReply);
                tvRetweetCount.setText(String.valueOf(currTweet.retweetCount));
                tvLikeCount.setText(String.valueOf(currTweet.likeCount));

                if (currTweet.retweeted) {
                    Glide.with(context).load(R.drawable.ic_vector_retweet).into(ivRetweet);
                    tvRetweetCount.setTextColor(ContextCompat.getColor(context, R.color.inline_action_retweet));
                } else {
                    Glide.with(context).load(R.drawable.ic_vector_retweet_stroke).into(ivRetweet);
                    tvRetweetCount.setTextColor(ContextCompat.getColor(context, R.color.inline_action));
                }
                if (currTweet.liked) {
                    Glide.with(context).load(R.drawable.ic_vector_heart).into(ivLike);
                    tvLikeCount.setTextColor(ContextCompat.getColor(context, R.color.inline_action_like));
                } else {
                    Glide.with(context).load(R.drawable.ic_vector_heart_stroke).into(ivLike);
                    tvLikeCount.setTextColor(ContextCompat.getColor(context, R.color.inline_action));
                }

                ivReply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onReply();
                    }
                });
                ivRetweet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRetweet();
                    }
                });
                ivLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onLike();
                    }
                });
            }
        }

        public void onReply() {
            FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
            ReplyDialogFragment replyDialogFragment = ReplyDialogFragment.newInstance(currTweet.user.screenName, currTweet.id);
            replyDialogFragment.show(fm, "fragment_reply");
        }

        public void onRetweet() {
            // TODO: implement retweet
        }

        public void onLike() {
            if(currTweet != null) {
                String id = currTweet.id;
                currTweet.liked = !currTweet.liked;

                if (currTweet.liked)
                    client.likeTweet(id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "onSuccess to like tweet");

                            Glide.with(context).load(R.drawable.ic_vector_heart).into(ivLike);
                            tvLikeCount.setTextColor(ContextCompat.getColor(context, R.color.inline_action_like));

                            currTweet.likeCount++;
                            tvLikeCount.setText(String.valueOf(currTweet.likeCount));
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.i(TAG, "onFailure to like tweet");
                        }
                    });
                else {
                    client.unlikeTweet(id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "onSuccess to like tweet");

                            Glide.with(context).load(R.drawable.ic_vector_heart_stroke).into(ivLike);
                            tvLikeCount.setTextColor(ContextCompat.getColor(context, R.color.inline_action));

                            currTweet.likeCount--;
                            tvLikeCount.setText(String.valueOf(currTweet.likeCount));
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.i(TAG, "onFailure to like tweet");
                        }
                    });
                }
            }
        }
    }
}
