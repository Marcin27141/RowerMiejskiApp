package com.example.firstandroidapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.firstandroidapp.DatabaseHelpers.DatabaseHelper;
import com.example.firstandroidapp.WrmModel.WrmStation;

import java.util.ArrayList;


public class AllStationsFragmentTest extends Fragment {
    private TestStationsRecViewAdapter adapter;
    private ArrayList<WrmStation> stations;

    public AllStationsFragmentTest(ArrayList<WrmStation> stations) {
        this.stations = stations;
    }

    public AllStationsFragmentTest() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_stations_test, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.stationsRecView);
        adapter = new TestStationsRecViewAdapter(requireContext(), WrmHelper.getWrmStations());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        SearchView searchView = view.findViewById(R.id.searchView);
        //setUpSearchViewListener();
        searchView.clearFocus();

        getParentFragmentManager().setFragmentResultListener("StationUnliked", getViewLifecycleOwner(), (requestKey, bundle) -> {
            adapter.updateList();
        });

        return view;
    }

    class TestStationsRecViewAdapter extends RecyclerView.Adapter<TestStationsRecViewAdapter.ViewHolder> {

        private Context context;
        private ArrayList<WrmStation> stations;
        private ArrayList<String> likedStationsIds;
        private OnFavouriteButtonClickedListener listener;

        public TestStationsRecViewAdapter(Context context, ArrayList<WrmStation> stations) {
            this.context = context;
            this.stations = stations;
            likedStationsIds = new DatabaseHelper(context).getLikedStationsIds();
        }

        public void setOnItemClickListener(OnFavouriteButtonClickedListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.station_card_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.stationLocationTxt.setText(stations.get(position).location.name);
            holder.stationIdTxt.setText((stations.get(position).id));
            holder.parent.setOnClickListener(view -> {
                showChooseBikeActivity(position);
            });

            holder.starIconCheckBox.setOnClickListener(v -> {
                boolean isChecked = ((CheckBox)v).isChecked();

                String stationId = holder.stationIdTxt.getText().toString();

                try (DatabaseHelper dbHelper = new DatabaseHelper(context)) {
                    if (isChecked) {
                        dbHelper.addLikedStation(stationId);
                    }
                    else
                    {
                        dbHelper.removeLikedStation(stationId);
                    }
                    likedStationsIds = new DatabaseHelper(context).getLikedStationsIds();
                }

//                if (listener != null) {
//                    listener.onFavouriteButtonClicked(stations.get(position));
//                }

                getParentFragmentManager().setFragmentResult("LikedListChanged", Bundle.EMPTY);


//                for (OnStationLikedListener listener : onStationLikedListeners)
//                    listener.onStationLiked(this, stations.get(position), isChecked);
            });


            holder.starIconCheckBox.setChecked(likedStationsIds.contains(holder.stationIdTxt.getText().toString()));
        }

        private void showChooseBikeActivity(int stationsPosition) {
            Intent intent = new Intent(context, ChooseBikeActivity.class);
            intent.putExtra("SERIALIZED_STATION", stations.get(stationsPosition));
            context.startActivity(intent);
        }

        @Override
        public int getItemCount() {
            return stations.size();
        }

        public void updateList() {
            likedStationsIds = new DatabaseHelper(context).getLikedStationsIds();
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final CardView parent;
            private final TextView stationLocationTxt, stationIdTxt;
            private final CheckBox starIconCheckBox;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                parent = itemView.findViewById(R.id.parent);
                stationLocationTxt = itemView.findViewById(R.id.stationLocation);
                stationIdTxt = itemView.findViewById(R.id.stationId);
                starIconCheckBox = itemView.findViewById(R.id.star_icon);
            }
        }


    }

    interface OnFavouriteButtonClickedListener {
        void onFavouriteButtonClicked(WrmStation station);
    }
}

