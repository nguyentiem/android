package khoa.nv.applocker.ui.background

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import khoa.nv.applocker.databinding.FragmentBackgroundBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BackgroundFragment : Fragment() {
    private var _binding: FragmentBackgroundBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BackgroundFragmentViewModel by viewModels()
    private val backgroundAdapter = BackgroundAdapter {
        viewModel.onSelectedItemChanged(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBackgroundBinding.inflate(inflater, container, false)

        binding.recyclerViewBackgrounds.apply {
            setHasFixedSize(true)
            adapter = backgroundAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.backgroundState.flowWithLifecycle(lifecycle).collect {
                backgroundAdapter.setBackgrounds(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = BackgroundFragment()
    }
}