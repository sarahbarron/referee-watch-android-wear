import android.graphics.Color
import android.view.View
import android.widget.Toast
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Convert the Jersey Number input String value to an integer
fun convertJerseyNumToInt(jerseyNum: String): Int {
    return Integer.parseInt(jerseyNum)
}

/* After a Red card has been given and the background has been set to red
       use a coroutine to delay for 5 seconds before changing the color back to black
   * */
fun setBackgroundBlack(view: View) {
    GlobalScope.launch {
        delay(5000)
        view.setBackgroundColor(Color.BLACK)
    }
}

/*
Combine both voice and input text notes together into one string
 */
fun getVoiceAndTextNote(voiceNotes: ArrayList<String>, textNotes: String) : String{
    var allNotes = "Notes: "
    if(textNotes.isNotEmpty()) allNotes = "$allNotes,"
    for(note in voiceNotes)
    {
        allNotes = "$allNotes, $note"
    }
    return allNotes
}


