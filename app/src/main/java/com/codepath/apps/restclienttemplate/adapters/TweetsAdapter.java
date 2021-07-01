package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    public static final String TAG = "TweetsAdapter";

    Context context;
    List<Tweet> tweets;
    ItemTweetBinding binding;
    TwitterClient client;
    Tweet tweet;
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

        client = TwitterApp.getRestClient(context);
        ViewHolder holder = new ViewHolder(view, new clickListener() {
            @Override
            public void onReply(int p) {
                // TODO: implement reply
            }

            @Override
            public void onRetweet(int p) {
                // TODO: implement retweet
            }

            @Override
            public void onLike(int p) {
                tweet = tweets.get(p);
                String id = tweet.id;
                tweet.liked = tweet.liked ? false : true;
                position = p;
                if (tweet.liked)
                    client.likeTweet(id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "onSuccess to like tweet");
                            tweet.liked = true;

                            Glide.with(context).load(R.drawable.ic_vector_heart).into(binding.ivLike);
                            binding.tvLikeCount.setTextColor(ContextCompat.getColor(context, R.color.inline_action_like));

                            tweet.likeCount++;
                            binding.tvLikeCount.setText(String.valueOf(tweet.likeCount));
                            notifyItemChanged(position);
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
                            tweet.liked = false;

                            Glide.with(context).load(R.drawable.ic_vector_heart_stroke).into(binding.ivLike);
                            binding.tvLikeCount.setTextColor(ContextCompat.getColor(context, R.color.inline_action));

                            tweet.likeCount--;
                            binding.tvLikeCount.setText(String.valueOf(tweet.likeCount));
                            notifyItemChanged(position);
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.i(TAG, "onFailure to like tweet");
                        }
                    });
                }
            }
        });
        return holder;
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet = tweets.get(position);
        // Bind the tweet with the view holder
        holder.bind(tweet);
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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        clickListener listener;

        public ViewHolder(@NonNull View itemView, clickListener listener) {
            super(itemView);
            this.listener = listener;
            binding.ivReply.setOnClickListener(this);
            binding.ivRetweet.setOnClickListener(this);
            binding.ivLike.setOnClickListener(this);
        }

        public void bind(Tweet tweet) {
            Glide.with(context).load(tweet.user.profileImageUrl).circleCrop().into(binding.ivProfileImage);
            binding.tvBody.setText(tweet.body);
            binding.tvName.setText(tweet.user.name);
            binding.tvScreenName.setText(tweet.user.screenName);
            binding.tvTime.setText(tweet.getRelativeTimeAgo());

            if (tweet.mediaUrl != null) {
                Log.d(TAG, "Loading image: " + tweet.mediaUrl);
                Glide.with(context)
                    .load(tweet.mediaUrl)
                    .transform(new RoundedCornersTransformation(60, 0))
                    .into(binding.ivMedia);
                binding.ivMedia.setVisibility(View.VISIBLE);
            } else {
                Log.d(TAG, "No image: " + tweet.mediaUrl);
                binding.ivMedia.setVisibility(View.GONE);
            }

            Glide.with(context).load(R.drawable.ic_vector_reply).into(binding.ivReply);
            binding.tvRetweetCount.setText(String.valueOf(tweet.retweetCount));
            binding.tvLikeCount.setText(String.valueOf(tweet.likeCount));

            if (tweet.retweeted) {
                Glide.with(context).load(R.drawable.ic_vector_retweet).into(binding.ivRetweet);
                binding.tvRetweetCount.setTextColor(ContextCompat.getColor(context, R.color.inline_action_retweet));
            } else {
                Glide.with(context).load(R.drawable.ic_vector_retweet_stroke).into(binding.ivRetweet);
                binding.tvRetweetCount.setTextColor(ContextCompat.getColor(context, R.color.inline_action));
            }
            if (tweet.liked) {
                Glide.with(context).load(R.drawable.ic_vector_heart).into(binding.ivLike);
                binding.tvLikeCount.setTextColor(ContextCompat.getColor(context, R.color.inline_action_like));
            } else {
                Glide.with(context).load(R.drawable.ic_vector_heart_stroke).into(binding.ivLike);
                binding.tvLikeCount.setTextColor(ContextCompat.getColor(context, R.color.inline_action));
            }
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ivReply:
                    listener.onReply(this.getLayoutPosition());
                    break;
                case R.id.ivRetweet:
                    listener.onRetweet(this.getLayoutPosition());
                    break;
                case R.id.ivLike:
                    listener.onLike(this.getLayoutPosition());
                    break;
                default:
                    break;
            }
        }
    }
    public interface clickListener {
        void onReply(int p);
        void onRetweet(int p);
        void onLike(int p);
    }
}
