package com.thewyp.android.criminalintent.ui.crimelist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import com.thewyp.android.criminalintent.APP
import com.thewyp.android.criminalintent.R
import com.thewyp.android.criminalintent.model.Crime
import java.util.*

class CrimeListFragment : Fragment() {

    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }

    private var callbacks: Callbacks? = null

    private val viewModel by viewModels<CrimeListViewModel>()
    private lateinit var recyclerView: RecyclerView
    private var adapter: CrimeAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_crime_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.crime_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
//                Thread {
//                    viewModel.test()
//                }.start()
        adapter = CrimeAdapter()
        recyclerView.adapter = adapter

        viewModel.crimeListLiveData.observe(viewLifecycleOwner, {
            adapter?.submitList(it)
        })
    }

    private inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view) {

        val title: TextView = itemView.findViewById(R.id.crime_title)
        val date: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)

        fun bind(crime: Crime) {
            title.text = crime.title
            date.text = crime.date.toString()
            solvedImageView.visibility = if (crime.isSoled) {
                View.VISIBLE
            } else {
                View.GONE
            }
            itemView.setOnClickListener {
                callbacks?.onCrimeSelected(crime.id)
            }
        }
    }

    private inner class CrimeAdapter : ListAdapter<Crime, CrimeHolder>(

        AsyncDifferConfig.Builder(object : ItemCallback<Crime>() {
            override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
                return oldItem == newItem
            }
        })
            .setBackgroundThreadExecutor(APP.executors.diskIO())
            .build()
    ) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            return CrimeHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.list_item_crime, parent, false)
            )
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            holder.bind(currentList[position])
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    companion object {
        fun newInstance(): Fragment {
            return CrimeListFragment()
        }
    }


}