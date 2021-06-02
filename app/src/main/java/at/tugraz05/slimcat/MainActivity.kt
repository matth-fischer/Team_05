package at.tugraz05.slimcat

import android.animation.LayoutTransition
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton
import at.tugraz05.slimcat.databinding.CatAccordionBinding
import at.tugraz05.slimcat.databinding.CatAccordionFoodBinding
import java.nio.file.Files


class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        DatabaseHelper.get().maybeInit(applicationContext)

        findViewById<FloatingActionButton>(R.id.btn_addcat).setOnClickListener {
            val intent = Intent(this, AddcatActivity::class.java)
            startActivity(intent)
        }

        DatabaseHelper.get().addValueEventListener{
            displayCats(DatabaseHelper.get().readUserCats())
        }

        LanguageHandler.setLanguage(this)
        supportActionBar!!.title = getString(R.string.app_name)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivityForResult(intent, 0)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        recreate()

    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
    }

    fun displayCats(cats: List<CatDataClass?>) {
        val container = findViewById<LinearLayout>(R.id.scroll_content)
        container.children.forEach { view ->
            val binding = DataBindingUtil.bind<CatAccordionBinding>(view)
            if (binding != null) {
                val foundCat = cats.find {
                    it?.name == binding.cat?.name
                }
                if (foundCat != null) {
                    binding.cat = foundCat
                    updateCatImage(binding)
                    updateFoods(binding)
                }
                else {
                    container.removeView(view)
                }
            }
        }

        cats.forEach { cat ->
            if (container.children.find { view -> DataBindingUtil.bind<CatAccordionBinding>(view)?.cat?.name == cat?.name} == null) {
                val binding = DataBindingUtil.inflate<CatAccordionBinding>(layoutInflater, R.layout.cat_accordion, container, false)
                binding.cat = cat
                binding.presenter = CatAccordionPresenter(this, binding)
                container.addView(binding.root)
                binding.root.findViewById<Button>(R.id.edit_cat).setOnClickListener {
                    val intent = Intent(this, AddcatActivity::class.java)
                    val bundle = bundleOf("Cat" to binding!!.cat)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
                updateCatImage(binding)
                updateFoods(binding)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    binding.root.findViewById<FrameLayout>(R.id.collapsible).layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            }
        }
    }

    private fun updateCatImage(binding: CatAccordionBinding) {
        if (binding.cat?.imageString?.isNotEmpty() == true) {
            val file = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.toPath().resolve(binding.cat!!.imageString!!).toFile()

            if (!file.exists()) {
                Files.createDirectories(file.parentFile!!.toPath())
                file.createNewFile()
                DatabaseHelper.get().getImage("${DatabaseHelper.get().getUserId()}/${binding.cat!!.imageString!!}", file) {
                    binding.root.findViewById<ImageView>(R.id.imageView).setImageURI(Uri.fromFile(file))
                }
            }
            else {
                binding.root.findViewById<ImageView>(R.id.imageView).setImageURI(Uri.fromFile(file))
            }
        }
    }

    private fun updateFoods(binding: CatAccordionBinding) {
        val table = binding.root.findViewById<TableLayout>(R.id.accordion_food_list)
        table.removeAllViews()

        Food.foods.forEach {
            val b = DataBindingUtil.inflate<CatAccordionFoodBinding>(
                layoutInflater,
                R.layout.cat_accordion_food,
                table,
                false
            )
            b.cat = binding.cat
            b.food = it
            table.addView(b.root)
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LanguageHandler.setLanguage(newBase!!))
    }
}
