package com.willbegod.readmoresample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.willbegod.readmoresample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this), null, false).also {
            setContentView(it.root)
        }

        setCurrentReadMoreStateText()
        setToggleBtnClickListener()
        setReadMoreStateChangeListener()
    }

    private fun setCurrentReadMoreStateText() {
        binding.tvReadMoreState1.text = "ReadMoreView 1 : ${getReadMoreStateText(binding.rmvNormal.isExpanded)}"
        binding.tvReadMoreState2.text = "ReadMoreView 2 : ${getReadMoreStateText(binding.rmvLocationEnd.isExpanded)}"
        binding.tvReadMoreState2.text = "ReadMoreView 2 : ${getReadMoreStateText(binding.rmvToButton.isExpanded)}"
    }

    private fun setToggleBtnClickListener() {
        binding.btnToggle.setOnClickListener {
            binding.rmvToButton.toggle()
        }
    }

    private fun setReadMoreStateChangeListener() {
        binding.rmvNormal.setStateChangedListener { isExpanded ->
            binding.tvReadMoreState1.text = "ReadMoreView 1 : ${getReadMoreStateText(isExpanded)}"
        }

        binding.rmvLocationEnd.setStateChangedListener { isExpanded ->
            binding.tvReadMoreState2.text = "ReadMoreView 2 : ${getReadMoreStateText(isExpanded)}"
        }

        binding.rmvToButton.setStateChangedListener { isExpanded ->
            binding.tvReadMoreState3.text = "ReadMoreView 3 : ${getReadMoreStateText(isExpanded)}"
        }
    }

    private fun getReadMoreStateText(isExpanded: Boolean) = if (isExpanded) {
        "확장됨"
    } else {
        "축소됨"
    }
}