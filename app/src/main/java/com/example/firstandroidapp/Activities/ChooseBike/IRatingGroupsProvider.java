package com.example.firstandroidapp.Activities.ChooseBike;

import com.example.firstandroidapp.WrmModel.WrmStation;

public interface IRatingGroupsProvider {
    IRatingGroupsHolder getRatingGroups(WrmStation station);
}
