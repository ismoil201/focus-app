package com.ismoil.focus

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.provider.MediaStore.Audio
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.ismoil.focus.databinding.DialogCountDownSettingBinding
import com.ismoil.focus.databinding.FragmentMainBinding
import java.util.Timer
import kotlin.concurrent.timer

class MainFragment : Fragment() {


    lateinit var binding: FragmentMainBinding
    private var countdownSecond = 5
    private var currentCountdownDeciSecond = countdownSecond * 10
    private var currentDeciSecond = 0
    private var timer: Timer? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvCountDownSecond.setOnClickListener {
            showAlertDialogCountDown()
        }
        binding.btnStart.setOnClickListener {

            start()
            binding.btnStart.isVisible = false
            binding.btnStop.isVisible = false
            binding.btnPause.isVisible = true
            binding.btnLab.isVisible = true

        }

        binding.btnStop.setOnClickListener {
            showAlertDialog()

        }

        binding.btnPause.setOnClickListener {
            pause()
            binding.btnStart.isVisible = true
            binding.btnStop.isVisible = true
            binding.btnPause.isVisible = false
            binding.btnLab.isVisible = false

        }

        binding.btnLab.setOnClickListener {
            lab()
            binding.btnStart.isVisible = true
            binding.btnStop.isVisible = true
            binding.btnPause.isVisible = false
            binding.btnLab.isVisible = false

        }

        initCountDownViews()
    }

    private fun initCountDownViews() {

        binding.tvCountDownSecond.text = String.format("%02d", countdownSecond)
        binding.progressBar.progress = 100

    }


    private fun start() {

        timer = timer(initialDelay = 0, period = 100) {
            if (currentCountdownDeciSecond == 0) {
                currentDeciSecond += 1

                val minutes = currentDeciSecond.div(10) / 60
                val seconds = currentDeciSecond.div(10) % 60
                val deciSeconds = currentDeciSecond % 10

                requireActivity().runOnUiThread {
                    binding.timerText.text =
                        String.format("%02d:%02d", minutes, seconds)

                    binding.tvTimerMlSec.text = deciSeconds.toString()
                    binding.group.isVisible = false

                }
            } else {
                currentCountdownDeciSecond -= 1
                val seconds = currentCountdownDeciSecond / 10
                var progress = (currentCountdownDeciSecond / (countdownSecond * 10f)) * 100

                binding.root.post {
                    binding.tvCountDownSecond.text = String.format("%02d", seconds)
                    binding.progressBar.progress = progress.toInt()
                }

            }

            if (currentDeciSecond == 0 && currentCountdownDeciSecond < 31
                && currentCountdownDeciSecond % 10 == 0){
                val toneType = if(currentCountdownDeciSecond==0) ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK
                else ToneGenerator.TONE_CDMA_ANSWER
                ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME)
                    .startTone(toneType,100)
            }

        }

    }


    private fun pause() {
        timer?.cancel()
        timer = null

    }

    private fun stop() {
        binding.btnStart.isVisible = true
        binding.btnStop.isVisible = true
        binding.btnPause.isVisible = false
        binding.btnLab.isVisible = false

        currentDeciSecond = 0
        binding.timerText.text = "00:00"
        binding.tvTimerMlSec.text = "0"
        binding.group.isVisible = true
        initCountDownViews()


    }

    private fun lab() {

    }


    private fun showAlertDialog() {

        AlertDialog.Builder(requireContext()).apply {
            setMessage("Do you want delete this")
            setPositiveButton("yes") { _, _ ->
                stop()
            }

            setNegativeButton("no", null)
        }.show()
    }

    private fun showAlertDialogCountDown() {
        val dialogBinding = DialogCountDownSettingBinding.inflate(layoutInflater)

        with(dialogBinding.countDownSecondPicker) {
            maxValue = 20
            minValue = 0
            value = countdownSecond
        }
        AlertDialog.Builder(requireContext()).apply {
            setView(dialogBinding.root)
            setTitle("CountDown setting")
            setPositiveButton("Save") { _, _ ->
                countdownSecond = dialogBinding.countDownSecondPicker.value
                currentCountdownDeciSecond = countdownSecond * 10
                binding.tvCountDownSecond.text = String.format("%02d", countdownSecond)
            }
            setNegativeButton("Cancel", null)
        }.create().show()
    }
}