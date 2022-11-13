package eu.auct.twitter2nitter

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import java.util.regex.Matcher
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref  = getPreferences(MODE_PRIVATE)

        when {
            intent?.action == Intent.ACTION_SEND && "text/plain" == intent.type -> {
                handleSendText(intent)
            }
            intent?.action == Intent.ACTION_VIEW -> {
                handleUrl(intent)
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_settings -> {
                showDialog()
                true
            }
            R.id.action_source_code -> {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.source_code_url))
                    )
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun showDialog() {
        this.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                val input = EditText(context)
                input.setText(getRedirectUrl())
                input.inputType = InputType.TYPE_CLASS_TEXT
                builder.setView(input)
                builder.setTitle(R.string.redirect_url)

                setPositiveButton(R.string.save,
                    DialogInterface.OnClickListener { dialog, id ->
                        setRedirectUrl(input.text.toString())

                    })
                setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })
            }
            builder.show()
        }
    }

    private fun handleSendText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            val link = getRedirectUrl() + getTweet(it)
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
            finish()
        }
    }

    private fun handleUrl(intent: Intent) {
        val data = intent.data

        if (data != null) {
            val link = getRedirectUrl() + data.path
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
            finish()
        }
    }

    private fun setRedirectUrl(url: String) {
        with(sharedPref.edit()) {
            putString(getString(R.string.pref_redirect_url_key), url)
            apply()
        }
    }

    private fun getRedirectUrl(): String? {
        val defaultValue = resources.getString(R.string.pref_redirect_url_default)
        return sharedPref.getString(getString(R.string.pref_redirect_url_key), defaultValue)
    }

    private fun getTweet(input: String): String? {
        val pattern: Pattern = Pattern.compile(".*?twitter\\.com(/.*?)(\\s|\$)")
        val matcher: Matcher = pattern.matcher(input)
        matcher.find()

        try {
            return matcher.group(1)
        } catch (t: Throwable) {
            Toast.makeText(
                this,
                getString(R.string.error_regex) + "\n" + t.message,
                Toast.LENGTH_LONG
            ).show()
            finish()
        }

        return null
    }
}