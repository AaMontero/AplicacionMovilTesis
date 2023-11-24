package com.example.aplicaciontesis

import MyAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplicaciontesis.dataClass.Vivienda

class ActividadRecycler : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recicler_view)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val dataList = mutableListOf(
            Vivienda("Texto 1", R.drawable.ic_launcher_foreground),
            Vivienda("Texto 2", R.drawable.ic_launcher_foreground),
            Vivienda("Texto 3", R.drawable.ic_launcher_foreground),
        )
        val adapter = MyAdapter(dataList)
        recyclerView.adapter = adapter
    }
}