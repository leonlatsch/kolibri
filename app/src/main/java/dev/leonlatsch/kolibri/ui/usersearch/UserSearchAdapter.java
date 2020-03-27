package dev.leonlatsch.kolibri.ui.usersearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import dev.leonlatsch.kolibri.R;
import dev.leonlatsch.kolibri.rest.dto.UserDTO;
import dev.leonlatsch.kolibri.util.ImageUtil;

/**
 * This Adapter is used tot display the user search
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public class UserSearchAdapter extends ArrayAdapter<UserDTO> {

    private List<UserDTO> dataset;
    private Context mContext;

    public UserSearchAdapter(Context context, List<UserDTO> users) {
        super(context, 0, users);
        this.dataset = users;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserDTO user = getItem(position);
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

    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
