package com.udacity.project4.ui.reminder.rule

import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.core.context.stopKoin

class KoinRule : TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
    }

    override fun finished(description: Description?) {
        super.finished(description)

        stopKoin()
    }
}