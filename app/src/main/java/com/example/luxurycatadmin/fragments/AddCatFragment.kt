package com.example.luxurycatadmin.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.luxurycatadmin.R
import com.example.luxurycatadmin.adapter.AddCatImageAdapter
import com.example.luxurycatadmin.databinding.FragmentAddCatBinding
import com.example.luxurycatadmin.model.AddCatModel
import com.example.luxurycatadmin.model.CategoryModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList


class AddCatFragment : Fragment() {

    private lateinit var binding: FragmentAddCatBinding
    private lateinit var list : ArrayList<Uri>
    private lateinit var listImages : ArrayList<String>
    private lateinit var adapter : AddCatImageAdapter
    private var coverImage: Uri ? = null
    private lateinit var dialog : Dialog
    private var coverImageUrl : String? = ""
    private lateinit var categoryList: ArrayList<String>

    private var launchGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == Activity.RESULT_OK){
            coverImage = it.data!!.data
            binding.productCoverImg.setImageURI(coverImage)
            binding.productCoverImg.visibility = VISIBLE
        }
    }

    private var launchCatActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == Activity.RESULT_OK){
            val imageUrl = it.data!!.data
            list.add(imageUrl!!)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddCatBinding.inflate(layoutInflater)
        list = ArrayList()
        listImages = ArrayList()

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)

        binding.selectCoverImg.setOnClickListener{
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchGalleryActivity.launch(intent)
        }

        binding.catImgBtn.setOnClickListener{
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchCatActivity.launch(intent)
        }

        setCatCategory()

        adapter = AddCatImageAdapter(list)
        binding.productImgRecyclerView.adapter = adapter

        binding.submitCatBtn.setOnClickListener{
            validateData()
        }

        return binding.root
    }

    private fun validateData() {
        if (binding.catNameEdt.text.toString().isEmpty()) {
            binding.catNameEdt.requestFocus()
            binding.catNameEdt.error = "Empty"
        } else if (binding.catSpEdt.text.toString().isEmpty()) {
            binding.catSpEdt.requestFocus()
            binding.catSpEdt.error = "Empty"
        } else if (coverImage == null) {
            Toast.makeText(requireContext(), "Please select cover image", Toast.LENGTH_SHORT).show()
        } else if (list.size < 1) {
            Toast.makeText(requireContext(), "Please select cat images", Toast.LENGTH_SHORT).show()
        } else {
            uploadImage()
        }
    }

    private fun uploadImage() {
        dialog.show()

        val fileName = UUID.randomUUID().toString()+".jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("cats/$fileName")
        refStorage.putFile(coverImage!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener {image ->
                    coverImageUrl = image.toString()

                    uploadCatImage()
                }
            }
            .addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something went wrong with storage", Toast.LENGTH_SHORT).show()
            }
    }

    private var i = 0
    private fun uploadCatImage() {
        dialog.show()

        val fileName = UUID.randomUUID().toString()+".jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("cats/$fileName")
        refStorage.putFile(list[i]!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener {image ->
                    listImages.add(image!!.toString())
                    if (list.size == listImages.size){
                        storeData()
                    }else{
                        i += 1
                        uploadCatImage()
                    }
                }
            }
            .addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something went wrong with storage", Toast.LENGTH_SHORT).show()
            }
    }

    private fun storeData() {
        val db = Firebase.firestore.collection("cats")
        val key = db.document().id

        val data = AddCatModel(
            binding.catNameEdt.text.toString(),
            binding.catDescriptionEdt.text.toString(),
            coverImageUrl.toString(),
            categoryList[binding.catCategoryDropdown.selectedItemPosition],
            key,
            binding.catSrpEdt.text.toString(),
            binding.catSpEdt.text.toString(),
            listImages
        )
        db.document(key).set(data).addOnSuccessListener {
            dialog.dismiss()
            Toast.makeText(requireContext(), "Cat Added", Toast.LENGTH_SHORT).show()
            binding.catNameEdt.text = null
        }
            .addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            }
    }


    private fun setCatCategory() {
        categoryList = ArrayList()
        Firebase.firestore.collection("categories").get().addOnSuccessListener {
            categoryList.clear()
            for (doc in it.documents){
                val data = doc.toObject(CategoryModel::class.java)
                categoryList.add(data!!.cat!!)
            }
            categoryList.add(0, "Select Category")

            val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item_layout, categoryList)
            binding.catCategoryDropdown.adapter = arrayAdapter
        }
        
    }
}