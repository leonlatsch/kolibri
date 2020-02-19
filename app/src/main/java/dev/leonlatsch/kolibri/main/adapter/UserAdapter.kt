package dev.leonlatsch.kolibri.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

import dev.leonlatsch.kolibri.R
import dev.leonlatsch.kolibri.rest.dto.UserDTO
import dev.leonlatsch.kolibri.util.ImageUtil

/**
 * This Adapter is used tot display the user search
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
class UserAdapter(private val mContext: Context, private val dataset: List<UserDTO>) : ArrayAdapter<UserDTO>(mContext, 0, dataset) {

    @Override
    fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val user = getItem(position)
        val viewHolder: ViewHolder

        if (convertView == null) {
            viewHolder = ViewHolder()
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false)
            viewHolder.imageView = convertView!!.findViewById(R.id.item_user_search_card_view).findViewById(R.id.item_user_search_image_view)
            viewHolder.textView = convertView!!.findViewById(R.id.item_user_search_username)
            convertView!!.setTag(viewHolder)
        } else {
            viewHolder = convertView!!.getTag()
        }

        if (user.getProfilePicTn() != null) {
            viewHolder.imageView!!.setImageBitmap(ImageUtil.createBitmap(user.getProfilePicTn()))
        } else {
            viewHolder.imageView!!.setImageDrawable(ImageUtil.getDefaultProfilePicTn(mContext))
        }
        viewHolder.textView!!.setText(user.getUsername())

        return convertView
    }

    private class ViewHolder {
        internal var imageView: ImageView? = null
        internal var textView: TextView? = null
    }
}
