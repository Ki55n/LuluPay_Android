package com.sdk.lulupay.bottomsheet

import android.os.Bundle
import android.widget.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import com.sdk.lulupay.R
import androidx.appcompat.app.AlertDialog
import com.sdk.lulupay.listeners.*
import com.sdk.lulupay.model.response.*
import  com.sdk.lulupay.recyclerView.*
import com.sdk.lulupay.remittance.Remittance
import kotlinx.coroutines.launch

class BottomSheetLookupsFragment : BottomSheetDialogFragment() {
    companion object {
        private const val ARG_RECEIVING_MODE = "receiving_mode"
        private const val ARG_SENDER = "sender"
        private const val ARG_COUNTRY = "country"

        fun newInstance(receivingMode: String, sender: String, country: String): BottomSheetLookupsFragment {
            val fragment = BottomSheetLookupsFragment()
            val args = Bundle()
            args.putString(ARG_RECEIVING_MODE, receivingMode)
            args.putString(ARG_SENDER, sender)
            args.putString(ARG_COUNTRY, country)
            fragment.arguments = args
            return fragment
        }
    }
    
    private lateinit var sortCodeEdittext: EditText
    private lateinit var routingCodeEdittext: EditText
    private lateinit var swiftCodeEdittext: EditText
    private lateinit var buttonSearch: MaterialButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BankDetailsAdapter
    
    private var receivingMode = ""
    private var sender = ""
    private var country = ""
    
    private var bankName: String = ""
private var routingCode: String = ""
private var bicSwift: String = ""
private var sortCode: String = ""
private var address: String = ""
private var townName: String = ""
private var countrySubdivision: String = ""
private var bankId: String = ""
private var branchId: String = ""
private var branchName: String = ""
private var branchFullName: String = ""
private var countryCode: String = ""
private var ifsc: String = ""
private var bic: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_lookups, container, false)
        
        sortCodeEdittext = view.findViewById(R.id.sort_code_bottom_sheet)
    routingCodeEdittext = view.findViewById(R.id.routing_code_bottom_sheet)
    swiftCodeEdittext = view.findViewById(R.id.bic_swift_code)
    buttonSearch = view.findViewById(R.id.button_search)
    recyclerView = view.findViewById(R.id.recycler_view_bottom_sheet)
        
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set RecyclerView with a LinearLayoutManager and Adapter
        receivingMode = arguments?.getString(ARG_RECEIVING_MODE) ?: ""
        sender = arguments?.getString(ARG_SENDER) ?: ""
        country = arguments?.getString(ARG_COUNTRY) ?: ""
        
        setClickListener()
    }
    
    private fun setClickListener(){
    buttonSearch.setOnClickListener{
    if(!sortCodeEdittext.text.toString().isNullOrBlank() || !swiftCodeEdittext.text.toString().isNullOrBlank() || !routingCodeEdittext.text.toString().isNullOrBlank()){
    showDialogProgress()
    branchLookup(sortCodeEdittext.text.toString(), routingCodeEdittext.text.toString(), swiftCodeEdittext.text.toString())
    }else{
    showMessage("Sort Code Or Routing Code Or Swift Code is required to perform bank search!")
    }
    }
    }
    
    private fun branchLookup(sort_code: String, routing_code: String, swift_code: String){
    lifecycleScope.launch {
    Remittance.branchLookup(
            sortCode = sort_code,
            routingCode = routing_code,
            swiftCode = swift_code,
            partnerName = sender,
            receivingCountryCode = country,
            receivingMode = receivingMode,
            object : BranchLookupListener {
              override fun onSuccess(response: BranchSearchResponse) {
              dismissDialogProgress()
              
              if(response.status.equals("failure") || response.status_code >= 400){
              showMessage(response.message ?: "Bank not found")
              }else{
                    response.data?.list?.let { branchDetails ->
                        (requireActivity().supportFragmentManager.findFragmentByTag("BottomSheet") as? BottomSheetLookupsFragment)?.setupRecyclerView(branchDetails)
                        }
                    }
              }

              override fun onFailed(errorMessage: String) {
                dismissDialogProgress()
                showMessage(errorMessage)
              }
            })
    }
    }
    
    fun setupRecyclerView(branchDetails: List<BranchDetails>) {
        val bankDetails = branchDetails.map {
            BankDetail(
                bankName = it.bank_name ?: "",
                routingCode = it.routing_code ?: "",
                bicSwift = it.bic ?: "",
                sortCode = it.sort,
                address = it.address,
                townName = it.town_name ?: "",
                countrySubdivision = it.country_subdivision ?: ""
            )
        }

        adapter = BankDetailsAdapter(bankDetails) { position, bankDetail ->
            val selectedBranch = branchDetails[position]
            // Retrieve the desired data
            bankId = selectedBranch.bank_id
            branchId = selectedBranch.branch_id
            branchName = selectedBranch.branch_name
            branchFullName = selectedBranch.branch_full_name
            countryCode = selectedBranch.country_code
            ifsc = selectedBranch.ifsc ?: ""
            bic = selectedBranch.bic ?: ""
            
            bankName = selectedBranch.bank_name ?: ""
        routingCode = selectedBranch.routing_code ?: ""
        bicSwift = selectedBranch.bic ?: ""
        sortCode = selectedBranch.sort
        address = selectedBranch.address
        townName = selectedBranch.town_name ?: ""
        countrySubdivision = selectedBranch.country_subdivision ?: ""
            
            destroyFragment()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }
    
    private fun addRecyclerViewAdapter(response: BranchSearchResponse){
    
    }
    
    private lateinit var dialog: AlertDialog

private fun showDialogProgress() {
    // Build the AlertDialog
    dialog = AlertDialog.Builder(requireContext()) // Use requireContext() to get the context
        .setView(R.layout.custom_dialog) // Set custom layout as the dialog's content
        .setCancelable(false) // Disable back button dismiss
        .create()

    // Prevent dialog from dismissing on outside touch
    dialog.setCanceledOnTouchOutside(false)

    // Show the dialog
    dialog.show()
}

private fun dismissDialogProgress() {
    if (::dialog.isInitialized && dialog.isShowing) {
        dialog.dismiss()
    }
}

private fun destroyFragment(){
val fragment = childFragmentManager.findFragmentByTag("BottomSheet")
if (fragment != null) {
    childFragmentManager.beginTransaction()
        .remove(fragment) // Removes the fragment
        .commit() // Apply the transaction
}
}

private fun showMessage(message: String) {
    Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
  }
  
  override fun onDestroyView() {
    super.onDestroyView()
    (activity as? BottomSheetListener)?.onBottomSheetDismissed(
            bankId = bankId,
            branchId = branchId,
            branchName = branchName,
            branchFullName = branchFullName,
            countryCode = countryCode,
            ifsc = ifsc,
            bic = bic,
            bankName = bankName,
            routingCode = routingCode,
            swiftCode = bicSwift,
            sortCode = sortCode,
            address = address,
            townName = townName,
            countrySubdivision = countrySubdivision
        )
}
}