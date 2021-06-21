package com.rsschool.quiz

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rsschool.quiz.databinding.FragmentSubmitBinding
import kotlin.system.exitProcess

private const val ARG_RESULT_TEXT = "result_text"

class SubmitFragment : Fragment() {
    private var resultText: String? = null
    private var binding: FragmentSubmitBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            resultText = it.getString(ARG_RESULT_TEXT)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSubmitBinding.bind(view)
        binding?.apply {
            shareButton.setOnClickListener {
                if (activity is QuizInterface) {
                    val shareText = (activity as QuizInterface).getShareText()

                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject))
                        type = "text/plain"
                    }

                    if (activity?.packageManager?.let { it1 -> sendIntent.resolveActivity(it1) } != null) {
                        startActivity(sendIntent)
                    }
                }
            }
            exitButton.setOnClickListener {
                exitProcess(0)
            }
            backButton.setOnClickListener {
                if (activity is QuizInterface)
                    (activity as QuizInterface).restartQuiz()
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_submit, container, false)
    }

    override fun onStart() {
        super.onStart()
        if (activity is QuizInterface)
            binding?.resultView?.text = (activity as QuizInterface).getResultText()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
    companion object {
        @JvmStatic
        fun newInstance() = SubmitFragment()
    }
}