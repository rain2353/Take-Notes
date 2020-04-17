package com.example.takenotes.Picture.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.takenotes.Api.INodeJS
import com.example.takenotes.Api.IRecyclerOnClick
import com.example.takenotes.Api.RetrofitClient
import com.example.takenotes.Api.ipaddress
import com.example.takenotes.Common.Common
import com.example.takenotes.Controller.ItemTouchHelperListener
import com.example.takenotes.Model.Image
import com.example.takenotes.Picture.SeePictureActivity.SeePictureActivity
import com.example.takenotes.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class PictureRecyclerAdapter(internal val context: Context, internal val image: List<Image>) :
    RecyclerView.Adapter<PictureRecyclerAdapter.Holder>(){
    lateinit var myAPI: INodeJS
    var compositeDisposable = CompositeDisposable()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PictureRecyclerAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.picture_recycler_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return image.size
    }

    override fun onBindViewHolder(holder: PictureRecyclerAdapter.Holder, position: Int) {
        if (image[position].title != null){
            holder.PictureTitleView.text = image[position].title
        }else{
            holder.PictureTitleView.visibility = View.GONE
        }
        Glide.with(context).load(ipaddress.ip+image[position].file).into(holder.PictureImageView)
        if (image[position].file4 != "empty"){
            Glide.with(context).load(ipaddress.ip+image[position].file1).override(200,200).into(holder.PictureImageView1)
            Glide.with(context).load(ipaddress.ip+image[position].file2).override(200,200).into(holder.PictureImageView2)
            Glide.with(context).load(ipaddress.ip+image[position].file3).override(200,200).into(holder.PictureImageView3)
            Glide.with(context).load(ipaddress.ip+image[position].file4).override(200,200).into(holder.PictureImageView4)
        }else{
            holder.ImagesLinear.visibility = View.GONE
        }
        holder.PictureUpdateView.text = image[position].updated_at

        holder.setClick(object : IRecyclerOnClick {
            override fun onLongClick(view: View, Position: Int) {

            }

            override fun onClick(view: View, position: Int) {
                Common.SelectPicture = image[position]
//                Toast.makeText(view.context,Memo[position].title + "클릭"+"("+position+")",Toast.LENGTH_SHORT).show()
                val intent = Intent(view.context, SeePictureActivity::class.java)
                view.context.startActivity(intent)

            }

        })
    }
    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        internal var PictureTitleView: TextView
        internal var PictureImageView: ImageView
        internal var PictureImageView1: ImageView
        internal var PictureImageView2: ImageView
        internal var PictureImageView3: ImageView
        internal var PictureImageView4: ImageView
        internal var PictureUpdateView: TextView
        internal var ImagesLinear: LinearLayout
        internal lateinit var iRecyclerOnClick: IRecyclerOnClick

        override fun onClick(v: View?) {
            iRecyclerOnClick.onClick(v!!, adapterPosition)

        }

        fun setClick(iRecyclerOnClick: IRecyclerOnClick) {
            this.iRecyclerOnClick = iRecyclerOnClick
        }

        init {
            PictureTitleView = itemView.findViewById(R.id.PictureTitleView) as TextView
            PictureImageView = itemView.findViewById(R.id.file) as ImageView
            PictureImageView1 = itemView.findViewById(R.id.file1) as ImageView
            PictureImageView2 = itemView.findViewById(R.id.file2) as ImageView
            PictureImageView3 = itemView.findViewById(R.id.file3) as ImageView
            PictureImageView4 = itemView.findViewById(R.id.file4) as ImageView
            PictureUpdateView = itemView.findViewById(R.id.PictureUpdateView) as TextView
            ImagesLinear = itemView.findViewById(R.id.ImagesLinear) as LinearLayout
            itemView.setOnClickListener(this)
        }

    }

}