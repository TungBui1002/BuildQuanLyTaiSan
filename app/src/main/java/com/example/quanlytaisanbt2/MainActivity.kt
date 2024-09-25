package com.example.quanlytaisanbt2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlytaisanbt2.Controller.AssetsController
import com.example.quanlytaisanbt2.Controller.PersonController
import com.example.quanlytaisanbt2.Data.DataAsset
import com.example.quanlytaisanbt2.Data.DataPerson
import com.example.quanlytaisanbt2.UI.formatMoney
import com.example.quanlytaisanbt2.adapter.AssetAdapter
import com.example.quanlytaisanbt2.adapter.PersonAdapter
import com.example.quanlytaisanbt2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var personAdapter: PersonAdapter
    private lateinit var assetAdapter: AssetAdapter
    private val personList = mutableListOf<DataPerson>()
    private val selectedAssets = mutableListOf<DataAsset>()
    private val assetList = mutableListOf<DataAsset>()
    private lateinit var assetsController: AssetsController

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(BT2, "onCreate")

        personAdapter = PersonAdapter(personList)
        val personController = PersonController(personAdapter)

        assetAdapter = AssetAdapter(assetList) { asset -> onAssetSelected(asset) }

        assetsController = AssetsController(assetAdapter)


        binding.layoutPeople.visibility = View.VISIBLE
        binding.layoutAssets.visibility = View.GONE
        Log.d(BT2, "View mặc định")

        binding.radioGroupPeopleAndAsset.setOnCheckedChangeListener { _, checkid ->
            when (checkid) {
                R.id.radiobuttonPeople -> {
                    binding.layoutPeople.visibility = View.VISIBLE
                    binding.layoutAssets.visibility = View.GONE
                    Log.d(BT2, PERSONVIEW)
                }
                R.id.radiobuttonAsset -> {
                    binding.layoutPeople.visibility = View.GONE
                    binding.layoutAssets.visibility = View.VISIBLE
                    Log.d(BT2, ASSETVIEW)
                }
            }
        }

        val peopleRecyclerView = findViewById<RecyclerView>(R.id.peopleRecyclerView)
        peopleRecyclerView.layoutManager = LinearLayoutManager(this)
        personAdapter = PersonAdapter(personList)
        peopleRecyclerView.adapter = personAdapter

        val assetsRecyclerView = findViewById<RecyclerView>(R.id.assetsRecyclerView)
        assetsRecyclerView.layoutManager = LinearLayoutManager(this)
        assetAdapter = AssetAdapter(assetList) { asset ->
            onAssetSelected(asset)
        }
        assetsRecyclerView.adapter = assetAdapter

        // Thêm tài sản
        binding.buttonAddAsset.setOnClickListener {
            val assetName = binding.editTextAsset.text.toString()
            val assetValueText = binding.editTextValueAsset.text.toString()

            if (assetsController.addAsset(assetName, assetValueText)) {

                assetAdapter.notifyDataSetChanged()

                binding.editTextAsset.text.clear()
                binding.editTextValueAsset.text.clear()
                Toast.makeText(this, "Thêm $assetName thành công", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Thêm tài sản thất bại", Toast.LENGTH_SHORT).show()
            }
        }

        // Thêm người
        binding.buttonAddPeople.setOnClickListener {
            val personName = binding.editTextPeople.text.toString()

            // Kiểm tra nếu chưa có tài sản
            if (selectedAssets.isEmpty()) {
                // Hiển thị cảnh báo
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Cảnh báo")
                builder.setMessage("Bắt buộc phải thêm tài sản cho con người")
                builder.setPositiveButton("Đóng") { dialog, _ ->
                    dialog.dismiss() // Đóng hộp thoại khi người dùng bấm "Đóng"
                }
                val alertDialog = builder.create()
                alertDialog.show()
            } else {
                // Thêm người nếu đã có tài sản
                if (personController.addPerson(personName, selectedAssets)) {
                    personAdapter.notifyDataSetChanged()

                    // Xóa các trường nhập và danh sách tài sản
                    binding.editTextPeople.text.clear()
                    selectedAssets.clear()
                    binding.textViewListAssets.text = "Tài sản: "

                    Toast.makeText(this, "Thêm người thành công", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Tên người không được để trống", Toast.LENGTH_SHORT).show()
                }
            }
        }

//        val dialogView = layoutInflater.inflate(R.layout.dialog_custom_alert, null)
//        val builder = AlertDialog.Builder(this)
//        builder.setView(dialogView)
//
//         // Hiển thị hộp thoại
//        val alertDialog = builder.create()
//        alertDialog.show()
//
//        // Thiết lập hành vi nút "Đóng"
//        val buttonClose = dialogView.findViewById<Button>(R.id.buttonClose)
//        buttonClose.setOnClickListener {
//            alertDialog.dismiss() // Đóng hộp thoại
//        }

        binding.personResult.setOnClickListener {
            val intentMain = Intent(this@MainActivity, ResultsScreen::class.java)
            startActivity(intentMain)
            Log.d(BT2, "Chuyển sang màn hình kết quả")
        }

        binding.assetResults.setOnClickListener {
            val intentMain = Intent(this@MainActivity, ResultsScreen::class.java)
            startActivity(intentMain)
            Log.d(BT2, "Chuyển sang màn hình kết quả")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onAssetSelected(asset: DataAsset) {
        if (!selectedAssets.contains(asset)) {
            selectedAssets.add(asset)
            binding.textViewListAssets.text = "Tài sản: " + selectedAssets.joinToString {it.name}
        }
    }

    companion object {
        private const val BT2 = "baitap2"
        private const val PERSONVIEW = "View Con người"
        private const val ASSETVIEW = "View Tài sản"
    }
}
