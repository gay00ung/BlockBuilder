package net.lateinit.blockbuilder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import net.lateinit.blockbuilder.presentation.ui.screen.BlockBuilderScreen
import net.lateinit.blockbuilder.presentation.ui.theme.BlockBuilderTheme
import net.lateinit.blockbuilder.presentation.viewmodel.BlockchainViewModel

import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {
    private val viewModel: BlockchainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            BlockBuilderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BlockBuilderScreen(viewModel)
                }
            }
        }
    }
}