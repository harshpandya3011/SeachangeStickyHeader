package com.seachange.healthandsafty.fragment

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import kotlinx.android.synthetic.main.fragment_item_list3.*
import com.seachange.healthandsafty.model.JSAAnswer
import com.seachange.healthandsafty.model.JSAQuestion

import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.adapter.JSAQuestionRecyclerViewAdapter
import com.seachange.healthandsafty.application.SCApplication
import com.seachange.healthandsafty.helper.Logger
import com.seachange.healthandsafty.helper.View.SlideAnimationUtil


class JSAQuestionFragment : BaseFragment() {
    // TODO: Customize parameters
    private var mColumnCount = 1
    private var mListener: OnListFragmentInteractionListener? = null
    private lateinit var mRecyclerView: RecyclerView
    private var mAnswerAdapter: JSAQuestionRecyclerViewAdapter? = null
    private var mQuestions: ArrayList<JSAQuestion> = ArrayList<JSAQuestion>()
    private lateinit var mNextButton: Button
    private lateinit var mPreButton: Button
    private var mCurrentQuestionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            mColumnCount = arguments!!.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mPreButton.setOnClickListener {
            updateQuestion(false)
        }

        mNextButton.setOnClickListener {
            updateQuestion(true)
        }

        mListener =  object : OnListFragmentInteractionListener {
            override fun onAnswerSelectInteraction(answer: JSAAnswer, index: Int) {
                val tmp: JSAQuestion = mQuestions.get(mCurrentQuestionIndex)
                tmp.answers.get(index).isChecked = !tmp.answers.get(index).isChecked

                for (i in 0..tmp.answers.size-1) {

                    if (i != index) {
                        tmp.answers.get(i).isChecked = false
                    }

                }

                mAnswerAdapter!!.reloadQuestion(tmp.answers)
                mAnswerAdapter!!.notifyDataSetChanged()
            }
        }


        val answers1: ArrayList<JSAAnswer> =  ArrayList<JSAAnswer>()
        answers1.add(JSAAnswer("Poorly rotating castors can cause: 1) Strain to back/shoulders when pushing wheels that won't rotate freely; 2) Veering to one side & colliding into somebody.", false))
        answers1.add(JSAAnswer("A Fire involving Flammable Liquids: petrol, meths, solvents.", false))
        answers1.add(JSAAnswer("Red body with a black label; 2) Suitable for Class B & Electrical Fires; 3) Never hold the nozzle as it will get extremely cold & cause burns; Do not use in confined spaces as CO2 can cause breathing difficulties.", false))

        val answers2: ArrayList<JSAAnswer> =  ArrayList<JSAAnswer>()
        answers2.add(JSAAnswer("A Fire involving Flammable Liquids: petrol, meths, solvents.", false))
        answers2.add(JSAAnswer("Strain to back/shoulders when pushing wheels that won't rotate freely; 2) Veering to one side & colliding into somebody.", false))
        answers2.add(JSAAnswer("Suitable for Class B & Electrical Fires; 3) Never hold the nozzle as it will get extremely cold & cause burns; Do not use in confined spaces as CO2 can cause breathing difficulties.", false))

        val answers3: ArrayList<JSAAnswer> =  ArrayList<JSAAnswer>()
        answers3.add(JSAAnswer("Do not use in confined spaces as CO2 can cause breathing difficulties. petrol, meths, solvents.", false))
        answers3.add(JSAAnswer("Veering to one side & colliding into somebody.", false))
        answers3.add(JSAAnswer("Never hold the nozzle as it will get extremely cold & cause burns; ", false))

        val tmp1 = JSAQuestion("What are the safety implications of having snarled up / jammed castors on combis/cages?", answers1)
        val tmp2 = JSAQuestion("This is question number 2. What are the safety implications of having snarled up / jammed castors on combis/cages?", answers2)
        val tmp3 = JSAQuestion("This is question number 3. What are the safety implications of having snarled up / jammed castors on combis/cages?", answers3)

        mQuestions.add(tmp1)
        mQuestions.add(tmp2)
        mQuestions.add(tmp3)

        updatePreAndNextButtons()
        jsa_question_title.text = tmp1.question
        mAnswerAdapter = JSAQuestionRecyclerViewAdapter(mCtx, tmp1.answers, mListener)
        mRecyclerView.adapter = mAnswerAdapter

        jsa_pre_arrow.typeface = SCApplication.FontMaterial()
        jsa_next_arrow.typeface = SCApplication.FontMaterial()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_item_list3, container, false)

        val context = view.context

        mRecyclerView = view.findViewById<View>(R.id.jsaQuestionRecyclyerView) as RecyclerView
        mPreButton = view.findViewById<Button>(R.id.jsa_pre_button)
        mNextButton = view.findViewById<Button>(R.id.jsa_next_button)

        mRecyclerView. layoutManager = LinearLayoutManager(context)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.isFocusable = false

        return view
    }

    private fun updateQuestion(next: Boolean) {


        if (next) {
            if (mCurrentQuestionIndex<mQuestions.size-1){
                mCurrentQuestionIndex++
                mAnswerAdapter!!.reloadQuestion(mQuestions.get(mCurrentQuestionIndex).answers)
                jsa_question_title.text = mQuestions.get(mCurrentQuestionIndex).question
                mAnswerAdapter!!.notifyDataSetChanged()
                updatePreAndNextButtons()
                animateChangeQuestion(jsa_question_content, true)
            } else {
                Logger.info("Last question is on the screen")
            }
        } else {
            if (mCurrentQuestionIndex ==0) {
                Logger.info("first question is on the screen")
            } else {
                mCurrentQuestionIndex--
                mAnswerAdapter!!.reloadQuestion(mQuestions.get(mCurrentQuestionIndex).answers)
                jsa_question_title.text = mQuestions.get(mCurrentQuestionIndex).question
                mAnswerAdapter!!.notifyDataSetChanged()
                updatePreAndNextButtons()
                animateChangeQuestion(jsa_question_content, false)

            }
        }
    }

    private fun updatePreAndNextButtons() {
        mNextButton.isEnabled = mCurrentQuestionIndex != mQuestions.size-1
        if (mNextButton.isEnabled){
            mNextButton.setTextColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
            jsa_next_arrow.setTextColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
        } else {
            mNextButton.setTextColor(ContextCompat.getColor(mCtx, R.color.buttonDisableGrey))
            jsa_next_arrow.setTextColor(ContextCompat.getColor(mCtx, R.color.buttonDisableGrey))
        }
        mPreButton.isEnabled = mCurrentQuestionIndex != 0

        if (mPreButton.isEnabled){
            mPreButton.setTextColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
            jsa_pre_arrow.setTextColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
        } else {
            mPreButton.setTextColor(ContextCompat.getColor(mCtx, R.color.buttonDisableGrey))
            jsa_pre_arrow.setTextColor(ContextCompat.getColor(mCtx, R.color.buttonDisableGrey))
        }

        jsa_question_count_title.text = "Question " + (mCurrentQuestionIndex +1) + " of " + mQuestions.size

    }

    fun animateChangeQuestion(view: View, next: Boolean) = if (next) {
        SlideAnimationUtil.slideInFromRight(context, view);
    } else {
        SlideAnimationUtil.slideInFromLeft(context, view);
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onAnswerSelectInteraction(answer: JSAAnswer, index: Int)
    }

    companion object {

        // TODO: Customize parameter argument names
        private val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        fun newInstance(columnCount: Int): JSAQuestionFragment {
            val fragment = JSAQuestionFragment()
            val args = Bundle()
            args.putInt(ARG_COLUMN_COUNT, columnCount)
            fragment.arguments = args
            return fragment
        }
    }
}
