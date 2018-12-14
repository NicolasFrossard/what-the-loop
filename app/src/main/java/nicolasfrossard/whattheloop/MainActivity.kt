package nicolasfrossard.whattheloop

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*

class MainActivity : AppCompatActivity() {

    lateinit var toggleButton: ToggleButton
    lateinit var spinner: Spinner

    val loopPlayer = LoopPlayer()

    lateinit var selectedResource: SelectableResource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toggleButton = findViewById(R.id.playButton)
        spinner = findViewById(R.id.fileselector)

        initializeFileSelector()
        initializePlayButton()
    }

    private fun initializePlayButton() {
        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                spinner.isEnabled = true
                loopPlayer.pause()
            } else {
                spinner.isEnabled = false
                loopPlayer.play(this, selectedResource.ref)
            }
        }
    }

    private fun initializeFileSelector() {
        val spinner: Spinner = findViewById(R.id.fileselector)

        val rawFiles = R.raw::class.java.fields


        val fileList = ArrayList<SelectableResource>()
        rawFiles.forEach { rawFile ->
            fileList.add(
                SelectableResource(
                    rawFile.name,
                    resources.getIdentifier(rawFile.name, "raw", this.getPackageName())
                )
            )
        }
        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fileList)

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedResource = fileList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }
}
