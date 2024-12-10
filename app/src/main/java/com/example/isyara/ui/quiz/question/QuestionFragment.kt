package com.example.isyara.ui.quiz.question

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isyara.R
import com.example.isyara.data.pref.UserPreferences
import com.example.isyara.databinding.FragmentQuestionBinding
import com.example.isyara.util.LoadImage

class QuestionFragment : Fragment() {

    private var _binding: FragmentQuestionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QuestionViewModel by viewModels {
        QuestionViewModelFactory.getInstance(requireContext())
    }

    private lateinit var userPreferences: UserPreferences
    private var levelId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionBinding.inflate(inflater, container, false)
        levelId = arguments?.getInt("itemId")
        userPreferences = UserPreferences(requireContext())
        setupRecyclerView()
        observeViewModel()

        userPreferences.getToken()?.let {
            viewModel.fetchQuestionById(it, levelId!!, 1)
        } ?: run {
            Toast.makeText(requireContext(), "Token is null", Toast.LENGTH_SHORT).show()
            userPreferences.clearToken()
            findNavController().navigate(R.id.action_questionFragment_to_onboardFragment)
        }

        binding.checkAnswer.setOnClickListener {
            userPreferences.getToken()?.let { token ->
                viewModel.checkAnswer(token)
            }
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerViewQuestion.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Log.d("QuestionFragment", "Error message: $it")
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.question.observe(viewLifecycleOwner) { questionResponse ->
            val question = questionResponse.data
            val name = question?.name
            val questionTitle = question?.question
            val image = question?.imageUrl
            val options = question?.options ?: emptyList()

            if (name != null && questionTitle != null && image != null) {
                binding.questionNumber.text = name
                binding.question.text = questionTitle
                LoadImage.load(
                    context = requireContext(),
                    imageView = binding.levelImage,
                    imageUrl = image,
                    placeholder = R.color.placeholder
                )

                val adapter = QuestionAdapter(options) { selectedOption ->
                    viewModel.selectOption(selectedOption)
                }
                binding.recyclerViewQuestion.adapter = adapter
            }
        }

        viewModel.checkAnswer.observe(viewLifecycleOwner) { checkAnswerResponse ->
            val isCorrect = checkAnswerResponse.isCorrect
            Log.d("QuestionFragment", "isCorrect: $isCorrect")
            if (isCorrect == true) {
                Toast.makeText(context, "Jawaban Benar!", Toast.LENGTH_SHORT).show()

                val nextQuestionId = viewModel.getNextQuestionId()
                Log.d("QuestionFragment", "Next Question ID: $nextQuestionId")
                if (nextQuestionId <= viewModel.totalQuestions) {
                    userPreferences.getToken()?.let { token ->
                        levelId?.let { level ->
                            viewModel.fetchQuestionById(token, level, nextQuestionId)
                        }
                    }
                } else {
                    if (viewModel.isQuizCompleted()) {
                        userPreferences.getToken()?.let { token ->
                            levelId?.let { level ->
                                viewModel.checkQuizCompletion(token, level)
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Jawaban Salah!", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.checkCompletion.observe(viewLifecycleOwner) { completionResponse ->
            if (completionResponse.message == "Congrats! Kamu telah menyelesaikan quiz Level 1. Silahkan lanjutkan perjalanan mu!") {
                findNavController().navigate(R.id.action_questionFragment_to_passResultFragment)
            } else if (completionResponse.message == "Failed. Oh tidak, kamu gagal menjawab semua pertanyaan dengan benar.") {
                findNavController().navigate(R.id.action_questionFragment_to_failedResultFragment)
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.levelContainer.visibility = View.GONE
                binding.recyclerViewQuestion.visibility = View.GONE
                binding.levelDescription.visibility = View.GONE
                binding.question.visibility = View.GONE
                binding.questionNumber.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.levelContainer.visibility = View.VISIBLE
                binding.recyclerViewQuestion.visibility = View.VISIBLE
                binding.levelDescription.visibility = View.VISIBLE
                binding.question.visibility = View.VISIBLE
                binding.questionNumber.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}