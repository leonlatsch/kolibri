package dev.leonlatsch.kolibri.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView

import dev.leonlatsch.kolibri.R
import dev.leonlatsch.kolibri.rest.dto.UserDTO
import dev.leonlatsch.kolibri.util.ImageUtil

/**
 * This Adapter is used tot display the user search
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
class UserAdapter(private val mContext: Context, private val dataset: MutableList<UserDTO>) : ArrayAdapter<UserDTO>(mContext, 0, dataset) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val user = getItem(position)
        val viewHolder: ViewHolder

        if (view == null) {
            viewHolder = ViewHolder()
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false)
            viewHolder.imageView = view!!.findViewById<CardView>(R.id.item_user_search_card_view).findViewById(R.id.item_user_search_image_view)
            viewHolder.textView = view.findViewById(R.id.item_user_search_username)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        if (user?.profilePicTn != null) {
            viewHolder.imageView!!.setImageBitmap(ImageUtil.createBitmap(user.profilePicTn))
        } else {
            viewHolder.imageView!!.setImageDrawable(ImageUtil.getDefaultProfilePicTn(mContext))
        }
        viewHolder.textView!!.text = user?.username

        return view
    }

    private class ViewHolder {
        internal var imageView: ImageView? = null
        internal var textView: TextView? = null
    }
}
