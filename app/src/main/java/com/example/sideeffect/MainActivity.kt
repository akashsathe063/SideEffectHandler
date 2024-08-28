package com.example.sideeffect

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import android.view.WindowInsets
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import com.example.sideeffect.ui.theme.SideEffectTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            SideEffectTheme {
//                ListComposable()
//                RememberCoroutineScopeLaunchEffect()
//                RememberUpdateState()
//                App()
//                DisposibleEffectHandler()
//                MediaComposable()
//                KeyBoardComposable()
//
//                TextField(value = "", onValueChange = {})
//                ProduceStateEffectHandler()
                Loader()
            }
        }
    }
}

/**
 * this is a example off side effect
 * becase we can update the value of variable
 * @param count in side a
 * @param HasSideEffect function because the variable is outside variable of the function scope
 */

var count = 1

@Composable
fun HasSideEffect(modifier: Modifier = Modifier) {
    count++
}


/**
 * in this following functio we can use effect handler
 * to handle the side effect
 * in this example we can implement LaunchEffect(key =)
 * this one is the side effect handler
 * by using this side effect handler we can do one time activity.
 */

@Composable
fun ListComposable(modifier: Modifier = Modifier) {
    var categoryState by remember {
        mutableStateOf(emptyList<String>())
    }

    LaunchedEffect(key1 = Unit) {
        categoryState = fetchCategories()
    }
    LazyColumn(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(categoryState) { item ->
            Text(
                text = item,
                textAlign = TextAlign.Center
            )
        }
    }
}

fun fetchCategories(): List<String> {
    //assuming network call
    return listOf("one", "two", "three")
}


/**
 * rememberCoroutineScope this is also a side effect handler
 * when i want to launch coroutine on the event then this launch effect we can use
 * this scope is conected with the composable life cycle
 * when the scope is removed from the composable inside the scope another coroutine also cancelled
 *
 */

@Composable
fun RememberCoroutineScopeLaunchEffect() {
    val scope = rememberCoroutineScope()
    var counter by remember {
        mutableIntStateOf(0)
    }
    val text = "Counter is running $counter"
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Text(text = text)
            Spacer(modifier = Modifier.padding(5.dp))
            Button(onClick = {
                counter = 0
                scope.launch {
                    Log.d("RememberCoroutineScope", "RememberCoroutineScopeLaunchEffect start ")
                    try {
                        repeat(30) {
                            counter++
                            delay(1000)
                        }
                    } catch (e: Exception) {
                        Log.d(
                            "RememberCoroutineScope",
                            "RememberCoroutineScopeLaunchEffect: ${e.message} "
                        )
                    }
                }
            }) {
                Text(text = "Start Counter")
            }
        }
    }
}


/**
 * RememberUpdateState is not a side effect
 * you have executed the long runing task keep doing it
 * you want to execute it oly once then you keep doing it whatever you are passing inside the
 * state it should always be done with the updated value
 */

@Composable
fun RememberUpdateState(modifier: Modifier = Modifier) {
    var counter by remember {
        mutableIntStateOf(0)
    }
    LaunchedEffect(key1 = Unit) {
        delay(2000)
        counter = 10
    }

    CounterIncrement(counter = counter)

}

//this is also example of rememberUpdateState
@Composable
fun CounterIncrement(counter: Int) {
    val state = rememberUpdatedState(newValue = counter)
    LaunchedEffect(Unit) {
        delay(5000)
        Log.d("CounterIncrement", "CounterIncrement: ${state.value} ")
    }

    Text(text = counter.toString())
}

fun a() {
    Log.d("a", "a: called function A")
}

fun b() {
    Log.d("a", "a: called function B")
}

@Composable
fun App(modifier: Modifier = Modifier) {
    var state by remember {
        mutableStateOf(::a)
    }
    Button(onClick = { state = ::b }) {
        Text(text = "change the state")
    }
    LandingScreen(state)

}

@Composable
fun LandingScreen(onTimeOut: () -> Unit) {
    val currentOnTimeOut by rememberUpdatedState(newValue = onTimeOut)
    LaunchedEffect(true) {
        delay(5000)
        currentOnTimeOut()
    }
}

/**
 * Disposible Effect:-
 * such side effect in which we require clean up,so there we use the diposible effect
 * before leaving the composition so there we use this disposible effect to clean up.
 * it is also run in initial stage
 * or when key change then it will be run
 */

@Composable
fun DisposibleEffectHandler() {
    var state by remember {
        mutableStateOf(false)
    }
    DisposableEffect(key1 = state) {
        Log.d("DisposibleEffectHandler", "DisposibleEffectHandler: start ")
        onDispose {
            Log.d("DisposibleEffectHandler", "DisposibleEffectHandler: clean up ")
        }
    }

    Button(onClick = { state = !state }) {
        Text(text = "click me")
    }
}

/**
 * Second Example of Disposible effect
 */

@Composable
fun MediaComposable(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    DisposableEffect(key1 = Unit) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.ak)
        mediaPlayer.start()
        onDispose {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }
}

/**
 * Third Example of Disposible effect
 */
@RequiresApi(Build.VERSION_CODES.R)
@SuppressLint("WrongConstant")
@Composable
fun KeyBoardComposable() {
    val view = LocalView.current
    DisposableEffect(key1 = Unit) {
        //This line creates a new instance of OnGlobalLayoutListener.
        // This listener will be triggered every time the layout of the view tree is changed.
        // The lambda inside it will be executed when the layout is finished and the view tree is drawn.

        val listner = ViewTreeObserver.OnGlobalLayoutListener {
            //This line retrieves the current window insets for the view.
            // Insets are the areas of a window where other UI elements (like the status bar, navigation bar, or keyboard) occupy space.
            val insets = ViewCompat.getRootWindowInsets(view)
            //This line checks if the Input Method Editor (IME), which typically refers to the on-screen keyboard, is visible or not.
            //insets?.isVisible(WindowInsets.Type.ime()) returns a Boolean value: true if the keyboard is visible, or false if it is not.
            // If insets is null, the ?. operator ensures that isKeyBoardVisible is null as well.
            val isKeyBoardVisible = insets?.isVisible(WindowInsets.Type.ime())
            Log.d("KeyBoardComposable", "KeyBoardComposable: ${isKeyBoardVisible.toString()} ")
        }
        //This line registers the listner with the view's ViewTreeObserver.
        // The ViewTreeObserver is a utility class that provides notifications when certain events happen in the view tree.
        // By adding the listener, the app is set up to monitor changes in the layout, specifically to detect changes that might indicate the keyboard is shown or hidden.
        view.viewTreeObserver.addOnGlobalLayoutListener(listner)
        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(listner)
        }
    }
}

/**
 * Produce state :-
 * a produce state side effect handler is combination of launchEffect and state object
 */

@SuppressLint("ProduceStateDoesNotAssignValue")
@Composable
fun ProduceStateEffectHandler(modifier: Modifier = Modifier) {
    val state = produceState(initialValue = 0) {
        for (i in 1..10) {
            delay(1000)
            value++
        }
    }

    Text(
        text = state.value.toString(),
        style = MaterialTheme.typography.titleLarge
    )
}

/**
 * Example 2  producestate
 */

@Composable
fun Loader(modifier: Modifier = Modifier) {
    val degree = produceState(initialValue = 0) {
        while (true) {
            delay(16)
            value = (value + 10) % 360
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Image(imageVector = Icons.Default.Refresh, contentDescription = "refresh",
        modifier = Modifier
            .size(60.dp)
            .rotate(degree.value.toFloat()))

            Text(text = "Loading")
        }
    }
}