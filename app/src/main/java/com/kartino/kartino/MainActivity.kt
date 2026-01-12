package com.kartino.kartino

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.kartino.kartino.data.database.DatabaseModule
import com.kartino.kartino.data.repository.CardRepository
import com.kartino.kartino.navigation.NavGraph
import com.kartino.kartino.navigation.NavRoutes
import com.kartino.kartino.ui.theme.KartinOTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize database and repository
        val database = DatabaseModule.getDatabase(applicationContext)
        val repository = CardRepository(
            bankCardDao = database.bankCardDao(),
            customCardDao = database.customCardDao(),
            customFieldDao = database.customFieldDao()
        )
        
        setContent {
            KartinOTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        repository = repository,
                        startDestination = NavRoutes.SPLASH
                    )
                }
            }
        }
    }
}
