package com.example.bandup.userprofile;

public class Item {
    private boolean checked;
    private String ItemString;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getItemString() {
        return ItemString;
    }

    public void setItemString(String itemString) {
        ItemString = itemString;
    }
}