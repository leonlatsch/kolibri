package de.leonlatsch.olivia.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.leonlatsch.olivia.R;
import de.leonlatsch.olivia.database.model.Contact;
import de.leonlatsch.olivia.rest.dto.UserDTO;
import de.leonlatsch.olivia.util.ImageUtil;

public class ContactAdapter extends ArrayAdapter<Contact> {

    private List<Contact> dataset;
    Context mContext;

    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }

    public ContactAdapter(Context context, List<Contact> users) {
        super(context, 0, users);
        this.dataset = users;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contact user = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
            viewHolder.imageView = convertView.findViewById(R.id.item_user_search_card_view).findViewById(R.id.item_user_search_image_view);
            viewHolder.textView = convertView.findViewById(R.id.item_user_search_username);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (user.getProfilePicTn() != null) {
            viewHolder.imageView.setImageBitmap(ImageUtil.createBitmap(user.getProfilePicTn()));
        } else {
            viewHolder.imageView.setImageDrawable(ImageUtil.getDefaultProfilePicTn(mContext));
        }
        viewHolder.textView.setText(user.getUsername());

        return convertView;
    }
}
