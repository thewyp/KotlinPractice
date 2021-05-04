package com.thewyp.android.nerdlauncher

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.DiffUtil.ItemCallback

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var activities: List<ResolveInfo>
    private lateinit var recyclerView: RecyclerView
    private lateinit var activityAdapter: ActivityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view)

        setupAdapter()

        activityAdapter = ActivityAdapter()

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = activityAdapter
        }
        activityAdapter.submitList(activities)

    }

    private fun setupAdapter() {
        val setupIntent = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
        activities = packageManager.queryIntentActivities(setupIntent, 0)

        activities.sortedWith { o1, o2 ->
            String.CASE_INSENSITIVE_ORDER.compare(
                o1.loadLabel(packageManager).toString(),
                o2.loadLabel(packageManager).toString()
            )
        }

        Log.i(TAG, "Found ${activities.size} activities")
    }

    inner class ActivityHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private var name: TextView = itemView.findViewById(R.id.name)
        private var icon: ImageView = itemView.findViewById(R.id.icon)
        private lateinit var resolveInfo: ResolveInfo

        init {
            name.setOnClickListener(this)
        }

        fun bind(resolveInfo: ResolveInfo) {
            this.resolveInfo = resolveInfo
            name.text = resolveInfo.loadLabel(itemView.context.packageManager)
            icon.setImageDrawable(resolveInfo.loadIcon(itemView.context.packageManager))
        }

        override fun onClick(view: View) {
            val activityInfo = resolveInfo.activityInfo
            val intent =
                Intent().apply {
                    setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            view.context.startActivity(intent)

        }
    }

    inner class ActivityAdapter : ListAdapter<ResolveInfo, ActivityHolder>(
        object : ItemCallback<ResolveInfo>() {
            override fun areItemsTheSame(oldItem: ResolveInfo, newItem: ResolveInfo): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ResolveInfo, newItem: ResolveInfo): Boolean {
                return oldItem.resolvePackageName == newItem.resolvePackageName
            }

        }
    ) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityHolder {
            return ActivityHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.activity_list_item, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ActivityHolder, position: Int) {
            holder.bind(currentList[position])
        }

    }
}