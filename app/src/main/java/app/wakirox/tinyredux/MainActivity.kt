package app.wakirox.tinyredux

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.wakirox.tinyredux.redux.StoreProvider
import app.wakirox.tinyredux.ui.counter.CounterView
import app.wakirox.tinyredux.ui.text_input.TextInputViewBind
import app.wakirox.tinyredux.ui.text_input.TextInputViewBindWithAction
import app.wakirox.tinyredux.ui.theme.TinyReduxTheme

class MainActivity : ComponentActivity() {

    //Not best practice to use this in production
    private val defaultStore = StoreProvider.defaultStore
    private val counterStore = StoreProvider.counterStore
    private val textStore = StoreProvider.textStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TinyReduxTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->


                    Column(modifier = Modifier.padding(innerPadding)) {
                        CounterView(
                            store = counterStore,
                            modifier = Modifier.padding(10.dp)
                        )

                        TextInputViewBind(store = textStore, modifier = Modifier.padding(10.dp))

                        TextInputViewBindWithAction(store = textStore, modifier = Modifier.padding(10.dp))
                    }
                }
            }
        }
    }
}



