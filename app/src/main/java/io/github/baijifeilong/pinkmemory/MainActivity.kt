package io.github.baijifeilong.pinkmemory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.verticalLayout
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), AnkoLogger {
    val list = ArrayList<String>()
    lateinit var recyclerView: RecyclerView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        verticalLayout {
            recyclerView {
                recyclerView = this
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                        return object : RecyclerView.ViewHolder(
                            LayoutInflater.from(this@MainActivity).inflate(
                                android.R.layout.simple_list_item_1,
                                parent,
                                false
                            )
                        ) {}
                    }

                    override fun getItemCount(): Int {
                        return list.size
                    }

                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                        holder.itemView.findViewById<TextView>(android.R.id.text1).text = list[position]
                    }
                }
            }
        }

        Timer().scheduleAtFixedRate(object : TimerTask() {
            var count = 0
            override fun run() {
                ++count
                runOnUiThread {
                    "OK $count".apply {
                        println(this)
                        info { this }
                        list.add(this)
                        recyclerView.adapter!!.notifyDataSetChanged()
                    }
                }
            }
        }, 0, 1000)
    }
}
