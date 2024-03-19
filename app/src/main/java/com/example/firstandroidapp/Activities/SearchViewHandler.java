package com.example.firstandroidapp.Activities;

import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class SearchViewHandler<T> implements SearchView.OnQueryTextListener {

    private final List<T> items;
    private final BiPredicate<T, String> predicate;
    private final Consumer<ArrayList<T>> finishingFunction;
    public SearchViewHandler(List<T> items, BiPredicate<T, String> predicate, Consumer<ArrayList<T>> finishingFunction) {
        this.items = items;
        this.predicate = predicate;
        this.finishingFunction = finishingFunction;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filterList(newText);
        return true;
    }

    private void filterList(String text) {
        ArrayList<T> filteredList = new ArrayList<>();
        for (T item : items) {
            if (predicate.test(item, text)) {
                filteredList.add(item);
            }
        }
        finishingFunction.accept(filteredList);
    }
}
