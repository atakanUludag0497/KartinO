package com.kartino.kartino.navigation

/**
 * Navigation routes for the app
 */
object NavRoutes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val ADD_CARD = "add_card"
    const val BANK_CARD_FORM = "bank_card_form"
    const val CUSTOM_CARD_FORM = "custom_card_form"
    const val CARD_DETAIL = "card_detail"
    
    // Arguments
    const val CARD_ID = "card_id"
    const val CARD_TYPE = "card_type"
    
    // Full routes with arguments
    fun cardDetail(cardId: Long, cardType: String): String {
        return "$CARD_DETAIL/$cardId/$cardType"
    }
    
    fun bankCardForm(cardId: Long? = null): String {
        return if (cardId != null) {
            "$BANK_CARD_FORM/$cardId"
        } else {
            BANK_CARD_FORM
        }
    }
    
    fun customCardForm(cardId: Long? = null): String {
        return if (cardId != null) {
            "$CUSTOM_CARD_FORM/$cardId"
        } else {
            CUSTOM_CARD_FORM
        }
    }
}
