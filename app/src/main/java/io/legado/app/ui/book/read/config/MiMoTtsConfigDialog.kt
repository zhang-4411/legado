package io.legado.app.ui.book.read.config

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import io.legado.app.R
import io.legado.app.base.BaseDialogFragment
import io.legado.app.databinding.DialogMimoTtsConfigBinding
import io.legado.app.help.config.AppConfig
import io.legado.app.lib.theme.primaryColor
import io.legado.app.utils.applyTint
import io.legado.app.utils.setLayout
import io.legado.app.utils.toastOnUi
import io.legado.app.utils.viewbindingdelegate.viewBinding

class MiMoTtsConfigDialog : BaseDialogFragment(R.layout.dialog_mimo_tts_config, true),
    Toolbar.OnMenuItemClickListener {

    private val binding by viewBinding(DialogMimoTtsConfigBinding::bind)

    private val voices = arrayOf(
        "mimo_default", "冰糖", "茉莉", "苏打", "白桦",
        "Mia", "Chloe", "Milo", "Dean"
    )

    override fun onStart() {
        super.onStart()
        setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolBar.setBackgroundColor(primaryColor)
        initMenu()
        initView()
    }

    private fun initMenu() {
        binding.toolBar.inflateMenu(R.menu.mimo_tts_config)
        binding.toolBar.menu.applyTint(requireContext())
        binding.toolBar.setOnMenuItemClickListener(this)
    }

    private fun initView() {
        binding.tvApiKey.setText(AppConfig.mimoApiKey ?: "")
        binding.tvStylePrompt.setText(AppConfig.mimoStylePrompt ?: "")

        val voiceAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            voices
        )
        binding.tvVoice.setAdapter(voiceAdapter)
        binding.tvVoice.setText(AppConfig.mimoVoice, false)
        binding.tvVoice.setOnClickListener { binding.tvVoice.showDropDown() }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_save -> save()
        }
        return true
    }

    private fun save() {
        val apiKey = binding.tvApiKey.text?.toString()?.trim()
        if (apiKey.isNullOrBlank()) {
            toastOnUi("API Key 不能为空")
            return
        }
        val voice = binding.tvVoice.text?.toString()?.trim()
        if (voice.isNullOrBlank()) {
            toastOnUi("音色不能为空")
            return
        }
        val stylePrompt = binding.tvStylePrompt.text?.toString()?.trim()

        AppConfig.mimoApiKey = apiKey
        AppConfig.mimoVoice = voice
        AppConfig.mimoStylePrompt = if (stylePrompt.isNullOrBlank()) null else stylePrompt

        toastOnUi("保存成功")
        dismissAllowingStateLoss()
    }
}
