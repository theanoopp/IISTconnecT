package in.rgpvnotes.alert.myresource.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import in.rgpvnotes.alert.myresource.R;
import in.rgpvnotes.alert.myresource.dialog.SlideshowDialogFragment;
import in.rgpvnotes.alert.myresource.models.PhotosModel;

public class GalleryActivity extends AppCompatActivity {

    private static final String TAG = "GalleryActivity";

    private RecyclerView recyclerView;

    private FirebaseFirestore mDatabase;

    private ArrayList<PhotosModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Gallery");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView = findViewById(R.id.gallery_recyclerView);

        mDatabase = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        setupRecyclerView();


    }

    private void setupRecyclerView() {


        Query query = mDatabase.collection("gallery");

        FirestoreRecyclerOptions<PhotosModel> options = new FirestoreRecyclerOptions.Builder<PhotosModel>()
                .setLifecycleOwner(this)
                .setQuery(query, PhotosModel.class)
                .build();


        FirestoreRecyclerAdapter mAdapter = new FirestoreRecyclerAdapter<PhotosModel,ViewHolder>(options) {
            @Override
            public void onBindViewHolder(final ViewHolder holder, int position, final PhotosModel model) {

                holder.bind(model);
                list.add(model);
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("images", list);
                        bundle.putInt("position", holder.getAdapterPosition());

                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                        newFragment.setArguments(bundle);
                        newFragment.show(ft, "slideshow");

                    }
                });

            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.gallery_thumbnail, group, false);
                return new ViewHolder(view);
            }
        };

        recyclerView.setAdapter(mAdapter);


    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView photo;
        private ProgressBar loading;
        private View mView;

        private ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            photo = itemView.findViewById(R.id.thumbnail);
            loading = itemView.findViewById(R.id.loading);
        }

        private void bind(PhotosModel model) {

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(photo.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(model.getDownloadURL())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            photo.setVisibility(View.VISIBLE);
                            loading.setVisibility(View.INVISIBLE);
                            return false;
                        }
                    })
                    .into(photo);


        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
