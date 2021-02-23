package com.example.user;

import android.content.Context;
import android.content.Intent;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.UserViewHolder> {

    private Context mContext;
    private List<Model> mymodelList;
    private int lastPosition = -1;
    private MyAdapterListener listener;
    String key="";
    DatabaseReference reff;
    public interface MyAdapterListener {
        void onContactSelected(Model model);

        void deleteItem(Model model);

    }
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
            //Booking b = dataSnapshot1.getValue(Booking.class);
            dataSnapshot1.getRef().removeValue();
        }
    }
    public MyAdapter(Context mContext, List<Model> mymodelList) {
        this.mContext = mContext;
        this.mymodelList = mymodelList;
    }

    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_row_item, viewGroup, false);

        return new UserViewHolder(mView);
    }


    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder userViewHolder, int i) {


        Glide.with(mContext)
                .load(mymodelList.get(i).getImage())
                .apply(RequestOptions.circleCropTransform())
                .into(userViewHolder.imageView);

        // foodViewHolder.imageView.setImageResource(myFoodList.get(i).getItemImage());
        userViewHolder.name.setText(mymodelList.get(i).getName());
        userViewHolder.gender.setText(mymodelList.get(i).getGender());
        userViewHolder.place.setText(mymodelList.get(i).getHometown());
        reff= FirebaseDatabase.getInstance().getReference().child("Users");
        userViewHolder.btndelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference storageReference = storage.getReferenceFromUrl(mymodelList.get(i).getImage());

                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        reference.child(mymodelList.get(i).getKey()).removeValue();
                        Toast.makeText(v.getContext(), "User Deleted", Toast.LENGTH_SHORT).show();


                    }
                });






            }
        });

        setAnimation(userViewHolder.itemView, i);

    }

    public void setAnimation(View viewToAnimate, int position) {

        if (position > lastPosition) {

            ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(1500);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;


        }


    }

    @Override
    public int getItemCount() {
        return mymodelList.size();
    }


    public void filteredList(ArrayList<Model> filterList) {

        mymodelList = filterList;
        notifyDataSetChanged();
    }


    class UserViewHolder extends RecyclerView.ViewHolder {


        ImageView imageView;
        TextView name, gender, place;
        ImageButton btndelete;

        public UserViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.ivImage);
            name = itemView.findViewById(R.id.name);
            gender = itemView.findViewById(R.id.gender);
            place = itemView.findViewById(R.id.place);
            btndelete = itemView.findViewById(R.id.btnRemove);


        }


    }
}

