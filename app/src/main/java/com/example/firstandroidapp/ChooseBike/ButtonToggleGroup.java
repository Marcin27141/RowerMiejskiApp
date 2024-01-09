package com.example.firstandroidapp.ChooseBike;

import com.example.firstandroidapp.R;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class ButtonToggleGroup {
    private MaterialButtonToggleGroup toggleGroup;

    public ButtonToggleGroup(MaterialButtonToggleGroup toggleGroup) {
        this.toggleGroup = toggleGroup;
    }
    void setUpListeners(
            IOnMenuItemClicked onPositiveButton,
            IOnMenuItemClicked onUngradedButton,
            IOnMenuItemClicked onNegativeButton
    ) {
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            if (checkedId == R.id.goodButton)
                onPositiveButton.handleClick();
            else if (checkedId == R.id.mediumButton)
                onUngradedButton.handleClick();
            else if (checkedId == R.id.sadButton)
                onNegativeButton.handleClick();
        });
    }

    public void clearChecked() {
        toggleGroup.clearChecked();
    }
}
