package com.example.bandup.userprofile;

import android.widget.CheckBox;
import android.widget.TextView;

public class ViewHolder {
    private CheckBox checkBox;
    private TextView text;

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public TextView getText() {
        return text;
    }

    public void setText(TextView text) {
        this.text = text;
    }
}