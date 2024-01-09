package com.example.firstandroidapp.ChooseBike;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.firstandroidapp.DatabaseHelpers.BikeRating;
import com.example.firstandroidapp.R;
import com.example.firstandroidapp.Services.RatingHelper;

import java.util.Optional;

public class RatingDescriptionDialog {
    private Context context;
    private RatingHelper ratingHelper;
    public RatingDescriptionDialog(Context context) {
        this.context = context;
        this.ratingHelper = new RatingHelper(context);
    }
    public void showDescription(String clickedBikeId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.description_dialog, null);
        setDialogViewContent(clickedBikeId, dialogView);

        builder.setView(dialogView)
                .setTitle(R.string.description)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setDialogViewContent(String clickedBikeId, View dialogView) {
        TextView dialogTextView = dialogView.findViewById(R.id.descriptionDialogTxt);
        Optional<BikeRating> rating = ratingHelper.GetBikeRating(clickedBikeId);
        boolean descriptionProvided = rating.isPresent() && !rating.get().description.isEmpty();
        String descriptionTxt = descriptionProvided ? rating.get().description : context.getResources().getString(R.string.no_description);
        dialogTextView.setText(descriptionTxt);
    }
}
