package nicolasfrossard.whattheloop

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*

class MainActivity : AppCompatActivity() {

    private lateinit var toggleButton: ToggleButton
    private lateinit var drumSpinner: Spinner
    private lateinit var guitarSpinner: Spinner

    private val loopPlayer = LoopPlayer()

    private lateinit var selectedDrumResource: SelectableResource
    private lateinit var selectedGuitarResource: SelectableResource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toggleButton = findViewById(R.id.playButton)

        drumSpinner = findViewById(R.id.drumselector)
        initializeFileSelector(drumSpinner, "drum") { selectedDrumResource = it }

        guitarSpinner = findViewById(R.id.guitarselector)
        initializeFileSelector(guitarSpinner, "guitar") { selectedGuitarResource = it }

        initializePlayButton()
    }

    private fun initializePlayButton() {
        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                guitarSpinner.isEnabled = true
                drumSpinner.isEnabled = true
                loopPlayer.pause()
            } else {
                guitarSpinner.isEnabled = false
                drumSpinner.isEnabled = false
                loopPlayer.play(this, selectedGuitarResource.ref, selectedDrumResource.ref)
            }
        }
    }

    private fun initializeFileSelector(spinner: Spinner, filePrefix: String, function: (SelectableResource) -> Unit) {
        val rawFiles = R.raw::class.java.fields

        val fileList = ArrayList<SelectableResource>()
        rawFiles.forEach { rawFile ->
            if(rawFile.name.startsWith(filePrefix)) {
                fileList.add(
                    SelectableResource(
                        rawFile.name,
                        resources.getIdentifier(rawFile.name, "raw", this.getPackageName())
                    )
                )
            }
        }
        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fileList)

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                function.invoke(fileList[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }
}
