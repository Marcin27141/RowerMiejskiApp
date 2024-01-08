package com.example.firstandroidapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.firstandroidapp.DatabaseHelpers.DatabaseHelper;
import com.example.firstandroidapp.Services.WrmHelper;
import com.example.firstandroidapp.WrmModel.WrmStation;
import java.util.ArrayList;

public class AllStationsFragment extends Fragment {
    private TestStationsRecViewAdapter adapter;
    private final ArrayList<WrmStation> stations;

    public AllStationsFragment(ArrayList<WrmStation> stations) {
        this.stations = stations;
    }

    public AllStationsFragment() {
        this.stations = WrmHelper.getWrmStations();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_stations_test, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.stationsRecView);
        adapter = new TestStationsRecViewAdapter(requireContext(), stations);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        SearchView searchView = view.findViewById(R.id.searchView);
        setUpSearchViewListener(searchView);
        searchView.clearFocus();

        getParentFragmentManager().setFragmentResultListener("StationUnliked", getViewLifecycleOwner(), (requestKey, bundle) -> {
            adapter.updateList();
        });

        return view;
    }

    private void setUpSearchViewListener(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchViewHandler<>(
                stations,
                (station, text) -> (station.id.contains(text) || station.location.name.toLowerCase().contains(text.toLowerCase())),
                filtered -> {
                    adapter.updateList(filtered);
                    if (filtered.isEmpty())
                        Toast.makeText(requireContext(), R.string.no_stations_found_msg, Toast.LENGTH_LONG).show();
                }
        ));
    }

    class TestStationsRecViewAdapter extends StationsListAdapter {
        private ArrayList<String> likedStationsIds;

        public TestStationsRecViewAdapter(Context context, ArrayList<WrmStation> stations) {
            super(context, stations);
            likedStationsIds = new DatabaseHelper(context).getLikedStationsIds();
        }

        @Override
        void onLikedListChanged() {
            getParentFragmentManager().setFragmentResult("LikedListChanged", Bundle.EMPTY);
        }

        @Override
        boolean isStationLiked(WrmStation station) {
            return likedStationsIds.contains(station.id);
        }

        @Override
        public void updateList() {
            likedStationsIds = new DatabaseHelper(context).getLikedStationsIds();
            notifyDataSetChanged();
        }
    }
}

