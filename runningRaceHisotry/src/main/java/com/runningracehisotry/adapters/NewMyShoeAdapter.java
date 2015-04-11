package com.runningracehisotry.adapters;

import com.runningracehisotry.models.Shoe;
/**
 * create by NTQ
 */
public class MyShoesAdapter extends BaseAdapter {

    private Context mContext;
    private List<Shoe> lstShoes;
    //constructor function
    public ContactAdapter(Context context, List<Shoe> list) {
        this.mContext = context;
        this.lstShoes = new ArrayList<Shoe>(list);
    }

    //get total object
    @Override
    public int getCount() {
        if(lstShoes != null){
            return this.lstShoes.size();
        }
        else{
            return 0;
        }
    }
    //get item base-on position
    @Override
    public Object getItem(int position) {
        return null;
    }
    // get Item Id
    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ContactItemViewHolder {
        private ImageView shoeAvatar, imgUnRead;//imgStatus,
        private TextView contactUsername;//, txtStatus;//, txtNumber;
        //private int positionItem;

    }

}