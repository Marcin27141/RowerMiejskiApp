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
import java.util.stream.Collectors;


public class LikedStationsFragmentTest extends Fragment {

    private Context context;

    private SearchView searchView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<WrmStation> stations;

    public LikedStationsFragmentTest(ArrayList<WrmStation> stations) {
        this.stations = stations;
    }

    public LikedStationsFragmentTest() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private TestStationsRecViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_liked_stations_test, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.stationsRecViewTest);

        adapter = new TestStationsRecViewAdapter(requireContext(), WrmHelper.getWrmStations());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));

        searchView = view.findViewById(R.id.searchView);
        //setUpSearchViewListener();
        searchView.clearFocus();

        getParentFragmentManager().setFragmentResultListener("LikedListChanged", getViewLifecycleOwner(), (requestKey, bundle) -> {
            adapter.updateList();
        });

        return view;
    }

    private ArrayList<WrmStation> getLikedStations(ArrayList<WrmStation> stations, ArrayList<String> likedStationsIds) {
        ArrayList<WrmStation> filteredStations = new ArrayList<>();

        for (WrmStation station : stations) {
            if (likedStationsIds.contains(station.id)) {
                filteredStations.add(station);
            }
        }

        return filteredStations;
    }

    class TestStationsRecViewAdapter extends RecyclerView.Adapter<TestStationsRecViewAdapter.ViewHolder> {

        private Context context;
        private ArrayList<WrmStation> allStations;
        private ArrayList<WrmStation> likedStations;

        public TestStationsRecViewAdapter(Context context, ArrayList<WrmStation> stations) {
            this.context = context;
            this.allStations = stations;
            this.likedStations = getLikedStations(allStations);
        }

        private ArrayList<WrmStation> getLikedStations(ArrayList<WrmStation> stations) {
            ArrayList<String> liked = new DatabaseHelper(context).getLikedStationsIds();
            return stations.stream().filter(s -> liked.contains(s.id)).collect(Collectors.toCollection(ArrayList::new));
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.station_card_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.stationLocationTxt.setText(likedStations.get(position).location.name);
            holder.stationIdTxt.setText((likedStations.get(position).id));
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
                }

                getParentFragmentManager().setFragmentResult("StationUnliked", Bundle.EMPTY);
                updateList();

                //this.likedStations = getLikedStations(wrmList, new DatabaseHelper(requireContext()).getLikedStationsIds())

//                for (OnStationLikedListener listener : onStationLikedListeners)
//                    listener.onStationLiked(this, stations.get(position), isChecked);
            });


            holder.starIconCheckBox.setChecked(true);
        }

        private void showChooseBikeActivity(int stationsPosition) {
            Intent intent = new Intent(context, ChooseBikeActivity.class);
            intent.putExtra("SERIALIZED_STATION", likedStations.get(stationsPosition));
            context.startActivity(intent);
        }

        @Override
        public int getItemCount() {
            return likedStations.size();
        }

        public void updateList() {
            this.likedStations = getLikedStations(allStations);;
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

}