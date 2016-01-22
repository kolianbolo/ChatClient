package ru.bolobanov.chat_client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Bolobanov Nikolay on 11.12.15.
 */
public class UsersAdapter extends BaseAdapter{

    private final List<String> mList;
    private final Context mContext;

    public UsersAdapter(List<String> pList, Context pContext){
        mList = pList;
        mContext = pContext;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflater.inflate(R.layout.i_user, parent, false);
            ViewHolder viewHolder = new ViewHolder((TextView) convertView.findViewById(R.id.titleText));
            convertView.setTag(viewHolder);
        }

        String userName = mList.get(position);
        ViewHolder holder = (ViewHolder)convertView.getTag();
        holder.mTitleText.setText(userName);
        return convertView;
    }

    class ViewHolder{
        public final TextView mTitleText;
        public ViewHolder(TextView pTitleText){
            mTitleText = pTitleText;

        }
    }
}
