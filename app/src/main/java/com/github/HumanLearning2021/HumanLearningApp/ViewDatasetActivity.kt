package com.github.HumanLearning2021.HumanLearningApp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class ViewDatasetActivity : AppCompatActivity() {

    var datasetImageModelList = ArrayList<DatasetImageModel>();

    var names = arrayOf(
            "chat",
            "chat",
            "chien"
    )

    var images = intArrayOf(R.drawable.chat1, R.drawable.chat2, R.drawable.chien1);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_dataset)

        for(i in names.indices){
            datasetImageModelList.add(DatasetImageModel(names[i], images[i]))
        }

        var customAdapter = CustomAdapter(datasetImageModelList, this);

        findViewById<GridView>(R.id.gridView).adapter = customAdapter;

        findViewById<GridView>(R.id.gridView).setOnItemClickListener {adapterView, view, i, l ->
            var intent = Intent(this, ViewImageActivity::class.java)
            intent.putExtra("label", datasetImageModelList[i])
            startActivity(intent);
        };


    }

    class CustomAdapter(
            var imageModel: ArrayList<DatasetImageModel>,
            var context: Context
    ) : BaseAdapter(){

        var layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
            var view = view;
            if(view == null){
                view = layoutInflater.inflate(R.layout.row_items, viewGroup, false);
            }

            var tvImageName = view?.findViewById<TextView>(R.id.imageLabel);
            var imageView = view?.findViewById<ImageView>(R.id.imageView);

            tvImageName?.text = imageModel[position].label;
            imageView?.setImageResource(imageModel[position].image!!)

            return view!!;
        }

        override fun getItem(position: Int): Any {
            return imageModel[position];
        }

        override fun getItemId(position: Int): Long {
            return position.toLong();
        }

        override fun getCount(): Int {
            return imageModel.size;
        }

    }
}