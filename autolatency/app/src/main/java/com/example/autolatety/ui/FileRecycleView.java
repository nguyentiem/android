package com.example.autolatety.ui;

import android.graphics.Typeface;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autolatety.R;
import com.example.autolatety.data.Result;
import com.example.autolatety.listener.ItemClickListener;

import java.util.LinkedList;
import java.util.List;


public class FileRecycleView extends RecyclerView.Adapter<FileRecycleView.ItemViewHolder> {

    private ItemClickListener listener;
    private List<Result> list;
    private int numTest =0;
    private int err =-1;
    public FileRecycleView(ItemClickListener itemclick, int n) {
        listener = itemclick;
        list = new LinkedList<>();
        numTest =n;

    }

    public void addItem(Result result) {
        err=0;
        list.add(result);
        notifyItemChanged(list.size() - 1);

    }

    public void updateLastItem() {
        if (list.size() == 0) return;
        notifyItemChanged(list.size() - 1);
    }

    public void setData(List<Result> tlist) {
        Log.d("getdb", "setData: ");
        list = tlist;
//        if (list.size() > 0) {
//            Result re = list.get(0);
//            Log.d("getdb", "" + re.getHi().command + " " + re.getCommand().command + " " + re.getBixbyRespone().command);
//        }
        notifyDataSetChanged();

    }


    @Override
    public FileRecycleView.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_item, parent, false);
        return new ItemViewHolder(view);

    }

    private void setSpanColor(TextView view, String fulltext, String subtext) {
        int i = fulltext.indexOf(subtext);
        Spannable spannable = new SpannableString(fulltext);
        //spannable.setSpan(new ForegroundColorSpan(color), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(spannable);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Result result = list.get(position);
      //  int size = list.size();
        String subString = null;
        String fullText = null;

        if(result.acc){
                holder.recog.setImageResource(R.drawable.icon_pass);
                holder.lhi.setVisibility(View.VISIBLE);
                subString = "\"" + result.getHi().command + "\"";
                fullText = "\"" + result.getHi().command + "\": " + result.getHi().endStr;
                setSpanColor(holder.hi, fullText, subString);

                holder.lcmd.setVisibility(View.VISIBLE);
                subString = "\"" + result.getCommand().command + "\"";
                fullText = "\"" + result.getCommand().command + "\": " + result.getCommand().endStr;
                setSpanColor(holder.cmd, fullText, subString);

                holder.lres.setVisibility(View.VISIBLE);
                subString = "\"" + result.getBixbyRespone().command + "\"";
                fullText = "\"" + result.getBixbyRespone().command + "\": " + result.getBixbyRespone().endStr;
                setSpanColor(holder.res, fullText, subString);

                holder.lting.setVisibility(View.VISIBLE);
                subString = "\"" + result.getTing().command + "\"";
                fullText = "\"" + result.getTing().command + "\": " + result.getTing().endStr;
                setSpanColor(holder.ting, fullText, subString);

                holder.llatency.setVisibility(View.VISIBLE);
                subString = "Latency: ";
                fullText = "Latency: "+(result.getLatety())+"s";
                setSpanColor(holder.tlatency, fullText, subString);
               err = 5;
        }else{

            if(result.getBixbyRespone().finish>0){

            holder.lres.setVisibility(View.VISIBLE);
            subString = "\"" + result.getBixbyRespone().command + "\"";
            fullText = "\"" + result.getBixbyRespone().command + "\": " + result.getBixbyRespone().endStr;
            setSpanColor(holder.res, fullText, subString);
            }else{
                holder.lres.setVisibility(View.GONE);
                err = 4;
            }

            if(result.getCommand().finish>0) {
                holder.lcmd.setVisibility(View.VISIBLE);
                subString = "\"" + result.getCommand().command + "\"";
                fullText = "\"" + result.getCommand().command + "\": " + result.getCommand().endStr;
                setSpanColor(holder.cmd, fullText, subString);
            }else{
                holder.lcmd.setVisibility(View.GONE);
                err = 3;
            }

            if(result.getTing().finish>0) {
                holder.lting.setVisibility(View.VISIBLE);
                subString = "\"" + result.getTing().command + "\"";
                fullText = "\"" + result.getTing().command + "\": " + result.getTing().endStr;
                setSpanColor(holder.ting, fullText, subString);
                holder.lhlatency.setVisibility(View.VISIBLE);
                subString = "Hi Latency: ";
                fullText = "Hi Latency: "+((result.getTing().begin-result.getHi().finish)/1000.0)+"s";
                setSpanColor(holder.thlatency, fullText, subString);
            }else{
                holder.lting.setVisibility(View.GONE);
                holder.lhlatency.setVisibility(View.GONE);
                err = 2;
            }
        }

        if(result.getHi().finish>0) {
            holder.lhi.setVisibility(View.VISIBLE);
            subString = "\"" + result.getHi().command + "\"";
            fullText = "\"" + result.getHi().command + "\": " + result.getHi().endStr;
            setSpanColor(holder.hi, fullText, subString);

        }else{
            holder.lhi.setVisibility(View.GONE);
            err = 1;
        }

        if(result.getCommand().finish>0){
            holder.title.setText("Test: " + (position + 1) + "/" + numTest + " " + result.getCommand().command);
        }else{
            holder.title.setText("Test: " + (position + 1));
        }
        if(result.isDone){

            if(result.acc){
                holder.lerr.setVisibility(View.GONE);
                holder.recog.setImageResource(R.drawable.icon_pass);
                holder.llatency.setVisibility(View.VISIBLE);
                err =5;
            }else{

                if(err==1){
                    holder.textErr.setText("Can't play Wake up");
                }else{
                    if(err==2){
                        holder.textErr.setText("Can't recognize Wakeup Response");
                    }else{
                        if(err==3){
                            holder.textErr.setText("Can't play Utterance");
                        }else{
                            if(err==4){
                                holder.textErr.setText("Can't recognize Bixby response");
                            }
                        }
                    }
                }
                err=0;
                holder.llatency.setVisibility(View.GONE);
                holder.recog.setImageResource(R.drawable.icon_failed);
                holder.lerr.setVisibility(View.VISIBLE);
            }
        }else{
            holder.llatency.setVisibility(View.GONE);
            holder.recog.setImageResource(R.drawable.icon_play);
            holder.lerr.setVisibility(View.GONE);
        }

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onLongClick(result);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView hi;
        public TextView cmd;
        public TextView res;
        public TextView ting;
        public TextView latety;
        public LinearLayout lhi;
        public LinearLayout lting;
        public LinearLayout lcmd;
        public LinearLayout lres;
        public LinearLayout lerr;
        public LinearLayout llatency;
        public TextView tlatency;
        public ImageView recog;
        public LinearLayout lhlatency;
        public TextView thlatency;
        public TextView textErr;
        View view;

        public ItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            title = itemView.findViewById(R.id.title);
            hi = (TextView) itemView.findViewById(R.id.hitext);
            cmd = (TextView) itemView.findViewById(R.id.cmdtext);
            res = (TextView) itemView.findViewById(R.id.restext);
            //  latety = (TextView) itemView.findViewById(R.id.latety);
            ting = (TextView) itemView.findViewById(R.id.ting);

            lhi = itemView.findViewById(R.id.imghi);
            lting = itemView.findViewById(R.id.imgting);
            lcmd = itemView.findViewById(R.id.imgcmd);
            lres = itemView.findViewById(R.id.imgres);
            lerr = itemView.findViewById(R.id.imgerr);
            recog = itemView.findViewById(R.id.up);

            llatency = itemView.findViewById(R.id.imglate);
            tlatency = itemView.findViewById(R.id.latetext);

            lhlatency = itemView.findViewById(R.id.imglatehi);
            thlatency = itemView.findViewById(R.id.latehitext);

            textErr =itemView.findViewById(R.id.texterror);
        }
    }

}



