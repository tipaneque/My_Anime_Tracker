package com.gitlab.bluestring.myanimetracker.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.gitlab.bluestring.myanimetracker.model.Tag;
import java.util.List;

public class TagAdapter extends ArrayAdapter<Tag> {

    public TagAdapter(@NonNull Context context, @NonNull List<Tag> tags) {
        super(context, android.R.layout.simple_spinner_item, tags);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        Tag tag = getItem(position);

        if (tag != null) {
            textView.setText(tag.getName() + " (" + tag.getPostCount() + ")");
        }

        return convertView;
    }
}
