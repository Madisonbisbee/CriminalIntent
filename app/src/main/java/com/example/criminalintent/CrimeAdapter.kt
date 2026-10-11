package com.example.criminalintent

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class CrimeAdapter(
    private val crimes: List<Crime>,
    private val onCrimeClicked: (Crime) -> Unit
) : RecyclerView.Adapter<CrimeAdapter.CrimeViewHolder>() {

    class CrimeViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val titleTextView: TextView =
            view.findViewById(R.id.crimeTitle)

        val dateTextView: TextView =
            view.findViewById(R.id.crimeDate)

        val solvedTextView: TextView =
            view.findViewById(R.id.crimeSolved)

        val photoImageView: ImageView =
            view.findViewById(R.id.crimePhoto)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CrimeViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.list_item_crime,
                parent,
                false
            )

        return CrimeViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CrimeViewHolder,
        position: Int
    ) {

        val crime = crimes[position]

        holder.titleTextView.text =
            if (crime.title.isBlank()) {
                "Untitled Crime"
            } else {
                crime.title
            }

        val formatter =
            SimpleDateFormat(
                "MMM dd, yyyy",
                Locale.getDefault()
            )

        holder.dateTextView.text =
            formatter.format(crime.date)

        holder.solvedTextView.text =
            if (crime.isSolved) {
                "✓ Solved"
            } else {
                "Unsolved"
            }

        val photoName = crime.photoFileName

        if (photoName != null) {

            val photoFile = File(
                holder.itemView.context.filesDir,
                "crime_photos/$photoName"
            )

            if (photoFile.exists()) {

                val bitmap =
                    BitmapFactory.decodeFile(
                        photoFile.absolutePath
                    )

                holder.photoImageView.setImageBitmap(bitmap)

            } else {

                holder.photoImageView.setImageResource(
                    android.R.drawable.ic_menu_camera
                )
            }

        } else {

            holder.photoImageView.setImageResource(
                android.R.drawable.ic_menu_camera
            )
        }

        holder.itemView.setOnClickListener {
            onCrimeClicked(crime)
        }
    }

    override fun getItemCount(): Int {
        return crimes.size
    }
}