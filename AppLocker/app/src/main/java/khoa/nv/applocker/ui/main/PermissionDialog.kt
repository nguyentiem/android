package khoa.nv.applocker.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import khoa.nv.applocker.databinding.DialogPermissionBinding
import khoa.nv.applocker.util.helper.PermissionChecker
import khoa.nv.applocker.util.helper.overlayIntent
import khoa.nv.applocker.util.helper.usageAccessIntent

class PermissionDialog : BottomSheetDialogFragment() {
    private lateinit var binding: DialogPermissionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogPermissionBinding.inflate(inflater, container, false).apply {

            cardViewUsageAccess.setOnClickListener {
                switchUsageAccess.isChecked = !switchUsageAccess.isChecked
            }
            switchUsageAccess.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && !PermissionChecker.checkUsageAccessPermission(requireContext())) {
                    startActivity(usageAccessIntent())
                }
            }

            cardViewOverlay.setOnClickListener {
                switchOverlay.isChecked = !switchOverlay.isChecked
            }
            switchOverlay.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && !PermissionChecker.checkOverlayPermission(requireContext())) {
                    startActivity(
                        overlayIntent(requireContext().packageName)
                    )
                }
            }
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (checkOverlayChecked(binding) && checkUsageAccessChecked(binding)) {
            dismiss()
        }
    }

    private fun checkOverlayChecked(binding: DialogPermissionBinding): Boolean {
        val isChecked = PermissionChecker.checkOverlayPermission(requireContext())
        binding.cardViewOverlay.isChecked = isChecked
        binding.switchOverlay.isChecked = isChecked

        binding.cardViewOverlay.visibility = if (isChecked) View.GONE else View.VISIBLE
        return isChecked
    }

    private fun checkUsageAccessChecked(binding: DialogPermissionBinding): Boolean {
        val isChecked = PermissionChecker.checkUsageAccessPermission(requireContext())
        binding.cardViewUsageAccess.isChecked = isChecked
        binding.switchUsageAccess.isChecked = isChecked

        binding.cardViewUsageAccess.visibility = if (isChecked) View.GONE else View.VISIBLE
        return isChecked
    }

    companion object {
        const val TAG = "PermissionDialog"
    }
}