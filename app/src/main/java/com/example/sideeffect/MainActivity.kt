package com.example.sideeffect

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sideeffect.ui.theme.SideEffectTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            SideEffectTheme {
//                ListComposable()
//                RememberCoroutineScopeLaunchEffect()
//                RememberUpdateState()
                App()
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