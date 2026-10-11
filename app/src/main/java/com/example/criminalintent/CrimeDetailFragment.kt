package com.example.criminalintent

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class CrimeDetailFragment : Fragment() {

    private var crime: Crime? = null

    private lateinit var titleEditText: EditText
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var dateButton: Button
    private lateinit var photoButton: Button
    private lateinit var photoImageView: ImageView
    private lateinit var shareButton: Button
    private lateinit var deleteButton: Button

    private var pendingPhotoFile: File? = null
    private var pendingPhotoName: String? = null

    private val takePictureLauncher =
        registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->

            if (success) {

                val currentCrime = crime
                val photoFile = pendingPhotoFile
                val photoName = pendingPhotoName

                if (
                    currentCrime != null &&
                    photoFile != null &&
                    photoName != null
                ) {

                    currentCrime.photoFileName = photoName

                    loadPhoto(photoFile)

                    viewLifecycleOwner.lifecycleScope.launch {
                        CrimeRepository.get()
                            .updateCrime(currentCrime)
                    }
                }

            } else {
                pendingPhotoFile?.delete()
            }

            pendingPhotoFile = null
            pendingPhotoName = null
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_crime_detail,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        titleEditText =
            view.findViewById(R.id.crimeTitleEditText)

        solvedCheckBox =
            view.findViewById(R.id.crimeSolvedCheckBox)

        dateButton =
            view.findViewById(R.id.crimeDateButton)

        photoButton =
            view.findViewById(R.id.takePhotoButton)

        photoImageView =
            view.findViewById(R.id.crimePhotoImageView)

        shareButton =
            view.findViewById(R.id.shareCrimeButton)

        deleteButton =
            view.findViewById(R.id.deleteCrimeButton)

        val crimeId =
            arguments?.getString(ARG_CRIME_ID)

        if (crimeId != null) {

            viewLifecycleOwner.lifecycleScope.launch {

                crime =
                    CrimeRepository.get()
                        .getCrime(
                            UUID.fromString(crimeId)
                        )

                crime?.let { currentCrime ->

                    titleEditText.setText(
                        currentCrime.title
                    )

                    solvedCheckBox.isChecked =
                        currentCrime.isSolved

                    updateDateButton()

                    showSavedPhoto()
                }
            }
        }

        dateButton.setOnClickListener {
            showDatePicker()
        }

        photoButton.setOnClickListener {
            takePhoto()
        }

        shareButton.setOnClickListener {
            shareCrimeReport()
        }

        deleteButton.setOnClickListener {

            crime?.let { currentCrime ->

                currentCrime.photoFileName?.let { photoName ->

                    val file =
                        getPhotoFile(photoName)

                    if (file.exists()) {
                        file.delete()
                    }
                }

                viewLifecycleOwner.lifecycleScope.launch {

                    CrimeRepository.get()
                        .deleteCrime(currentCrime)

                    parentFragmentManager
                        .popBackStack()
                }
            }
        }
    }

    override fun onPause() {

        super.onPause()

        crime?.let { currentCrime ->

            currentCrime.title =
                titleEditText.text.toString()

            currentCrime.isSolved =
                solvedCheckBox.isChecked

            viewLifecycleOwner.lifecycleScope.launch {
                CrimeRepository.get()
                    .updateCrime(currentCrime)
            }
        }
    }

    private fun updateDateButton() {

        crime?.let { currentCrime ->

            val formatter =
                SimpleDateFormat(
                    "MMM dd, yyyy",
                    Locale.getDefault()
                )

            dateButton.text =
                formatter.format(
                    currentCrime.date
                )
        }
    }

    private fun showDatePicker() {

        val currentCrime =
            crime ?: return

        val calendar =
            Calendar.getInstance()

        calendar.time =
            currentCrime.date

        val dialog =
            DatePickerDialog(
                requireContext(),

                { _, year, month, day ->

                    calendar.set(
                        year,
                        month,
                        day
                    )

                    currentCrime.date =
                        calendar.time

                    updateDateButton()

                    viewLifecycleOwner.lifecycleScope.launch {
                        CrimeRepository.get()
                            .updateCrime(currentCrime)
                    }
                },

                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

        dialog.show()
    }

    private fun takePhoto() {

        val currentCrime =
            crime ?: return

        val photoDirectory =
            File(
                requireContext().filesDir,
                "crime_photos"
            )

        if (!photoDirectory.exists()) {
            photoDirectory.mkdirs()
        }

        val photoName =
            "IMG_${currentCrime.id}.jpg"

        val photoFile =
            File(
                photoDirectory,
                photoName
            )

        pendingPhotoFile =
            photoFile

        pendingPhotoName =
            photoName

        val photoUri: Uri =
            FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                photoFile
            )

        takePictureLauncher.launch(
            photoUri
        )
    }

    private fun showSavedPhoto() {

        val photoName =
            crime?.photoFileName

        if (photoName != null) {

            val photoFile =
                getPhotoFile(photoName)

            if (photoFile.exists()) {
                loadPhoto(photoFile)
            } else {
                photoImageView.setImageResource(
                    android.R.drawable.ic_menu_camera
                )
            }

        } else {

            photoImageView.setImageResource(
                android.R.drawable.ic_menu_camera
            )
        }
    }

    private fun loadPhoto(
        file: File
    ) {

        val bitmap =
            BitmapFactory.decodeFile(
                file.absolutePath
            )

        photoImageView
            .setImageBitmap(bitmap)
    }

    private fun getPhotoFile(
        photoName: String
    ): File {

        return File(
            requireContext().filesDir,
            "crime_photos/$photoName"
        )
    }

    private fun shareCrimeReport() {

        val currentCrime =
            crime ?: return

        currentCrime.title =
            titleEditText.text.toString()

        currentCrime.isSolved =
            solvedCheckBox.isChecked

        val report = """
            Crime Report

            Title: ${currentCrime.title}
            Date: ${dateButton.text}
            Status: ${if (currentCrime.isSolved) "Solved" else "Unsolved"}
        """.trimIndent()

        val intent =
            Intent(
                Intent.ACTION_SEND
            ).apply {

                type =
                    "text/plain"

                putExtra(
                    Intent.EXTRA_TEXT,
                    report
                )
            }

        startActivity(
            Intent.createChooser(
                intent,
                "Share Crime Report"
            )
        )
    }

    companion object {

        private const val ARG_CRIME_ID =
            "crime_id"

        fun newInstance(
            crimeId: String
        ): CrimeDetailFragment {

            return CrimeDetailFragment()
                .apply {

                    arguments =
                        Bundle().apply {

                            putString(
                                ARG_CRIME_ID,
                                crimeId
                            )
                        }
                }
        }
    }
}