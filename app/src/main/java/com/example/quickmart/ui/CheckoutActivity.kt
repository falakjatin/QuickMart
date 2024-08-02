package com.example.quickmart.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quickmart.R
import com.example.quickmart.models.CartProductModel
import com.example.quickmart.models.FormModel
import com.example.quickmart.models.UserModel
import com.example.quickmart.utils.ValidationUtil
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Objects

class CheckoutActivity : AppCompatActivity() {
    private var firstNameLt: TextInputLayout? = null
    private var lastNameLt: TextInputLayout? = null
    private var emailLt: TextInputLayout? = null
    private var mobileNumbLt: TextInputLayout? = null
    private var streetAddrLt: TextInputLayout? = null
    private var cityLt: TextInputLayout? = null
    private var postalLt: TextInputLayout? = null
    private var provinceLt: TextInputLayout? = null
    private var cardHolderLt: TextInputLayout? = null
    private var cardNumbLt: TextInputLayout? = null
    private var expDateLt: TextInputLayout? = null
    private var cvvLt: TextInputLayout? = null

    private var etFirstName: TextInputEditText? = null
    private var etLastName: TextInputEditText? = null
    private var etMobileNo: TextInputEditText? = null
    private var etEmail: TextInputEditText? = null
    private var etStreetAddr: TextInputEditText? = null
    private var etCity: TextInputEditText? = null
    private var etPostal: TextInputEditText? = null
    private var etProvince: TextInputEditText? = null
    private var etCardHolder: TextInputEditText? = null
    private var etCardNumb: TextInputEditText? = null
    private var etExpDate: TextInputEditText? = null
    private var etCvv: TextInputEditText? = null

    private lateinit var form: Array<FormModel?>

    private lateinit var rgPaymentMode: RadioGroup
    private lateinit var btnPlaceOrder: Button
    lateinit var layoutCard: LinearLayout
    lateinit var toolbar: MaterialToolbar

    var auth: FirebaseAuth? = null
    var database: FirebaseDatabase? = null
    var userRef: DatabaseReference? = null
    var currentUser: FirebaseUser? = null
    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_out)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userRef = database!!.getReference("users")
        currentUser = auth!!.currentUser
        rgPaymentMode = findViewById(R.id.rgPaymentMode)

        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etMobileNo = findViewById(R.id.etMobileNo)
        etEmail = findViewById(R.id.etEmail)
        etStreetAddr = findViewById(R.id.etStreetName)
        etCity = findViewById(R.id.etCityName)
        etPostal = findViewById(R.id.etPostalCode)
        etProvince = findViewById(R.id.etProvince)
        etCardHolder = findViewById(R.id.etCardHolderName)
        etCardNumb = findViewById(R.id.etCardNumber)
        etExpDate = findViewById(R.id.etExpDate)
        etCvv = findViewById(R.id.etCardCvv)

        firstNameLt = findViewById(R.id.etFirstName_layout)
        lastNameLt = findViewById(R.id.etLastName_layout)
        mobileNumbLt = findViewById(R.id.etMobileNo_layout)
        emailLt = findViewById(R.id.etEmail_layout)
        streetAddrLt = findViewById(R.id.etStreetName_layout)
        cityLt = findViewById(R.id.etCityName_layout)
        postalLt = findViewById(R.id.etPostalCode_layout)
        provinceLt = findViewById(R.id.etProvince_layout)
        cardHolderLt = findViewById(R.id.etCardHolderName_layout)
        cardNumbLt = findViewById(R.id.etCardNumber_layout)
        expDateLt = findViewById(R.id.etExpDate_layout)
        cvvLt = findViewById(R.id.etCardCvv_layout)

        layoutCard = findViewById(R.id.layoutCard)
        toolbar = findViewById(R.id.toolbar)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        btnPlaceOrder.setOnClickListener {
            placeOrder()
        }

        rgPaymentMode.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.rbCOD) {
                layoutCard.visibility = View.GONE
                setFormForCOD()
            }
            if (checkedId == R.id.rbDebit) {
                layoutCard.visibility = View.VISIBLE
                setFormForCard()
            }
        }

        userData
    }

    private fun placeOrder() {
        showProgressDialog()

        // Prepare order details
        val orderDetails = HashMap<String, Any>()
        orderDetails["firstName"] = Objects.requireNonNull<Editable>(etFirstName?.text).toString()
        orderDetails["lastName"] = Objects.requireNonNull<Editable>(etLastName?.text).toString()
        orderDetails["mobileNumber"] = Objects.requireNonNull<Editable>(etMobileNo?.text).toString()
        orderDetails["email"] = Objects.requireNonNull<Editable>(etEmail?.text).toString()
        orderDetails["streetAddress"] = Objects.requireNonNull<Editable>(etStreetAddr?.text).toString()
        orderDetails["city"] = Objects.requireNonNull<Editable>(etCity?.text).toString()
        orderDetails["postalCode"] = Objects.requireNonNull<Editable>(etPostal?.text).toString()
        orderDetails["province"] = Objects.requireNonNull<Editable>(etProvince?.text).toString()

        if (rgPaymentMode.checkedRadioButtonId == R.id.rbDebit) {
            orderDetails["paymentMode"] = "Debit Card"
            orderDetails["cardHolderName"] = Objects.requireNonNull<Editable>(etCardHolder?.text).toString()
            orderDetails["cardNumber"] = Objects.requireNonNull<Editable>(etCardNumb?.text).toString()
            orderDetails["expiryDate"] = Objects.requireNonNull<Editable>(etExpDate?.text).toString()
            orderDetails["cvv"] = Objects.requireNonNull<Editable>(etCvv?.text).toString()
        } else {
            orderDetails["paymentMode"] = "Cash on Delivery"
        }

        // Retrieve cart items
        val cartList = intent.getSerializableExtra("cartItems") as ArrayList<CartProductModel>

        // Prepare order items
        val orderItems = ArrayList<HashMap<String, Any>>()
        for (cartProduct in cartList) {
            val itemDetails = HashMap<String, Any>()
            itemDetails["imageUrl"] = cartProduct.imageUrl ?: ""
            itemDetails["price"] = cartProduct.price ?: 0.0
            itemDetails["productName"] = cartProduct.name ?: ""
            itemDetails["quantity"] = cartProduct.quantity
            orderItems.add(itemDetails)
        }
        orderDetails["orderItems"] = orderItems

        // Save order details and clear cart
        val orderReference = database!!.getReference("orders")
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userOrderRef = orderReference.child(user.uid).push()
            userOrderRef.setValue(orderDetails).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Clear the cart after placing the order
                    clearCart(user.uid)
                    hideProgressDialog()
                    // Show success dialog and handle continuation
                    showSuccessDialog()
                } else {
                    hideProgressDialog()
                    Toast.makeText(this@CheckoutActivity, "Failed to place order", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            hideProgressDialog()
            Toast.makeText(this@CheckoutActivity, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private val userData: Unit
        get() {
            showProgressDialog()
            currentUser?.let {
                userRef?.child(it.uid)
                    ?.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            hideProgressDialog()
                            val user: UserModel? = snapshot.getValue(UserModel::class.java)
                            if (user != null) {
                                etEmail?.setText(user.email)
                                etFirstName?.setText(user.firstname)
                                etLastName?.setText(user.lastname)
                                etMobileNo?.setText(user.mobileno)
                                setFormForCard()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            hideProgressDialog()
                            Toast.makeText(
                                this@CheckoutActivity,
                                error.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }

    private fun clearCart(userId: String) {
        val cartReference = database!!.getReference("carts").child(userId)
        cartReference.removeValue()
    }

    private fun showProgressDialog() {
        progressDialog = Dialog(this)
        progressDialog!!.setContentView(R.layout.dialog_progress)
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun hideProgressDialog() {
        progressDialog?.dismiss()
    }

    private fun setFormForCOD() {
        form = arrayOfNulls(8)
        form[0] = FormModel("default", firstNameLt!!, Objects.requireNonNull<Editable>(etFirstName?.text).toString(), "", "")
        form[1] = FormModel("default", lastNameLt!!, Objects.requireNonNull<Editable>(etLastName?.text).toString(), "", "")
        form[2] = FormModel("email", emailLt!!, Objects.requireNonNull<Editable>(etEmail?.text).toString(), "", "")
        form[3] = FormModel("mobile", mobileNumbLt!!, Objects.requireNonNull<Editable>(etMobileNo?.text).toString(), "", "")
        form[4] = FormModel("default", streetAddrLt!!, Objects.requireNonNull<Editable>(etStreetAddr?.text).toString(), "", "")
        form[5] = FormModel("no_numeric_special", cityLt!!, Objects.requireNonNull<Editable>(etCity?.text).toString(), "Please enter a valid city name.", "")
        form[6] = FormModel("postal", postalLt!!, Objects.requireNonNull<Editable>(etPostal?.text).toString(), "", "")
        form[7] = FormModel("no_numeric_special", provinceLt!!, Objects.requireNonNull<Editable>(etProvince?.text).toString(), "Please enter a valid province name.", "")
    }

    private fun setFormForCard() {
        form = arrayOfNulls(12)
        form[0] = FormModel("default", firstNameLt!!, Objects.requireNonNull<Editable>(etFirstName?.text).toString(), "", "")
        form[1] = FormModel("default", lastNameLt!!, Objects.requireNonNull<Editable>(etLastName?.text).toString(), "", "")
        form[2] = FormModel("email", emailLt!!, Objects.requireNonNull<Editable>(etEmail?.text).toString(), "", "")
        form[3] = FormModel("mobile", mobileNumbLt!!, Objects.requireNonNull<Editable>(etMobileNo?.text).toString(), "", "")
        form[4] = FormModel("default", streetAddrLt!!, Objects.requireNonNull<Editable>(etStreetAddr?.text).toString(), "", "")
        form[5] = FormModel("no_numeric_special", cityLt!!, Objects.requireNonNull<Editable>(etCity?.text).toString(), "Please enter a valid city name.", "")
        form[6] = FormModel("postal", postalLt!!, Objects.requireNonNull<Editable>(etPostal?.text).toString(), "", "")
        form[7] = FormModel("no_numeric_special", provinceLt!!, Objects.requireNonNull<Editable>(etProvince?.text).toString(), "Please enter a valid province name.", "")
        form[8] = FormModel("default", cardHolderLt!!, Objects.requireNonNull<Editable>(etCardHolder?.text).toString(), "", "")
        form[9] = FormModel("default", cardNumbLt!!, Objects.requireNonNull<Editable>(etCardNumb?.text).toString(), "", "")
        form[10] = FormModel("default", expDateLt!!, Objects.requireNonNull<Editable>(etExpDate?.text).toString(), "", "")
        form[11] = FormModel("cvv", cvvLt!!, Objects.requireNonNull(etCvv?.text).toString(), "", "")
    }

    private fun showSuccessDialog() {
        val dialog = MaterialAlertDialogBuilder(this@CheckoutActivity)
        dialog.setTitle("Successful")
        dialog.setMessage("Order has been received and will be placed soon.")
        dialog.setCancelable(false)
        dialog.setPositiveButton("Continue Shopping") { dialog, which ->
            dialog.cancel()
            val homeIntent = Intent(this@CheckoutActivity, ProductActivity::class.java)
            startActivity(homeIntent)
            finish()
        }
        dialog.show()
    }
}
