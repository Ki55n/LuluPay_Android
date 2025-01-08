package com.sdk.lulupay.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sdk.lulupay.R
import com.sdk.lulupay.database.LuluPayDB
import com.sdk.lulupay.listeners.*
import com.sdk.lulupay.model.response.*
import com.sdk.lulupay.remittance.Remittance
import com.sdk.lulupay.session.SessionManager
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

data class Country(val name: String, val code: String, val currency: String)

class AddNewReceipient : AppCompatActivity() {

  private lateinit var dialog: AlertDialog

  private lateinit var getDetailsBtn: Button

  private lateinit var luluPayDB: LuluPayDB

  // Sender Details
  private lateinit var senderNameEdittext: EditText
  private lateinit var senderPhoneNoEdittext: EditText
  private lateinit var senderAddressType: Spinner
  private lateinit var senderAddressLine: EditText
  private lateinit var senderTownName: EditText

  // Receivers Details
  private lateinit var receiverFirstNameEdittext: EditText
  private lateinit var receiverMiddleNameEdittext: EditText
  private lateinit var receiverLastNameEdittext: EditText
  private lateinit var receiverPhoneNoEdittext: EditText
  private lateinit var receiverCountrySpinner: Spinner
  private lateinit var receiverReceivingMode: Spinner
  private lateinit var receiverBankSpinner: Spinner
  private lateinit var receiverAcctType: Spinner
  private lateinit var receiverAcctNoEdittext: EditText

  // Transaction Details
  private lateinit var instrument: Spinner
  private lateinit var selectPurpose: Spinner
  private lateinit var paymentMode: Spinner
  private lateinit var sourceOfIncome: Spinner

  // Selected Spinner Details
  private var selectedReceivingModeCode: String = ""
  private var selectedBank: String = ""
  private var selectedCountryName: String = ""
  private var selectedCountryCode: String = ""
  private var selectedAddressTypeSender: String = ""
  private var selectedSenderAddressCountry: String = ""
  private var selectedSenderAddressCountryCode: String = ""
  private var selectedReceiverAcctType: String = ""
  private var selectedInstrument: String = ""
  private var selectedPurposeOfTXN: String = ""
  private var selectedPaymentMode: String = ""
  private var selectedSourceOfIncome: String = ""

  // List CodeNames to use in onItemSelectedListener
  private lateinit var receivingModeList: List<CodeName>
  private lateinit var addressTypeListSender: List<CodeName>
  private lateinit var acctTypeList: List<CodeName>
  private lateinit var instrumentList: List<CodeName>
  private lateinit var purposeOfTXNList: List<CodeName>
  private lateinit var paymentModeList: List<CodeName>
  private lateinit var sourceOfIncomeList: List<CodeName>
  private lateinit var bankList: List<BankInfo>

  // Details will be use to construct a remittance payload
  private var sendingCountryCode: String = ""
  private var type: String = ""
  private var receivingMode: String = ""
  private var receivingCountryCode: String = ""
  private var limitMinAmount: BigDecimal = BigDecimal.ZERO
  private var limitPerTransaction: BigDecimal = BigDecimal.ZERO
  private var sendMinAmount: BigDecimal = BigDecimal.ZERO
  private var sendMaxAmount: BigDecimal = BigDecimal.ZERO
  private var correspondent: String = ""
  private var sendingCurrencyCode: String = ""
  private var receivingCurrencyCode: String = ""
  private var correspondentName: String = ""
  private var bankId: String = ""
  private var branchId: String = ""
  private var branchName: String = ""
  private var routingCode: String = ""
  private var isoCode: String = ""
  private var sort: String = ""
  private var bankName: String = ""
  private var bankBranchName: String = ""
  private var ifsc: String = ""
  private var bic: String = ""
  private var address: String = ""
  private var townName: String = ""
  private var countrySubdivision: String = ""
  private var countryCode: String = ""

  private val countries =
      listOf(
          Country("Choose Country", "", ""),
          Country("Pakistan", "PK", "PKR"),
          Country("India", "IN", "INR"),
          Country("Egypt", "EG", "EGP"),
          Country("China", "CN", "CNY"),
          Country("Sri Lanka", "LK", "LKR"),
          Country("Philippines", "PH", "PHP"))

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.add_new_receipient)

    luluPayDB = LuluPayDB(this)

    setupViews()
  }

  private fun setupViews() {
    getDetailsBtn = findViewById(R.id.get_details_btn)

    senderNameEdittext = findViewById(R.id.sender_name)
    senderPhoneNoEdittext = findViewById(R.id.sender_phone_no)
    senderAddressType = findViewById(R.id.address_type)
    senderAddressLine = findViewById(R.id.address_line)
    senderTownName = findViewById(R.id.town_name)

    receiverFirstNameEdittext = findViewById(R.id.receiver_first_name)
    receiverMiddleNameEdittext = findViewById(R.id.receiver_middle_name)
    receiverLastNameEdittext = findViewById(R.id.receiver_last_name)
    receiverPhoneNoEdittext = findViewById(R.id.receiver_phone_no)
    receiverCountrySpinner = findViewById(R.id.receiver_country)
    receiverReceivingMode = findViewById(R.id.receiver_mode)
    receiverBankSpinner = findViewById(R.id.receiver_bank)
    receiverAcctType = findViewById(R.id.receiver_acct_type)
    receiverAcctNoEdittext = findViewById(R.id.receiver_acct_no)

    instrument = findViewById(R.id.instrument)
    selectPurpose = findViewById(R.id.select_purpose)
    paymentMode = findViewById(R.id.payment_mode)
    sourceOfIncome = findViewById(R.id.source_of_income)

    addCountriesSpinner()

    registerClickListeners()
    registerSpinnerListeners()
    showDialogProgress()
    getReceiveingModes()
    getPurposeOfTXN()
    getPaymentMode()
    getSourceOfIncome()
    getAccountType()
    getAddressType()
    showMessage("Fetching required data please wait!")
  }

  private fun registerSpinnerListeners() {
    receiverCountrySpinner.onItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
          override fun onItemSelected(
              parent: AdapterView<*>,
              view: View?,
              position: Int,
              id: Long
          ) {
            val selectedCountry = countries[position]
            selectedCountryName = selectedCountry.name
            selectedCountryCode = selectedCountry.code
            val currencyCode = selectedCountry.currency

            if (!selectedReceivingModeCode.isNullOrEmpty()) {
              showDialogProgress()
              getBankList(selectedCountryCode)
              getServiceCorridor()
            }
          }

          override fun onNothingSelected(parent: AdapterView<*>) {
            // Handle the case where nothing is selected
          }
        }

    receiverReceivingMode.onItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
          override fun onItemSelected(
              parent: AdapterView<*>,
              view: View?,
              position: Int,
              id: Long
          ) {
            val selectedMode = receivingModeList[position]

            // Get the code
            selectedReceivingModeCode = selectedMode.code

            if (!selectedCountryName.isNullOrEmpty()) {
              showDialogProgress()
              getBankList(selectedCountryCode)
              getServiceCorridor()
            }
          }

          override fun onNothingSelected(parent: AdapterView<*>) {
            // Handle case where nothing is selected
          }
        }

    receiverBankSpinner.onItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
          override fun onItemSelected(
              parent: AdapterView<*>,
              view: View?,
              position: Int,
              id: Long
          ) {
            val selected = bankList[position]

            // Get the code
            selectedBank = selected.bank_id
            showDialogProgress()
            getBankById()
          }

          override fun onNothingSelected(parent: AdapterView<*>) {
            // Handle case where nothing is selected
          }
        }

    senderAddressType.onItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
          override fun onItemSelected(
              parent: AdapterView<*>,
              view: View?,
              position: Int,
              id: Long
          ) {
            val selected = addressTypeListSender[position]

            // Get the code
            selectedAddressTypeSender = selected.code
          }

          override fun onNothingSelected(parent: AdapterView<*>) {
            // Handle case where nothing is selected
          }
        }

    receiverAcctType.onItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
          override fun onItemSelected(
              parent: AdapterView<*>,
              view: View?,
              position: Int,
              id: Long
          ) {
            val selected = acctTypeList[position]

            // Get the code
            selectedReceiverAcctType = selected.code
          }

          override fun onNothingSelected(parent: AdapterView<*>) {
            // Handle case where nothing is selected
          }
        }

    instrument.onItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
          override fun onItemSelected(
              parent: AdapterView<*>,
              view: View?,
              position: Int,
              id: Long
          ) {
            val selected = instrumentList[position]

            // Get the code
            selectedInstrument = selected.code
          }

          override fun onNothingSelected(parent: AdapterView<*>) {
            // Handle case where nothing is selected
          }
        }

    selectPurpose.onItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
          override fun onItemSelected(
              parent: AdapterView<*>,
              view: View?,
              position: Int,
              id: Long
          ) {
            val selected = purposeOfTXNList[position]

            // Get the code
            selectedPurposeOfTXN = selected.code
          }

          override fun onNothingSelected(parent: AdapterView<*>) {
            // Handle case where nothing is selected
          }
        }

    paymentMode.onItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
          override fun onItemSelected(
              parent: AdapterView<*>,
              view: View?,
              position: Int,
              id: Long
          ) {
            val selected = paymentModeList[position]

            // Get the code
            selectedPaymentMode = selected.code
          }

          override fun onNothingSelected(parent: AdapterView<*>) {
            // Handle case where nothing is selected
          }
        }

    sourceOfIncome.onItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
          override fun onItemSelected(
              parent: AdapterView<*>,
              view: View?,
              position: Int,
              id: Long
          ) {
            val selected = sourceOfIncomeList[position]

            // Get the code
            selectedSourceOfIncome = selected.code
          }

          override fun onNothingSelected(parent: AdapterView<*>) {
            // Handle case where nothing is selected
          }
        }
  }

  private fun showViews() {
    if (receiverBankSpinner.visibility == View.GONE) {
      receiverBankSpinner.visibility = View.VISIBLE
    }

    if (receiverAcctNoEdittext.visibility == View.GONE) {
      receiverAcctNoEdittext.visibility = View.VISIBLE
    }

    if (receiverAcctType.visibility == View.GONE) {
      receiverAcctType.visibility = View.VISIBLE
    }

    if (instrument.visibility == View.GONE) {
      instrument.visibility = View.VISIBLE
    }

    if (selectPurpose.visibility == View.GONE) {
      selectPurpose.visibility = View.VISIBLE
    }

    if (paymentMode.visibility == View.GONE) {
      paymentMode.visibility = View.VISIBLE
    }

    if (sourceOfIncome.visibility == View.GONE) {
      sourceOfIncome.visibility = View.VISIBLE
    }
  }

  private fun getBankList(countryCode: String) {
    lifecycleScope.launch {
      withContext(Dispatchers.IO) {
        Remittance.getBankMaster(
            countryCode = countryCode,
            receivingMode = selectedReceivingModeCode,
            partnerName = senderNameEdittext.text.toString(),
            object : BankMasterListener {
              override fun onSuccess(response: MasterBankResponse) {
                addBanksSpinner(response)
                showViews()
              }

              override fun onFailed(errorMessage: String) {
              lifecycleScope.launch(Dispatchers.Main) {
                showMessage("Error: $errorMessage")
                }
              }
            })
      }
    }
  }

  private fun getBankById() {
    lifecycleScope.launch {
      withContext(Dispatchers.IO) {
        Remittance.getBranchMaster(
            receiving_country_code = selectedCountryCode,
            receiving_mode = selectedReceivingModeCode,
            correspondent = correspondent,
            bankId = selectedBank,
            partnerName = senderNameEdittext.text.toString(),
            object : BranchMasterListener {
              override fun onSuccess(response: BankBranchResponse) {
                dismissDialogProgress()
                sortBankByIdResponse(response)
              }

              override fun onFailed(errorMessage: String) {
              lifecycleScope.launch(Dispatchers.Main) {
                dismissDialogProgress()
                showMessage("Error: $errorMessage")
                }
              }
            })
      }
    }
  }

  private fun getServiceCorridor() {
    lifecycleScope.launch {
      withContext(Dispatchers.IO) {
        Remittance.getServiceCorridor(
            partnerName = senderNameEdittext.text.toString(),
            receiving_mode = selectedReceivingModeCode,
            receiving_country_code = selectedCountryCode,
            listener =
                object : ServiceCorridorListener {
                  override fun onSuccess(response: ServiceCorridorResponse) {
                    sortServiceCorridorResponse(response)
                  }

                  override fun onFailed(errorMessage: String) {
                  lifecycleScope.launch(Dispatchers.Main) {
                    dismissDialogProgress()
                    showMessage(errorMessage)
                    }
                  }
                })
      }
    }
  }

  private fun getReceiveingModes() {
    lifecycleScope.launch {
      withContext(Dispatchers.IO) {
        Remittance.getReceivingModes(
            senderNameEdittext.text.toString(),
            object : ReceiveModeListener {
              override fun onSuccess(response: CodeResponse) {
                addReceivingModeSpinner(response)
              }

              override fun onFailed(errorMessage: String) {
              lifecycleScope.launch(Dispatchers.Main) {
                dismissDialogProgress()
                showMessage(errorMessage)
                }
              }
            })
      }
    }
  }

  private fun getPurposeOfTXN() {
    lifecycleScope.launch {
      withContext(Dispatchers.IO) {
        Remittance.getPurposeOfTXN(
            senderNameEdittext.text.toString(),
            object : PurposeOfTXNListener {
              override fun onSuccess(response: CodeResponse) {
                addPurposeOfTXNSpinner(response)
              }

              override fun onFailed(errorMessage: String) {
              lifecycleScope.launch(Dispatchers.Main) {
                showMessage(errorMessage)
                dismissDialogProgress()
                }
              }
            })
      }
    }
  }

  private fun getPaymentMode() {
    lifecycleScope.launch {
      withContext(Dispatchers.IO) {
        Remittance.getPaymentMode(
            senderNameEdittext.text.toString(),
            object : PaymentModeListener {
              override fun onSuccess(response: CodeResponse) {
                addPaymentModeSpinner(response)
              }

              override fun onFailed(errorMessage: String) {
              lifecycleScope.launch(Dispatchers.Main) {
                dismissDialogProgress()
                showMessage(errorMessage)
                }
              }
            })
      }
    }
  }

  private fun getSourceOfIncome() {
    lifecycleScope.launch {
      withContext(Dispatchers.IO) {
        Remittance.getSourceOfIncome(
            senderNameEdittext.text.toString(),
            object : SourceOfIncomeListener {
              override fun onSuccess(response: CodeResponse) {
                addSourceOfIncomeSpinner(response)
              }

              override fun onFailed(errorMessage: String) {
              lifecycleScope.launch(Dispatchers.Main) {
                dismissDialogProgress()
                showMessage(errorMessage)
                }
              }
            })
      }
    }
  }

  private fun getCorrespondent() {
    lifecycleScope.launch {
      withContext(Dispatchers.IO) {
        Remittance.getCorrespondent(
            senderNameEdittext.text.toString(),
            object : CorrespondentListener {
              override fun onSuccess(response: CodeResponse) {
                // addCorrespondentSpinner(response)
              }

              override fun onFailed(errorMessage: String) {
              lifecycleScope.launch(Dispatchers.Main) {
                dismissDialogProgress()
                showMessage(errorMessage)
                }
              }
            })
      }
    }
  }

  private fun getAccountType() {
    lifecycleScope.launch {
    withContext(Dispatchers.IO) {
        Remittance.getAccountType(
            senderNameEdittext.text.toString(),
            object : AccountTypeListener {
                override fun onSuccess(response: CodeResponse) {
                    addAcctTypeSpinner(response)
                }

                override fun onFailed(errorMessage: String) {
                    lifecycleScope.launch(Dispatchers.Main) {
                    dismissDialogProgress()
                        showMessage(errorMessage) // Show toast or any UI-related action
                    }
                }
            })
    }
}
  }

  private fun getAddressType() {
    lifecycleScope.launch {
      withContext(Dispatchers.IO) {
        Remittance.getAddressType(
            senderNameEdittext.text.toString(),
            object : AddressTypeListener {
              override fun onSuccess(response: CodeResponse) {
                addAddressTypeSpinnerSender(response)
              }

              override fun onFailed(errorMessage: String) {
              lifecycleScope.launch(Dispatchers.Main) {
                dismissDialogProgress()
                showMessage(errorMessage)
                }
              }
            })
      }
    }
  }

  private fun getInstruments() {
    lifecycleScope.launch {
      withContext(Dispatchers.IO) {
        Remittance.getInstruments(
            senderNameEdittext.text.toString(),
            object : InstrumentListener {
              override fun onSuccess(response: CodeResponse) {
                addInstrumentSpinner(response)
                dismissDialogProgress()
              }

              override fun onFailed(errorMessage: String) {
              lifecycleScope.launch(Dispatchers.Main) {
                dismissDialogProgress()
                showMessage(errorMessage)
                }
              }
            })
      }
    }
  }

  private fun registerClickListeners() {
    getDetailsBtn.setOnClickListener {
      val senderName: String = senderNameEdittext.text.toString()
      val senderPhoneNo: String = senderPhoneNoEdittext.text.toString()
      val senderAddressLineValue: String = senderAddressLine.text.toString()
      val senderTownNameValue: String = senderTownName.text.toString()
      val receiverFirstName: String = receiverFirstNameEdittext.text.toString()
      val receiverMiddleName: String = receiverMiddleNameEdittext.text.toString()
      val receiverLastName: String = receiverLastNameEdittext.text.toString()
      val receiverPhoneNo: String = receiverPhoneNoEdittext.text.toString()
      val receiverAcctNo: String = receiverAcctNoEdittext.text.toString()

      if (!senderName.isNullOrEmpty()) {
        if (!senderPhoneNo.isNullOrEmpty()) {
          if (isValidPhoneNumber(senderPhoneNo)) {
            if (!selectedAddressTypeSender.isNullOrEmpty()) {
              if (!senderAddressLineValue.isNullOrEmpty()) {
                if (!senderTownNameValue.isNullOrEmpty()) {
                  if (!receiverFirstName.isNullOrEmpty()) {
                    if (!receiverMiddleName.isNullOrEmpty()) {
                      if (!receiverLastName.isNullOrEmpty()) {
                        if (!receiverPhoneNo.isNullOrEmpty()) {
                          if (isValidPhoneNumber(receiverPhoneNo)) {
                            if (!receiverAcctNo.isNullOrEmpty()) {
                              if (!selectedCountryCode.isNullOrEmpty()) {
                                if (!selectedReceivingModeCode.isNullOrEmpty()) {
                                  if (!selectedBank.isNullOrEmpty()) {
                                    if (!selectedReceiverAcctType.isNullOrEmpty()) {
                                      if (!selectedInstrument.isNullOrEmpty()) {
                                        if (!selectedPurposeOfTXN.isNullOrEmpty()) {
                                          if (!selectedPaymentMode.isNullOrEmpty()) {
                                            if (!selectedSourceOfIncome.isNullOrEmpty()) {
                                              SessionManager.partnerName = senderName
                                              showDialogProgress()
                                              showMessage("Validating Inputs")
                                              validateAccount(
                                                  receiverFirstName,
                                                  receiverMiddleName,
                                                  receiverLastName,
                                                  receiverAcctNo)
                                            } else {
                                              showMessage("Source Of Income is required!")
                                            }
                                          } else {
                                            showMessage("Payment Mode is required!")
                                          }
                                        } else {
                                          showMessage("Purpose Of Transaction is required!")
                                        }
                                      } else {
                                        showMessage("Instrument is required!")
                                      }
                                    } else {
                                      showMessage("Receiver Account Type is required!")
                                    }
                                  } else {
                                    showMessage("Receiver Bank is required!")
                                  }
                                } else {
                                  showMessage("Receiver Receiving Mode is required")
                                }
                              } else {
                                showMessage("Receiver Country is Required!")
                              }
                            } else {
                              showMessage("Receiver Account Number is required!")
                            }
                          } else {
                            showMessage("Receiver Phone Number is invalid!")
                          }
                        } else {
                          showMessage("Receiver Phone Number is required")
                        }
                      } else {
                        showMessage("Receiver Last Name is required!")
                      }
                    } else {
                      showMessage("Receiver Middle Name is required!")
                    }
                  } else {
                    showMessage("Receiver First Name is required!")
                  }
                } else {
                  showMessage("Sender Bank Address Town Name is required!")
                }
              } else {
                showMessage("Sender Bank Address Line is required!")
              }
            } else {
              showMessage("Sender Bank Address Type is required!")
            }
          } else {
            showMessage("Sender Phone Number is invalid!")
          }
        } else {
          showMessage("Sender Phone Number is required!")
        }
      } else {
        showMessage("Sender name is required!")
      }
    }
  }

  fun isValidPhoneNumber(phoneNo: String): Boolean {
    val phoneNumber = phoneNo
    val phoneNumberRegex = Regex("^(\\+|0)[0-9]{3,15}$")
    return phoneNumberRegex.matches(phoneNumber)
  }

  private fun addCountriesSpinner() {
    val adapter =
        ArrayAdapter(this, android.R.layout.simple_spinner_item, countries.map { it.name })
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    receiverCountrySpinner.adapter = adapter
  }

  private fun addReceivingModeSpinner(response: CodeResponse) {
    receivingModeList = response.data.receiving_modes
    val receivingModeName = mutableListOf("Choose Receive Mode") // Add the default first string
    receivingModeName.addAll(receivingModeList.map { it.name }) // Append the rest of the items

    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, receivingModeName)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    receiverReceivingMode.adapter = adapter
  }

  private fun addBanksSpinner(response: MasterBankResponse) {
    bankList = response.data.list

    // Map the list to bank names for the spinner
    val bankNames = mutableListOf("Choose a Bank") // Add the default first string
    bankNames.addAll(bankList.map { it.bank_name }) // Append the rest of the items

    // Set up the Spinner with the list of bank names
    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bankNames)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    receiverBankSpinner.adapter = adapter
  }

  private fun sortServiceCorridorResponse(response: ServiceCorridorResponse) {
    // Assuming you need to process the first item in the list
    val remittanceDetail = response.data.firstOrNull() ?: return // Safely handle empty list

    sendingCountryCode = remittanceDetail.sending_country_code
    type = remittanceDetail.transaction_type
    receivingMode = remittanceDetail.receiving_mode
    receivingCountryCode = remittanceDetail.receiving_country_code
    limitMinAmount = remittanceDetail.limit_min_amount
    limitPerTransaction = remittanceDetail.limit_per_transaction
    sendMinAmount = remittanceDetail.send_min_amount
    sendMaxAmount = remittanceDetail.send_max_amount

    val corridorCurrency = remittanceDetail.corridor_currencies.firstOrNull()
    correspondent = corridorCurrency?.correspondent ?: "Unknown"
    sendingCurrencyCode = corridorCurrency?.sending_currency_code ?: "Unknown"
    receivingCurrencyCode = corridorCurrency?.receiving_currency_code ?: "Unknown"
    correspondentName = corridorCurrency?.correspondent_name ?: "Unknown"
  }

  private fun sortBankByIdResponse(response: BankBranchResponse) {
    val bankData = response.data.list.firstOrNull() ?: return
    bankId = bankData.bank_id
    branchId = bankData.branch_id
    branchName = bankData.branch_name
    routingCode = bankData.routing_code ?: ""
    isoCode = bankData.iso_code ?: ""
    sort = bankData.sort ?: ""
    bankName = bankData.bank_name
    bankBranchName = bankData.bank_branch_name ?: ""
    ifsc = bankData.ifsc ?: ""
    bic = bankData.bic
    address = bankData.address ?: ""
    townName = bankData.town_name ?: ""
    countrySubdivision = bankData.country_subdivision ?: ""
    countryCode = bankData.country_code ?: ""
  }

  private fun addPurposeOfTXNSpinner(response: CodeResponse) {
    purposeOfTXNList = response.data.purposes_of_transactions
    val purposeOfTXNName = mutableListOf("Choose Purpose Of Transaction") // Default string
    purposeOfTXNName.addAll(purposeOfTXNList.map { it.name }) // Append the rest

    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, purposeOfTXNName)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    selectPurpose.adapter = adapter
  }

  private fun addPaymentModeSpinner(response: CodeResponse) {
    paymentModeList = response.data.payment_modes
    val paymentModeName = mutableListOf("Choose Payment Mode") // Default string
    paymentModeName.addAll(paymentModeList.map { it.name }) // Append the rest

    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentModeName)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    paymentMode.adapter = adapter
  }

  private fun addSourceOfIncomeSpinner(response: CodeResponse) {
    sourceOfIncomeList = response.data.sources_of_incomes
    val sourceOfIncomeName = mutableListOf("Choose Source Of Income") // Default string
    sourceOfIncomeName.addAll(sourceOfIncomeList.map { it.name }) // Append the rest

    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sourceOfIncomeName)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    sourceOfIncome.adapter = adapter
  }

  private fun addAcctTypeSpinner(response: CodeResponse) {
    acctTypeList = response.data.account_types
    val acctTypeName = mutableListOf("Choose Account Type") // Default string
    acctTypeName.addAll(acctTypeList.map { it.name }) // Append the rest

    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, acctTypeName)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    receiverAcctType.adapter = adapter
  }

  private fun addAddressTypeSpinnerSender(response: CodeResponse) {
    addressTypeListSender = response.data.address_types
    val addressTypeName = mutableListOf("Choose Address Type") // Default string
    addressTypeName.addAll(addressTypeListSender.map { it.name }) // Append the rest

    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, addressTypeName)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    senderAddressType.adapter = adapter
  }

  private fun addInstrumentSpinner(response: CodeResponse) {
    instrumentList = response.data.instruments
    val instrumentName = mutableListOf("Choose an instrument") // Default string
    instrumentName.addAll(instrumentList.map { it.name }) // Append the rest

    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, instrumentName)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    instrument.adapter = adapter
  }

  private fun validateAccount(
      firstName: String,
      middleName: String,
      lastName: String,
      accountNo: String
  ) {
    lifecycleScope.launch {
      withContext(Dispatchers.IO) {
        Remittance.validateAccount(
            partnerName = senderNameEdittext.text.toString(),
            correspondent = correspondent,
            receiving_country_code = receivingCountryCode,
            receiving_mode = receivingMode,
            iso_code = isoCode,
            routing_code = Integer.parseInt(routingCode),
            sort_code = sort,
            iban = "",
            bank_id = bankId,
            branch_id = branchId,
            first_name = firstName,
            middle_name = middleName,
            last_name = lastName,
            account_number = accountNo,
            object : ValidateAccountListener {
              override fun onSuccess(response: AccountValidationResponse) {
                sortValidateAccountResponse(response)
              }

              override fun onFailed(errorMessage: String) {
                dismissDialogProgress()
                showMessage(errorMessage)
              }
            })
      }
    }
  }

  private fun sortValidateAccountResponse(response: AccountValidationResponse) {
	  if(response.status == "failure" || response.status_code >= 400){
		  showMessage(response.message ?: "")
		  return
	  }
	  
	  val intent = Intent(this, InputScreen::class.java)
	  startActivity(intent)
  }

  private fun addSession(
      username: String,
      password: String,
      grantType: String,
      clientId: String,
      scope: String,
      clientSecret: String
  ) {
    SessionManager.username = username
    SessionManager.password = password
    SessionManager.grantType = grantType
    SessionManager.clientId = clientId
    SessionManager.scope = scope
    SessionManager.clientSecret = clientSecret
  }

  private fun redirectToLoginScreen() {
    val intent = Intent(this, LoginScreen::class.java)
    startActivity(intent)
    finish()
  }

  private fun saveRecipient(
      senderName: String,
      channelName: String,
      branchCode: String,
      companyCode: String
  ) {
    lifecycleScope.launch {
      try {
        withContext(Dispatchers.IO) {
          // Perform the database operation in the IO context
          luluPayDB.insertData(senderName, channelName, branchCode, companyCode)
        }
        // Show success message on the main thread
        showMessage("Recipient saved successfully!")
      } catch (e: Exception) {
        // Handle and show the error message
        showMessage(e.message ?: "An unexpected error occurred")
      }
    }
  }

  private fun showMessage(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
  }

  private fun showDialogProgress() {

    // Build the AlertDialog
    dialog =
        AlertDialog.Builder(this)
            .setView(R.layout.custom_dialog) // Set custom layout as the dialog's content
            .create()

    // Show the dialog
    dialog.show()
  }

  private fun dismissDialogProgress() {
    if (::dialog.isInitialized) {
      dialog.dismiss()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    // Close the database when the activity is destroyed
    luluPayDB.closeDatabase()
  }
}
