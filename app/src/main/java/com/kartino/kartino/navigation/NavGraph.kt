package com.kartino.kartino.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kartino.kartino.data.model.Card
import com.kartino.kartino.data.repository.CardRepository
import com.kartino.kartino.ui.screen.AddCardScreen
import com.kartino.kartino.ui.screen.BankCardFormScreen
import com.kartino.kartino.ui.screen.CardDetailScreen
import com.kartino.kartino.ui.screen.CustomCardFormScreen
import com.kartino.kartino.ui.screen.HomeScreen
import com.kartino.kartino.ui.screen.SplashScreen

/**
 * Navigation graph for the app
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    repository: CardRepository,
    startDestination: String = NavRoutes.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavRoutes.SPLASH) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        
        composable(NavRoutes.HOME) {
            HomeScreen(
                repository = repository,
                onNavigateToAddCard = {
                    navController.navigate(NavRoutes.ADD_CARD)
                },
                onNavigateToCardDetail = { card ->
                    val cardType = when (card) {
                        is Card.Bank -> "bank"
                        is Card.Custom -> "custom"
                    }
                    navController.navigate(NavRoutes.cardDetail(card.id, cardType))
                },
                onDeleteAll = {
                    // Handle delete all if needed
                }
            )
        }
        
        composable(NavRoutes.ADD_CARD) {
            AddCardScreen(
                onNavigateToBankCardForm = {
                    navController.navigate(NavRoutes.bankCardForm())
                },
                onNavigateToCustomCardForm = {
                    navController.navigate(NavRoutes.customCardForm())
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Bank Card Form - Create new
        composable(NavRoutes.BANK_CARD_FORM) {
            BankCardFormScreen(
                repository = repository,
                cardId = null,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Bank Card Form - Edit existing
        composable(
            route = "${NavRoutes.BANK_CARD_FORM}/{${NavRoutes.CARD_ID}}",
            arguments = listOf(
                navArgument(NavRoutes.CARD_ID) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getLong(NavRoutes.CARD_ID)
            
            BankCardFormScreen(
                repository = repository,
                cardId = cardId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Custom Card Form - Create new
        composable(NavRoutes.CUSTOM_CARD_FORM) {
            CustomCardFormScreen(
                repository = repository,
                cardId = null,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Custom Card Form - Edit existing
        composable(
            route = "${NavRoutes.CUSTOM_CARD_FORM}/{${NavRoutes.CARD_ID}}",
            arguments = listOf(
                navArgument(NavRoutes.CARD_ID) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getLong(NavRoutes.CARD_ID)
            
            CustomCardFormScreen(
                repository = repository,
                cardId = cardId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = "${NavRoutes.CARD_DETAIL}/{${NavRoutes.CARD_ID}}/{${NavRoutes.CARD_TYPE}}",
            arguments = listOf(
                navArgument(NavRoutes.CARD_ID) { type = NavType.LongType },
                navArgument(NavRoutes.CARD_TYPE) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getLong(NavRoutes.CARD_ID) ?: return@composable
            val cardType = backStackEntry.arguments?.getString(NavRoutes.CARD_TYPE) ?: return@composable
            
            CardDetailScreen(
                repository = repository,
                cardId = cardId,
                cardType = cardType,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEdit = { id, type ->
                    if (type == "bank") {
                        navController.navigate(NavRoutes.bankCardForm(id)) {
                            popUpTo(NavRoutes.CARD_DETAIL) { inclusive = true }
                        }
                    } else {
                        navController.navigate(NavRoutes.customCardForm(id)) {
                            popUpTo(NavRoutes.CARD_DETAIL) { inclusive = true }
                        }
                    }
                },
                onDeleteCard = {
                    navController.popBackStack()
                }
            )
        }
    }
}
