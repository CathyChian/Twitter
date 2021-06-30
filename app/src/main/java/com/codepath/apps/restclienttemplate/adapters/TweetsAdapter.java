package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    public static final String TAG = "TweetsAdapter";

    Context context;
    List<Tweet> tweets;
    ItemTweetBinding binding;

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
        return new ViewHolder(view);
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
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(Tweet tweet) {
            Glide.with(context).load(tweet.user.profileImageUrl).into(binding.ivProfileImage);
            binding.tvBody.setText(tweet.body);
            binding.tvName.setText(tweet.user.name);
            binding.tvScreenName.setText(tweet.user.screenName);
            binding.tvTime.setText(tweet.getRelativeTimeAgo());

            if (tweet.mediaUrl != null) {
                Log.d(TAG, "Loading image: " + tweet.mediaUrl);
                Glide.with(context).load(tweet.mediaUrl).into(binding.ivMedia);
                binding.ivMedia.setVisibility(View.VISIBLE);
            } else {
                Log.d(TAG, "No image: " + tweet.mediaUrl);
                binding.ivMedia.setVisibility(View.GONE);
            }

            Glide.with(context).load(R.drawable.twitter_reply).into(binding.ivReply);
            Glide.with(context).load(R.drawable.ic_retweet_twitter).into(binding.ivRetweet);
            Glide.with(context).load(R.drawable.ic_like_twitter).into(binding.ivLike);
            binding.tvRetweetCount.setText(tweet.retweetCount);
            binding.tvLikeCount.setText(tweet.likeCount);
        }
    }
}
