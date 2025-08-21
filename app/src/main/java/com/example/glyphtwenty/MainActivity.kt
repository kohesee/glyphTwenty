
import android.content.ComponentName
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.glyphtwenty.ui.theme.GlyphTwentyTheme

import com.nothing.ketchum.Common
import com.nothing.ketchum.Glyph
import com.nothing.ketchum.GlyphException
import com.nothing.ketchum.GlyphFrame
import com.nothing.ketchum.GlyphManager

enum class NothingPhoneModel(val modelName: String) {
    NOTHING_PHONE_1("Nothing Phone (1)"),
    NOTHING_PHONE_2("Nothing Phone (2)"),
    NOTHING_PHONE_2A("Nothing Phone (2a)"),
    NOTHING_PHONE_2A_PLUS("Nothing Phone (2a) Plus"),
    NOTHING_PHONE_3("Nothing Phone (3)")
}

class NothingPhoneAnimation(private val model: NothingPhoneModel, private val glyphManager: GlyphManager?) {
    init {
        Log.d("NothingPhoneAnimation", "Initialized animation handler for ${model.modelName}")
    }

    fun toggleChannelA() {
        glyphManager?.let { gm ->
            try {
                val builder = gm.glyphFrameBuilder ?: GlyphFrame.Builder()
                val frame = builder
                    .buildChannelA()
                    .buildPeriod(1000)
                    .buildCycles(1)
                    .build()
                gm.toggle(frame)
                Log.d("NothingPhoneAnimation", "Toggling Channel A for ${model.modelName}")
            } catch (e: GlyphException) {
                Log.e("NothingPhoneAnimation", "Error toggling Channel A: ${e.message}")
            }
        } ?: Log.w("NothingPhoneAnimation", "GlyphManager is null, cannot toggle Channel A.")
    }

    fun animateChannelB() {
        glyphManager?.let { gm ->
            try {
                val builder = gm.glyphFrameBuilder ?: GlyphFrame.Builder()
                val frame = builder
                    .buildChannelB()
                    .buildPeriod(1500)
                    .buildInterval(500)
                    .buildCycles(3)
                    .build()
                gm.animate(frame)
                Log.d("NothingPhoneAnimation", "Animating Channel B for ${model.modelName}")
            } catch (e: GlyphException) {
                Log.e("NothingPhoneAnimation", "Error animating Channel B: ${e.message}")
            }
        } ?: Log.w("NothingPhoneAnimation", "GlyphManager is null, cannot animate Channel B.")
    }


    fun displayProgressOnChannel(progress: Int) {
        glyphManager?.let { gm ->
            try {
                val builder = gm.glyphFrameBuilder ?: GlyphFrame.Builder()
                val frame: GlyphFrame

                frame = when (model) {
                    NothingPhoneModel.NOTHING_PHONE_1 -> builder.buildChannel(Glyph.Code_20111.D1_1).build()
                    NothingPhoneModel.NOTHING_PHONE_2 -> builder.buildChannel(Glyph.Code_22111.C1_1).build() // Using C1 for Phone 2
                    NothingPhoneModel.NOTHING_PHONE_2A, NothingPhoneModel.NOTHING_PHONE_2A_PLUS -> builder.buildChannel(
                        Glyph.Code_23111.C_1).build()
                    NothingPhoneModel.NOTHING_PHONE_3 -> builder.buildChannel(Glyph.Code_24111.A_1).build() // Using A_1 for Phone 3
                }

                gm.displayProgress(frame, progress)
                Log.d("NothingPhoneAnimation", "Displaying progress $progress on ${model.modelName}")
            } catch (e: GlyphException) {
                Log.e("NothingPhoneAnimation", "Error displaying progress: ${e.message}")
            }
        } ?: Log.w("NothingPhoneAnimation", "GlyphManager is null, cannot display progress.")
    }


    fun turnOffGlyphs() {
        glyphManager?.let { gm ->
            try {
                gm.turnOff()
                Log.d("NothingPhoneAnimation", "Turning off glyphs for ${model.modelName}")
            } catch (e: GlyphException) {
                Log.e("NothingPhoneAnimation", "Error turning off glyphs: ${e.message}")
            }
        } ?: Log.w("NothingPhoneAnimation", "GlyphManager is null, cannot turn off glyphs.")
    }
}

class MainActivity : ComponentActivity() {

    private val TAG = "GlyphDemo"
    private val appName = "GlyphDemo"

    private var mGM: GlyphManager? = null
    private var mCallback: GlyphManager.Callback? = null

    private var statusText by mutableStateOf("Status: Initializing...")
    private var isButtonEnabled by mutableStateOf(false)
    private var progressSliderValue by mutableStateOf(0f)

    private var phoneModel: NothingPhoneModel? = null
    private var phoneAnimator: NothingPhoneAnimation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GlyphTwentyTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    GlyphDemoScreen(
                        statusText = statusText,
                        isButtonEnabled = isButtonEnabled,
                        progressSliderValue = progressSliderValue,
                        onProgressSliderChange = { progressSliderValue = it },
                        onToggleChannelA = { phoneAnimator?.toggleChannelA() },
                        onAnimateChannelB = { phoneAnimator?.animateChannelB() },
                        onDisplayProgress = { phoneAnimator?.displayProgressOnChannel(progressSliderValue.toInt()) },
                        onTurnOffAll = { phoneAnimator?.turnOffGlyphs() }
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        init()
    }

    override fun onStop() {
        super.onStop()
        mGM?.unInit()
        Log.d(TAG, "GlyphManager uninitialized.")
        statusText = "Status: Glyph SDK uninitialized."
        isButtonEnabled = false
    }

    private fun init() {
        if (mGM != null) {
            Log.d(TAG, "GlyphManager already initialized.")
            return
        }

        mCallback = object : GlyphManager.Callback {
            override fun onServiceConnected(componentName: ComponentName) {
                Log.d(TAG, "Glyph Service Connected")
                statusText = "Status: Glyph Service Connected. Registering..."

                if (Common.is20111()) {
                    mGM?.register(Glyph.DEVICE_20111)
                    phoneModel = NothingPhoneModel.NOTHING_PHONE_1
                }
                if (Common.is22111()) {
                    mGM?.register(Glyph.DEVICE_22111)
                    phoneModel = NothingPhoneModel.NOTHING_PHONE_2
                }
                if (Common.is23111()) {
                    mGM?.register(Glyph.DEVICE_23111)
                    phoneModel = NothingPhoneModel.NOTHING_PHONE_2A
                }
                if (Common.is23113()) {
                    mGM?.register(Glyph.DEVICE_23113)
                    phoneModel = NothingPhoneModel.NOTHING_PHONE_2A_PLUS
                }
                if (Common.is24111()) {
                    mGM?.register(Glyph.DEVICE_24111)
                    phoneModel = NothingPhoneModel.NOTHING_PHONE_3
                }

                if (null != phoneModel) {
                    Log.i(appName, "${phoneModel!!.modelName} detected")
                    statusText = "Status: Detected ${phoneModel!!.modelName}. Initializing animator..."
                    try {
                        mGM?.openSession()
                        Log.d(TAG, "Glyph Session Opened")

                        phoneAnimator = NothingPhoneAnimation(phoneModel!!, mGM)
                        statusText = "Status: Ready to control Glyph!"
                        isButtonEnabled = true
                    } catch (e: GlyphException) {
                        Log.e(appName, "Error opening session or initializing animator: ${e.message}")
                        statusText = "Status: Error: ${e.message}"
                        isButtonEnabled = false
                    }
                } else {
                    Log.w(TAG, "Unknown Nothing Phone model or not a Nothing Phone.")
                    statusText = "Status: Unknown Nothing Phone model or not a Nothing Phone."
                    isButtonEnabled = false
                }
            }

            override fun onServiceDisconnected(componentName: ComponentName) {
                Log.d(TAG, "Glyph Service Disconnected")
                statusText = "Status: Glyph Service Disconnected."
                mGM?.closeSession()
                isButtonEnabled = false
                phoneAnimator = null
                phoneModel = null
            }
        }

        mGM = GlyphManager.getInstance(applicationContext)
        mGM?.init(mCallback)
        statusText = "Status: Initializing Glyph SDK..."
        isButtonEnabled = false
    }
}

@Composable
fun GlyphDemoScreen(
    statusText: String,
    isButtonEnabled: Boolean,
    progressSliderValue: Float,
    onProgressSliderChange: (Float) -> Unit,
    onToggleChannelA: () -> Unit,
    onAnimateChannelB: () -> Unit,
    onDisplayProgress: () -> Unit,
    onTurnOffAll: () -> Unit
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Control Nothing Glyph Interface",
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 32.dp),
            style = MaterialTheme.typography.headlineSmall
        )

        Button(
            onClick = onToggleChannelA,
            enabled = isButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Toggle Channel A (Main)", fontSize = 16.sp)
        }

        Button(
            onClick = onAnimateChannelB,
            enabled = isButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Animate Channel B (Breathing)", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Progress: ${progressSliderValue.toInt()}%",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Slider(
            value = progressSliderValue,
            onValueChange = onProgressSliderChange,
            valueRange = 0f..100f,
            steps = 99,
            enabled = isButtonEnabled,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onDisplayProgress,
            enabled = isButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Display Progress", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onTurnOffAll,
            enabled = isButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Turn Off All Glyphs", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = statusText,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GlyphTwentyTheme {
        GlyphDemoScreen(
            statusText = "Status: Preview Mode",
            isButtonEnabled = true,
            progressSliderValue = 50f,
            onProgressSliderChange = {},
            onToggleChannelA = {},
            onAnimateChannelB = {},
            onDisplayProgress = {},
            onTurnOffAll = {}
        )
    }
}
