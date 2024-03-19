package com.example.firstandroidapp.Activities.Stations;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.firstandroidapp.Activities.SearchViewHandler;
import com.example.firstandroidapp.R;
import com.example.firstandroidapp.Services.WrmHelper;
import com.example.firstandroidapp.WrmModel.WrmStation;

import java.util.ArrayList;


public class LikedStationsFragment extends Fragment {

    private final ArrayList<WrmStation> stations;
    private LikedStationsRecyclerViewAdapter adapter;

    public LikedStationsFragment(ArrayList<WrmStation> stations) {
        this.stations = stations;
    }

    public LikedStationsFragment() {
        this.stations = WrmHelper.getWrmStations();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_liked_stations_test, container, false);

        setUpRecyclerView(view);
        setUpSearchView(view);

        getParentFragmentManager().setFragmentResultListener("LikedListChanged", getViewLifecycleOwner(), (requestKey, bundle) -> adapter.updateList());

        return view;
    }

    private void setUpSearchView(View view) {
        SearchView searchView = view.findViewById(R.id.searchView);
        setUpSearchViewListener(searchView);
        searchView.clearFocus();
    }

    private void setUpRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.stationsRecViewTest);
        adapter = new LikedStationsRecyclerViewAdapter(requireContext(), stations);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
    }

    private void setUpSearchViewListener(SearchView searchView) {
        ArrayList<WrmStation> likedStations = WrmHelper.getLikedWrmStations(requireContext());
        searchView.setOnQueryTextListener(new SearchViewHandler<>(
                likedStations,
                (station, text) -> (station.id.contains(text) || station.location.name.toLowerCase().contains(text.toLowerCase())),
                filtered -> {
                    adapter.updateList(filtered);
                    if (filtered.isEmpty() && !likedStations.isEmpty())
                        Toast.makeText(requireContext(), R.string.no_stations_found_msg, Toast.LENGTH_LONG).show();
                }
        ));
    }

    class LikedStationsRecyclerViewAdapter extends StationsListAdapter {
        private final ArrayList<WrmStation> allStations;

        public LikedStationsRecyclerViewAdapter(Context context, ArrayList<WrmStation> stations) {
            super(context, WrmHelper.getLikedWrmStations(context, stations));
            this.allStations = stations;
        }

        @Override
        void onLikedListChanged() {
            updateList();
            getParentFragmentManager().setFragmentResult("StationUnliked", Bundle.EMPTY);
        }

        @Override
        boolean isStationLiked(WrmStation station) {
            return true;
        }

        @Override
        public void updateList() {
            this.listItems = WrmHelper.getLikedWrmStations(context, allStations);
            notifyDataSetChanged();
        }
    }
}