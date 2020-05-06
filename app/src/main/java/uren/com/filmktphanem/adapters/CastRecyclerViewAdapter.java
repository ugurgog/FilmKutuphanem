package uren.com.filmktphanem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import uren.com.filmktphanem.Interfaces.ReturnCallback;
import uren.com.filmktphanem.R;
import uren.com.filmktphanem.data.MyLibraryItem;
import uren.com.filmktphanem.models.Cast;

public class CastRecyclerViewAdapter extends RecyclerView.Adapter<CastRecyclerViewAdapter.MyViewHolder> {
    private Context context;
    private List<Cast> cast;
    private ReturnCallback returnCallback;

    public CastRecyclerViewAdapter(Context context, List<Cast> cast, ReturnCallback returnCallback) {
        this.context = context;
        this.cast = cast;
        this.returnCallback = returnCallback;
    }

    @Override
    public CastRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.cast_item, parent, false);

        return new CastRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CastRecyclerViewAdapter.MyViewHolder holder, final int position) {
        Cast castItem = cast.get(position);
        holder.setData(castItem, position);
    }

    @Override
    public int getItemCount() {
        return cast.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout clCastItem;
        ImageView ivProfile;
        TextView tvName;
        TextView tvCharacter;
        Cast castItem;
        int position;

        public MyViewHolder(View itemView) {
            super(itemView);
            // Binds the current view item's components
            clCastItem = itemView.findViewById(R.id.cl_cast_item);
            ivProfile = itemView.findViewById(R.id.iv_cast_profile);
            tvName = itemView.findViewById(R.id.tv_cast_name);
            tvCharacter = itemView.findViewById(R.id.tv_cast_character);

            // Set animation on imageview
            Animation fadeInAnimation = AnimationUtils.loadAnimation(clCastItem.getContext(), R.anim.fade_in);
            ivProfile.startAnimation(fadeInAnimation);

            clCastItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    returnCallback.onReturn(castItem.getId());
                }
            });
        }

        public void setData(Cast castItem, int position) {
            this.castItem = castItem;
            this.position = position;

            // Assigns the results the the current item's components
            if (castItem.getProfilePath() == "null") {
                Picasso.get().load(R.drawable.profile_placeholder).into(ivProfile);
            } else {
                Picasso.get().load(cast.get(position).getProfileUrl()).into(ivProfile);
            }
            tvName.setText(cast.get(position).getName());
            tvCharacter.setText(cast.get(position).getCharacter());
        }
    }
}
