package tools.certification.latency.auto.ui.main.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tools.certification.latency.auto.R
import tools.certification.latency.auto.data.database.entity.TestResult
import tools.certification.latency.auto.data.database.entity.Utterance
import tools.certification.latency.auto.databinding.LatencyTestItemBinding
import tools.certification.latency.auto.ext.setDrawableStart
import tools.certification.latency.auto.ext.setSpanColor
import tools.certification.latency.auto.utils.AccuracyUtils

@SuppressLint("SetTextI18n")
class LatencyTestAdapter(private val totalTests: Int) : RecyclerView.Adapter<LatencyTestAdapter.ViewHolder>() {
    private val testResults = mutableListOf<TestResult>()

    fun addItem(testResult: TestResult) {
        val position = testResults.size
        testResults.add(testResult)
        notifyItemInserted(position)
    }

    fun updateLastItem(testResult: TestResult) {
        if (testResults.isNotEmpty()) {
            val lastIndex = testResults.lastIndex
            testResults[lastIndex] = testResult
            notifyItemChanged(lastIndex)
        }
    }

    fun clear() {
        if (testResults.isNotEmpty()) {
            val oldSize = testResults.size
            testResults.clear()
            notifyItemRangeRemoved(0, oldSize)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LatencyTestItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding(testResults[position], position + 1)
    }

    override fun getItemCount() = testResults.size

    inner class ViewHolder(private val binding: LatencyTestItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun binding(testResult: TestResult, number: Int) = with(binding) {
            title.text = "Test $number/$totalTests: ${testResult.request.command} (Volume: ${(testResult.volume*100).toInt()}%, Noise:  ${(testResult.noise*100).toInt()}%)"
            showProgress(testResult)
            showResult(testResult)
        }

        private fun TextView.showIfCompleted(utterance: Utterance, showEnd: Boolean = true, keywords: List<String>? = null) {
            if (utterance.isNotCompleted) {
                visibility = View.GONE
                return
            }
            val time = if (showEnd) utterance.endText else utterance.startText
            setSpanColor(
                fullText = "$time: \"${utterance.command}\"",
                subText = "\"${utterance.command}\"",
                keywords
            )
            visibility = View.VISIBLE
        }

        private fun LatencyTestItemBinding.showProgress(testResult: TestResult) {
            hiBixby.showIfCompleted(testResult.wakeUp)
            ting.showIfCompleted(testResult.ting)

            if (testResult.ting.isCompleted) {
                hiLatency.setSpanColor(
                    fullText = "Wakeup Latency: ${testResult.ting.start - testResult.wakeUp.end}ms",
                    subText = "Wakeup Latency:"
                )
                hiLatency.visibility = View.VISIBLE
            } else {
                hiLatency.visibility = View.GONE
            }

            request.showIfCompleted(testResult.request)
            val keywords = testResult.response.command.split(" ").filter {
                AccuracyUtils.map[testResult.request.command]?.contains(it) == true
            }
            response.showIfCompleted(testResult.response, false, keywords)
        }

        private fun LatencyTestItemBinding.showResult(testResult: TestResult) {
            if (!testResult.isFinished) {
                requestLatency.visibility = View.GONE
                accuracy.visibility = View.GONE
                error.visibility = View.GONE
                title.setDrawableStart(R.drawable.ic_play)
                return
            }
            when (testResult.accuracy) {
                1 -> {
                    title.setDrawableStart(R.drawable.ic_success)
                    requestLatency.setSpanColor(
                        fullText = "Response Latency: ${testResult.response.start - testResult.request.end}ms",
                        subText = "Response Latency:"
                    )
                    requestLatency.visibility = View.VISIBLE
                    accuracy.setSpanColor(
                        fullText = "Accuracy: TRUE",
                        subText = "Accuracy:"
                    )
                    accuracy.visibility = View.VISIBLE
                }
                0 -> {
                    title.setDrawableStart(R.drawable.ic_failed)
                    requestLatency.setSpanColor(
                        fullText = "Response Latency: ${testResult.response.start - testResult.request.end}ms",
                        subText = "Response Latency:"
                    )
                    requestLatency.visibility = View.VISIBLE
                    accuracy.setSpanColor(
                        fullText = "Accuracy: FALSE",
                        subText = "Accuracy:"
                    )
                    accuracy.visibility = View.VISIBLE
                }
                else -> {
                    title.setDrawableStart(R.drawable.ic_failed)
                    if (testResult.wakeUp.isNotCompleted) {
                        error.text = "Could NOT play Wake Up"
                    } else if (testResult.ting.isNotCompleted) {
                        error.text = "Could NOT recognize Wake Up Response"
                    } else if (testResult.request.isNotCompleted) {
                        error.text = "Could NOT play Utterance"
                    } else if (testResult.response.isNotCompleted) {
                        error.text = "Could NOT recognize Bixby Response"
                    } else {
                        error.text = "Error. Please try again!"
                    }
                    error.visibility = View.VISIBLE
                }
            }
        }
    }
}
