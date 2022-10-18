package khoa.nv.applocker.ui.newpattern

import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener

abstract class SimplePatternListener : PatternLockViewListener {

    override fun onCleared() {

    }

    override fun onStarted() {

    }

    override fun onProgress(progressPattern: List<PatternLockView.Dot>) {

    }

    abstract override fun onComplete(pattern: List<PatternLockView.Dot>);
}