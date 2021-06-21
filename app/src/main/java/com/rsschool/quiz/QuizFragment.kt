package com.rsschool.quiz

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.forEachIndexed
import com.rsschool.quiz.databinding.FragmentQuizBinding

private const val ARG_Q_NUMBER = "number"

class QuizFragment : Fragment() {

    private var questionNumber = 0
    private var idTheme = 0
    private var binding: FragmentQuizBinding? = null
    private var quizQuestion: QuestionBean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            questionNumber = it.getInt(ARG_Q_NUMBER)
        }

        idTheme = when (questionNumber) {
            0 -> R.style.Theme_Quiz_First
            1 -> R.style.Theme_Quiz_Second
            2 -> R.style.Theme_Quiz_Third
            3 -> R.style.Theme_Quiz_Fourth
            4 -> R.style.Theme_Quiz_Fifth
            else -> R.style.Theme_Quiz_First
        }
    }

    override fun onResume() {
        super.onResume()

        val value = TypedValue ()

        activity?.setTheme(idTheme)
        activity?.theme?.resolveAttribute(android.R.attr.statusBarColor, value, true)
        activity?.window?.statusBarColor = value.data

        binding?.apply {
            if (quizQuestion?.userAnswer != -1) {
                radioGroup.forEachIndexed { questionNumber, view ->
                    if (questionNumber == quizQuestion?.userAnswer) {
                        radioGroup.check(view.id)
                    }
                }
            }
        }

        enableButton()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity is QuizInterface)
            quizQuestion = (activity as QuizInterface).getQuestion(questionNumber)

        binding = FragmentQuizBinding.bind(view)
        binding?.apply {
            toolbar.title = getString(R.string.fragment_toolbar_title) + " ${questionNumber + 1}"
            toolbar.setNavigationOnClickListener {
                (activity as QuizInterface).goPrev(questionNumber)
            }

            prevButton.setOnClickListener { (activity as QuizInterface).goPrev(questionNumber) }

            if (questionNumber < (activity as QuizInterface).getQuestionCount() - 1) {
                nextButton.text = getString(R.string.fragment_next_button)
            } else {
                nextButton.text = getString(R.string.fragment_submit_button)
            }
            nextButton.setOnClickListener {
                if (activity is QuizInterface)
                    (activity as QuizInterface).goNext(questionNumber)
            }

            question.text    = quizQuestion?.question
            optionOne.text   = quizQuestion?.answer1
            optionTwo.text   = quizQuestion?.answer2
            optionThree.text = quizQuestion?.answer3
            optionFour.text  = quizQuestion?.answer4
            optionFive.text  = quizQuestion?.answer5

            radioGroup.setOnCheckedChangeListener { _, _ ->
                radioGroup.forEachIndexed { index, view ->
                    if ((view as RadioButton).isChecked) {
                        quizQuestion?.userAnswer = index
                    }
                }
                enableButton()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding?.radioGroup?.clearCheck()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.setTheme(idTheme)
        return inflater.inflate(R.layout.fragment_quiz, container, false)
    }

    private fun enableButton() {
        binding?.apply {
            prevButton.visibility = if(questionNumber == 0) View.GONE else View.VISIBLE
            nextButton.isEnabled = quizQuestion?.userAnswer != -1
            if (questionNumber == 0)
                toolbar.setNavigationIcon(null)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(pos: Int) =
            QuizFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_Q_NUMBER, pos)
                }
            }
    }
}