package com.kickstarter.libs.utils

class EventContextValues {

    /**
     * Determines the button that was clicked or tapped by a user.
     *
     * @param contextName: The name of the event context.
     *
     * ADD_ONS_CONTINUE: The add-ons screen/carousel for a reward.
     * PLEDGE_INITIATE: The checkout screen.
     * PLEDGE_SUBMIT: The project screen.
     * REWARD_CONTINUE: The Rewards carousel.
     */
    enum class CtaContextName(val contextName: String) {
        ADD_ONS_CONTINUE("add_ons_continue"),
        PLEDGE_INITIATE("pledge_initiate"),
        PLEDGE_SUBMIT("pledge_submit"),
        REWARD_CONTINUE("reward_continue")
    }

    /**
     * Determines which screen has been presented.
     *
     * @param contextName: The name of the event context.
     *
     * ADD_ONS: The add-ons screen/carousel for a reward.
     * CHECKOUT: The checkout screen.
     * PROJECT: The project screen.
     * REWARDS: The Rewards carousel.
     * THANKS: The Thanks page.
     */
    enum class PageViewedContextName(val contextName: String) {
        ADD_ONS("add_ons"),
        CHECKOUT("checkout"),
        PROJECT("project"),
        REWARDS("rewards"),
        THANKS("thanks")
    }
}