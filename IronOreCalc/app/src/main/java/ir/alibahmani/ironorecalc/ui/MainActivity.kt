package ir.alibahmani.ironorecalc.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import ir.alibahmani.ironorecalc.ui.navigation.AppNavGraph
import ir.alibahmani.ironorecalc.ui.theme.IronOreCalcTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IronOreCalcTheme {
                AppNavGraph()
            }
        }
    }
}
