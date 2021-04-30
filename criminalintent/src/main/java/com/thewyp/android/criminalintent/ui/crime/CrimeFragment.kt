package com.thewyp.android.criminalintent.ui.crime

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.*
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.thewyp.android.criminalintent.R
import com.thewyp.android.criminalintent.model.Crime
import com.thewyp.android.criminalintent.utils.PictureUtil
import java.io.File
import java.util.*

private const val ARG_CRIME_ID = "crime_id"
private const val DIALOG_DATE = "dialog_date"
private const val REQUEST_DATE = 0
private const val REQUEST_SUSPECT = 1
private const val REQUEST_PHOTO = 2
private const val DATE_FORMAT = "EEE, MMM, dd"

class CrimeFragment : Fragment(), DatePickerFragment.Callbacks {

    private lateinit var crime: Crime

    private lateinit var titleFiled: EditText
    private lateinit var dateButton: Button
    private lateinit var suspectButton: Button
    private lateinit var reportButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var photoImageView: ImageView

    private val viewModel by viewModels<CrimeDetailViewModel>()

    private lateinit var photoFile: File
    private lateinit var photoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        crime = Crime()
        val crimeId = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        viewModel.loadCrime(crimeId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_crime, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        titleFiled = view.findViewById(R.id.crime_title)
        dateButton = view.findViewById(R.id.crime_date)
        solvedCheckBox = view.findViewById(R.id.crime_solved)
        suspectButton = view.findViewById(R.id.crime_suspect)
        reportButton = view.findViewById(R.id.crime_report)
        photoImageView = view.findViewById(R.id.crime_photo)
        viewModel.crimeLiveData.observe(viewLifecycleOwner, {
            it?.let {
                crime = it
                photoFile = viewModel.getPhotoFile(it)
                photoUri = FileProvider.getUriForFile(
                    requireActivity(),
                    "com.thewyp.android.criminalintent.fileprovider",
                    photoFile
                )
                updateUI(it)
            }
        })

        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                show(this@CrimeFragment.parentFragmentManager, DIALOG_DATE)
            }
        }

        suspectButton.apply {
            val pickContactIntent =
                Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

            setOnClickListener {
                startActivityForResult(pickContactIntent, REQUEST_SUSPECT)
            }

            val resolvedActivity: ResolveInfo? = requireActivity().packageManager.resolveActivity(
                pickContactIntent, PackageManager.MATCH_DEFAULT_ONLY
            )
            if (resolvedActivity == null) {
                isEnabled = false
            }
        }

        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
            }.also {
                startActivity(Intent.createChooser(it, getString(R.string.send_report)))
            }
        }

    }

    override fun onStop() {
        super.onStop()
        if (!TextUtils.isEmpty(crime.title)) {
            viewModel.saveCrime(crime)
        }
    }

    private fun updateUI(crime: Crime) {
        this.crime = crime
        titleFiled.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckBox.isChecked = crime.isSoled
        if (crime.suspect.isNotEmpty()) {
            suspectButton.text = crime.suspect
        } else {
            suspectButton.text = getString(R.string.crime_suspect_text)
        }
        updatePhotoView()
    }

    private fun updatePhotoView() {
        if (photoFile.exists()) {
            photoImageView.setImageBitmap(PictureUtil.getScaleBitmap(photoFile.path,
                photoImageView.width,
                photoImageView.height
            ))
        } else {
            photoImageView.setImageDrawable(null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            resultCode != Activity.RESULT_OK -> return

            requestCode == REQUEST_SUSPECT && data != null -> {
                val contactUri: Uri = data.data ?: return
                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                val cursor = requireActivity().contentResolver
                    .query(contactUri, queryFields, null, null, null)
                cursor?.use {
                    if (it.count == 0) {
                        return
                    }
                    it.moveToFirst()
                    val suspect = it.getString(0)
                    crime.suspect = suspect
                    viewModel.saveCrime(crime)
                    suspectButton.text = suspect
                }
            }

            requestCode == REQUEST_PHOTO -> {
                updatePhotoView()
            }
        }
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSoled) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()
        val suspect = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(R.string.crime_report,
            crime.title, dateString, solvedString, suspect)
    }

    override fun onStart() {
        super.onStart()
        titleFiled.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    crime.title = s.toString()
                }

                override fun afterTextChanged(s: Editable?) {
                }

            }
        )
        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSoled = isChecked
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_crime, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.crime_camera -> {
                val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val packageManager = requireActivity().packageManager
                val resolvedActivity: ResolveInfo? =
                    packageManager.resolveActivity(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
                if (resolvedActivity == null) {
                    setHasOptionsMenu(false)
                    return true
                }
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                val cameraActivities: List<ResolveInfo> =
                    packageManager.queryIntentActivities(captureImage,
                        PackageManager.MATCH_DEFAULT_ONLY)
                for (cameraActivity in cameraActivities) {
                    requireActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName,
                        photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }
                startActivityForResult(captureImage, REQUEST_PHOTO)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    companion object {
        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }

    override fun onDateSelected(date: Date) {
        crime.date = date
        updateUI(crime)
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().revokeUriPermission(
            photoUri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
    }
}