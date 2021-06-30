package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
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
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    public static final String TAG = "TweetsAdapter";

    Context context;
    List<Tweet> tweets;
    ItemTweetBinding binding;

    // Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
        setHasStableIds(true);
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
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
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
                binding.tvRetweetCount.setTextColor(ContextCompat.getColor(context, R.color.inline_action));
            }
        }
    }
}
