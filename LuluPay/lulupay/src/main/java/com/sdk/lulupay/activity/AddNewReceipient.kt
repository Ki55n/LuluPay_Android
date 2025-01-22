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
import com.google.android.material.button.MaterialButton
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import com.sdk.lulupay.bottomsheet.*

data class Country(val name: String, val code: String, val currency: String)

class AddNewReceipient : AppCompatActivity(), BottomSheetListener {

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
  private lateinit var receiverIbanEdittext: EditText
  private lateinit var receiverAcctNoEdittext: EditText
  private lateinit var searchBankButton: MaterialButton
  private lateinit var receiverRoutingCodeEdittext: EditText
  private lateinit var receiverIsoCodeEdittext: EditText
  private lateinit var receiverAddressEdittext: EditText
  private lateinit var receiverTownNameEdittext: EditText
  private lateinit var receiverCountrySubdivisionEdittext: EditText

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
  private var iban: String = ""
  private var bankName: String = ""
  private var bankBranchName: String = ""
  private var ifsc: String = ""
  private var bic: String = ""
  private var address: String = ""
  private var townName: String = ""
  private var countrySubdivision: String = ""
  private var countryCode: String = ""
  
  private var countryPosition: Int = 0
  private var receivingPosition: Int  = 0

  private val countries = listOf(
    Country("Choose Country", "", ""),
    Country("Afghanistan", "AF", "AFN"),
    Country("Albania", "AL", "ALL"),
    Country("Algeria", "DZ", "DZD"),
    Country("Andorra", "AD", "EUR"),
    Country("Angola", "AO", "AOA"),
    Country("Antigua and Barbuda", "AG", "XCD"),
    Country("Argentina", "AR", "ARS"),
    Country("Armenia", "AM", "AMD"),
    Country("Australia", "AU", "AUD"),
    Country("Austria", "AT", "EUR"),
    Country("Azerbaijan", "AZ", "AZN"),
    Country("Bahamas", "BS", "BSD"),
    Country("Bahrain", "BH", "BHD"),
    Country("Bangladesh", "BD", "BDT"),
    Country("Barbados", "BB", "BBD"),
    Country("Belarus", "BY", "BYN"),
    Country("Belgium", "BE", "EUR"),
    Country("Belize", "BZ", "BZD"),
    Country("Benin", "BJ", "XOF"),
    Country("Bhutan", "BT", "BTN"),
    Country("Bolivia", "BO", "BOB"),
    Country("Bosnia and Herzegovina", "BA", "BAM"),
    Country("Botswana", "BW", "BWP"),
    Country("Brazil", "BR", "BRL"),
    Country("Brunei", "BN", "BND"),
    Country("Bulgaria", "BG", "BGN"),
    Country("Burkina Faso", "BF", "XOF"),
    Country("Burundi", "BI", "BIF"),
    Country("Cabo Verde", "CV", "CVE"),
    Country("Cambodia", "KH", "KHR"),
    Country("Cameroon", "CM", "XAF"),
    Country("Canada", "CA", "CAD"),
    Country("Central African Republic", "CF", "XAF"),
    Country("Chad", "TD", "XAF"),
    Country("Chile", "CL", "CLP"),
    Country("China", "CN", "CNY"),
    Country("Colombia", "CO", "COP"),
    Country("Comoros", "KM", "KMF"),
    Country("Congo (Congo-Brazzaville)", "CG", "XAF"),
    Country("Congo (DRC)", "CD", "CDF"),
    Country("Costa Rica", "CR", "CRC"),
    Country("Croatia", "HR", "HRK"),
    Country("Cuba", "CU", "CUP"),
    Country("Cyprus", "CY", "EUR"),
    Country("Czechia", "CZ", "CZK"),
    Country("Denmark", "DK", "DKK"),
    Country("Djibouti", "DJ", "DJF"),
    Country("Dominica", "DM", "XCD"),
    Country("Dominican Republic", "DO", "DOP"),
    Country("Ecuador", "EC", "USD"),
    Country("Egypt", "EG", "EGP"),
    Country("El Salvador", "SV", "USD"),
    Country("Equatorial Guinea", "GQ", "XAF"),
    Country("Eritrea", "ER", "ERN"),
    Country("Estonia", "EE", "EUR"),
    Country("Eswatini", "SZ", "SZL"),
    Country("Ethiopia", "ET", "ETB"),
    Country("Fiji", "FJ", "FJD"),
    Country("Finland", "FI", "EUR"),
    Country("France", "FR", "EUR"),
    Country("Gabon", "GA", "XAF"),
    Country("Gambia", "GM", "GMD"),
    Country("Georgia", "GE", "GEL"),
    Country("Germany", "DE", "EUR"),
    Country("Ghana", "GH", "GHS"),
    Country("Greece", "GR", "EUR"),
    Country("Grenada", "GD", "XCD"),
    Country("Guatemala", "GT", "GTQ"),
    Country("Guinea", "GN", "GNF"),
    Country("Guinea-Bissau", "GW", "XOF"),
    Country("Guyana", "GY", "GYD"),
    Country("Haiti", "HT", "HTG"),
    Country("Honduras", "HN", "HNL"),
    Country("Hungary", "HU", "HUF"),
    Country("Iceland", "IS", "ISK"),
    Country("India", "IN", "INR"),
    Country("Indonesia", "ID", "IDR"),
    Country("Iran", "IR", "IRR"),
    Country("Iraq", "IQ", "IQD"),
    Country("Ireland", "IE", "EUR"),
    Country("Israel", "IL", "ILS"),
    Country("Italy", "IT", "EUR"),
    Country("Jamaica", "JM", "JMD"),
    Country("Japan", "JP", "JPY"),
    Country("Jordan", "JO", "JOD"),
    Country("Kazakhstan", "KZ", "KZT"),
    Country("Kenya", "KE", "KES"),
    Country("Kiribati", "KI", "AUD"),
    Country("Kuwait", "KW", "KWD"),
    Country("Kyrgyzstan", "KG", "KGS"),
    Country("Laos", "LA", "LAK"),
    Country("Latvia", "LV", "EUR"),
    Country("Lebanon", "LB", "LBP"),
    Country("Lesotho", "LS", "LSL"),
    Country("Liberia", "LR", "LRD"),
    Country("Libya", "LY", "LYD"),
    Country("Liechtenstein", "LI", "CHF"),
    Country("Lithuania", "LT", "EUR"),
    Country("Luxembourg", "LU", "EUR"),
    Country("Madagascar", "MG", "MGA"),
    Country("Malawi", "MW", "MWK"),
    Country("Malaysia", "MY", "MYR"),
    Country("Maldives", "MV", "MVR"),
    Country("Mali", "ML", "XOF"),
    Country("Malta", "MT", "EUR"),
    Country("Marshall Islands", "MH", "USD"),
    Country("Mauritania", "MR", "MRU"),
    Country("Mauritius", "MU", "MUR"),
    Country("Mexico", "MX", "MXN"),
    Country("Micronesia", "FM", "USD"),
    Country("Moldova", "MD", "MDL"),
    Country("Monaco", "MC", "EUR"),
    Country("Mongolia", "MN", "MNT"),
    Country("Montenegro", "ME", "EUR"),
    Country("Morocco", "MA", "MAD"),
    Country("Mozambique", "MZ", "MZN"),
    Country("Myanmar", "MM", "MMK"),
    Country("Namibia", "NA", "NAD"),
    Country("Nauru", "NR", "AUD"),
    Country("Nepal", "NP", "NPR"),
    Country("Netherlands", "NL", "EUR"),
    Country("New Zealand", "NZ", "NZD"),
    Country("Nicaragua", "NI", "NIO"),
    Country("Niger", "NE", "XOF"),
    Country("Nigeria", "NG", "NGN"),
    Country("North Korea", "KP", "KPW"),
    Country("North Macedonia", "MK", "MKD"),
    Country("Norway", "NO", "NOK"),
    Country("Oman", "OM", "OMR"),
    Country("Pakistan", "PK", "PKR"),
    Country("Palau", "PW", "USD"),
    Country("Palestine", "PS", "ILS"),
    Country("Panama", "PA", "PAB"),
    Country("Papua New Guinea", "PG", "PGK"),
    Country("Paraguay", "PY", "PYG"),
    Country("Peru", "PE", "PEN"),
    Country("Philippines", "PH", "PHP"),
    Country("Poland", "PL", "PLN"),
    Country("Portugal", "PT", "EUR"),
    Country("Qatar", "QA", "QAR"),
    Country("Romania", "RO", "RON"),
    Country("Russia", "RU", "RUB"),
    Country("Rwanda", "RW", "RWF"),
    Country("Saint Kitts and Nevis", "KN", "XCD"),
    Country("Saint Lucia", "LC", "XCD"),
    Country("Saint Vincent and the Grenadines", "VC", "XCD"),
    Country("Samoa", "WS", "WST"),
    Country("San Marino", "SM", "EUR"),
    Country("Sao Tome and Principe", "ST", "STN"),
    Country("Saudi Arabia", "SA", "SAR"),
    Country("Senegal", "SN", "XOF"),
    Country("Serbia", "RS", "RSD"),
    Country("Seychelles", "SC", "SCR"),
    Country("Sierra Leone", "SL", "SLL"),
    Country("Singapore", "SG", "SGD"),
    Country("Slovakia", "SK", "EUR"),
    Country("Slovenia", "SI", "EUR"),
    Country("Solomon Islands", "SB", "SBD"),
    Country("Somalia", "SO", "SOS"),
    Country("South Africa", "ZA", "ZAR"),
    Country("South Korea", "KR", "KRW"),
    Country("South Sudan", "SS", "SSP"),
    Country("Spain", "ES", "EUR"),
    Country("Sri Lanka", "LK", "LKR"),
    Country("Sudan", "SD", "SDG"),
    Country("Suriname", "SR", "SRD"),
    Country("Sweden", "SE", "SEK"),
    Country("Switzerland", "CH", "CHF"),
    Country("Syria", "SY", "SYP"),
    Country("Taiwan", "TW", "TWD"),
    Country("Tajikistan", "TJ", "TJS"),
    Country("Tanzania", "TZ", "TZS"),
    Country("Thailand", "TH", "THB"),
    Country("Timor-Leste", "TL", "USD"),
    Country("Togo", "TG", "XOF"),
    Country("Tonga", "TO", "TOP"),
    Country("Trinidad and Tobago", "TT", "TTD"),
    Country("Tunisia", "TN", "TND"),
    Country("Turkey", "TR", "TRY"),
    Country("Turkmenistan", "TM", "TMT"),
    Country("Tuvalu", "TV", "AUD"),
    Country("Uganda", "UG", "UGX"),
    Country("Ukraine", "UA", "UAH"),
    Country("United Arab Emirates", "AE", "AED"),
    Country("United Kingdom", "GB", "GBP"),
    Country("United States", "US", "USD"),
    Country("Uruguay", "UY", "UYU"),
    Country("Uzbekistan", "UZ", "UZS"),
    Country("Vanuatu", "VU", "VUV"),
    Country("Vatican City", "VA", "EUR"),
    Country("Venezuela", "VE", "VES"),
    Country("Vietnam", "VN", "VND"),
    Country("Yemen", "YE", "YER"),
    Country("Zambia", "ZM", "ZMW"),
    Country("Zimbabwe", "ZW", "ZWL")
)

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
    receiverIbanEdittext = findViewById(R.id.receiver_iban)
    receiverAcctNoEdittext = findViewById(R.id.receiver_acct_no)
    searchBankButton = findViewById(R.id.button_search1)
    receiverRoutingCodeEdittext = findViewById(R.id.receiver_routing_code)
    receiverIsoCodeEdittext = findViewById(R.id.receiver_iso_code)
    receiverAddressEdittext = findViewById(R.id.receiver_address)
    receiverTownNameEdittext = findViewById(R.id.receiver_town_name)
    receiverCountrySubdivisionEdittext = findViewById(R.id.receiver_country_subdivision)

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
    getInstruments()
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
          countryPosition = position
            val selectedCountry = countries[position]
            selectedCountryName = selectedCountry.name
            selectedCountryCode = selectedCountry.code
            val currencyCode = selectedCountry.currency
            hideViews()

            if (!selectedReceivingModeCode.isNullOrBlank() && !selectedCountryName.equals("Choose Country") && receivingPosition != 0) {
              showDialogProgress()
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
        
        receivingPosition = position
        hideViews()
            if (position in receivingModeList.indices) { // Ensure valid position
                val selectedMode = receivingModeList[position]
                selectedReceivingModeCode = selectedMode.code

                if (!selectedCountryName.equals("Choose Country") && !selectedReceivingModeCode.isNullOrBlank() && countryPosition != 0) {
                    showDialogProgress()
                    getServiceCorridor()
                }
            }
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
            if (position == 0) {
                // Default item selected, clear selection
                selectedAddressTypeSender = ""
            } else if (position - 1 in addressTypeListSender.indices) {
                // Adjust for the "Choose Address Type" offset
                val selected = addressTypeListSender[position - 1]
                selectedAddressTypeSender = selected.code
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            selectedAddressTypeSender = ""
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
            if (position in acctTypeList.indices) { // Ensure valid position
                val selected = acctTypeList[position]
                selectedReceiverAcctType = selected.code
            }
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
            if (position in instrumentList.indices) { // Ensure valid position
                val selected = instrumentList[position]
                selectedInstrument = selected.code
            }
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
            if (position in purposeOfTXNList.indices) { // Ensure valid position
                val selected = purposeOfTXNList[position]
                selectedPurposeOfTXN = selected.code
            }
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
            if (position in paymentModeList.indices) { // Ensure valid position
                val selected = paymentModeList[position]
                selectedPaymentMode = selected.code
            }
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
            if (position in sourceOfIncomeList.indices) { // Ensure valid position
                val selected = sourceOfIncomeList[position]
                selectedSourceOfIncome = selected.code
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            // Handle case where nothing is selected
        }
    }
  }

  private fun showViews() {
  if(selectedCountryCode.equals("EG") || selectedCountryCode.equals("PK") || selectedCountryCode.equals("UK") || selectedCountryCode.equals("AE")){
  
  if (receiverAcctNoEdittext.visibility == View.VISIBLE) {
      receiverAcctNoEdittext.visibility = View.GONE
    }
    
    if (receiverIbanEdittext.visibility == View.GONE) {
      receiverIbanEdittext.visibility = View.VISIBLE
    }
  
  }else{
  if (receiverAcctNoEdittext.visibility == View.GONE) {
      receiverAcctNoEdittext.visibility = View.VISIBLE
    }
    
    if (receiverIbanEdittext.visibility == View.VISIBLE) {
      receiverIbanEdittext.visibility = View.GONE
    }
  }
    if (receiverBankSpinner.visibility == View.GONE) {
      receiverBankSpinner.visibility = View.VISIBLE
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
  
  private fun hideViews() {
   if (receiverAcctNoEdittext.visibility == View.VISIBLE) {
      receiverAcctNoEdittext.visibility = View.GONE
    }
    
    if (receiverIbanEdittext.visibility == View.VISIBLE) {
      receiverIbanEdittext.visibility = View.GONE
    }
    
    if (receiverBankSpinner.visibility == View.VISIBLE) {
      receiverBankSpinner.visibility = View.GONE
    }

    if (receiverAcctType.visibility == View.VISIBLE) {
      receiverAcctType.visibility = View.GONE
    }

    if (instrument.visibility == View.VISIBLE) {
      instrument.visibility = View.GONE
    }

    if (selectPurpose.visibility == View.VISIBLE) {
      selectPurpose.visibility = View.GONE
    }

    if (paymentMode.visibility == View.VISIBLE) {
      paymentMode.visibility = View.GONE
    }

    if (sourceOfIncome.visibility == View.VISIBLE) {
      sourceOfIncome.visibility = View.GONE
    }
  }

  private fun getBankList(countryCode: String) {
    lifecycleScope.launch {
        Remittance.getBankMaster(
            countryCode = selectedCountryCode,
            receivingMode = selectedReceivingModeCode,
            partnerName = senderNameEdittext.text.toString(),
            object : BankMasterListener {
              override fun onSuccess(response: MasterBankResponse) {
                addBanksSpinner(response)
              }

              override fun onFailed(errorMessage: String) {
                showMessage("Error: $errorMessage")
                hideViews()
              }
            })
    }
  }

  /*private fun getBankById() {
    lifecycleScope.launch {
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
                dismissDialogProgress()
                showMessage("Error: $errorMessage")
              }
            })
    }
  }*/

  private fun getServiceCorridor() {
    lifecycleScope.launch {
        Remittance.getServiceCorridor(
            partnerName = senderNameEdittext.text.toString(),
            receiving_mode = selectedReceivingModeCode,
            receiving_country_code = selectedCountryCode,
            listener =
                object : ServiceCorridorListener {
                  override fun onSuccess(response: ServiceCorridorResponse) {
                    sortServiceCorridorResponse(response)
                    dismissDialogProgress()
                    showViews()
                  }

                  override fun onFailed(errorMessage: String) {
                    dismissDialogProgress()
                    showMessage(errorMessage)
                    hideViews()
                  }
                })
    }
  }

  private fun getReceiveingModes() {
    lifecycleScope.launch {
        Remittance.getReceivingModes(
            senderNameEdittext.text.toString(),
            object : ReceiveModeListener {
              override fun onSuccess(response: CodeResponse) {
                addReceivingModeSpinner(response)
              }

              override fun onFailed(errorMessage: String) {
                dismissDialogProgress()
                showMessage(errorMessage)
              }
            })
    }
  }

  private fun getPurposeOfTXN() {
    lifecycleScope.launch {
        Remittance.getPurposeOfTXN(
            senderNameEdittext.text.toString(),
            object : PurposeOfTXNListener {
              override fun onSuccess(response: CodeResponse) {
                addPurposeOfTXNSpinner(response)
              }

              override fun onFailed(errorMessage: String) {
                showMessage(errorMessage)
                dismissDialogProgress()
              }
            })
    }
  }

  private fun getPaymentMode() {
    lifecycleScope.launch {
        Remittance.getPaymentMode(
            senderNameEdittext.text.toString(),
            object : PaymentModeListener {
              override fun onSuccess(response: CodeResponse) {
                addPaymentModeSpinner(response)
              }

              override fun onFailed(errorMessage: String) {
                dismissDialogProgress()
                showMessage(errorMessage + "3")
              }
            })
      }
    }

  private fun getSourceOfIncome() {
    lifecycleScope.launch {
        Remittance.getSourceOfIncome(
            senderNameEdittext.text.toString(),
            object : SourceOfIncomeListener {
              override fun onSuccess(response: CodeResponse) {
                addSourceOfIncomeSpinner(response)
              }

              override fun onFailed(errorMessage: String) {
                dismissDialogProgress()
                showMessage(errorMessage + "2")
              }
            })
    }
  }

  private fun getCorrespondent() {
    lifecycleScope.launch {
        Remittance.getCorrespondent(
            senderNameEdittext.text.toString(),
            object : CorrespondentListener {
              override fun onSuccess(response: CodeResponse) {
                // addCorrespondentSpinner(response)
              }

              override fun onFailed(errorMessage: String) {
                dismissDialogProgress()
                showMessage(errorMessage)
              }
            })
    }
  }

  private fun getAccountType() {
    lifecycleScope.launch {
        Remittance.getAccountType(
            senderNameEdittext.text.toString(),
            object : AccountTypeListener {
                override fun onSuccess(response: CodeResponse) {
                    addAcctTypeSpinner(response)
                }

                override fun onFailed(errorMessage: String) {
                    dismissDialogProgress()
                        showMessage(errorMessage) // Show toast or any UI-related action
                }
            })
}
  }

  private fun getAddressType() {
    lifecycleScope.launch {
        Remittance.getAddressType(
            senderNameEdittext.text.toString(),
            object : AddressTypeListener {
              override fun onSuccess(response: CodeResponse) {
                addAddressTypeSpinnerSender(response)
              }

              override fun onFailed(errorMessage: String) {
                dismissDialogProgress()
                showMessage(errorMessage)
              }
            })
    }
  }

  private fun getInstruments() {
    lifecycleScope.launch {
        Remittance.getInstruments(
            senderNameEdittext.text.toString(),
            object : InstrumentListener {
              override fun onSuccess(response: CodeResponse) {
                addInstrumentSpinner(response)
                dismissDialogProgress()
              }

              override fun onFailed(errorMessage: String) {
                dismissDialogProgress()
                showMessage(errorMessage + "1")
              }
            })
    }
  }
  
  private fun routingCodeLookup(routing_code: String){
  lifecycleScope.launch {
  
  }
  }
  
  private fun isoCodeLookup(iso_code: String){
  lifecycleScope.launch {
  
  }
  }
  
  private fun sortCodeLookup(sort: String){
  lifecycleScope.launch {
  
  }
  }

  private fun registerClickListeners() {
  searchBankButton.setOnClickListener{
  if(!selectedReceivingModeCode.isNullOrBlank()){
  if(!selectedCountryCode.isNullOrBlank()){
  if(!senderNameEdittext.text.toString().isNullOrBlank()){
  val bottomSheet = BottomSheetLookupsFragment.newInstance(receivingMode = selectedReceivingModeCode,
  sender = senderNameEdittext.text.toString(),
  country = selectedCountryCode)
bottomSheet.show(supportFragmentManager, "BottomSheet")
}else{
showMessage("Sender Name is required!")
}
}else{
showMessage("Receiver Country is required!")
}
}else{
showMessage("Receiving Mode is required!")
}
  }
  
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
      val receiverIban: String = receiverIbanEdittext.text.toString()
      
      

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
                            if (!receiverAcctNo.isNullOrEmpty() || !receiverIban.isNullOrEmpty()) {
                              if (!selectedCountryCode.isNullOrEmpty()) {
                                if (!selectedReceivingModeCode.isNullOrEmpty()) {
                                  if (!selectedBank.isNullOrEmpty()) {
                                    if (!selectedReceiverAcctType.isNullOrBlank()) {
                                      if (!selectedInstrument.isNullOrEmpty()) {
                                        if (!selectedPurposeOfTXN.isNullOrEmpty()) {
                                          if (!selectedPaymentMode.isNullOrEmpty()) {
                                            if (!selectedSourceOfIncome.isNullOrEmpty()) {
                                            if(!routingCode.isNullOrEmpty()){
                                            if(!isoCode.isNullOrEmpty()){
                                            if(!sort.isNullOrEmpty()){
                                            if(!address.isNullOrEmpty() || !receiverAddressEdittext.text.toString().isNullOrEmpty()){
                                            if(!townName.isNullOrEmpty() || !receiverTownNameEdittext.text.toString().isNullOrEmpty()){
                                            if(!countrySubdivision.isNullOrEmpty() || !receiverCountrySubdivisionEdittext.text.toString().isNullOrEmpty()){
                                              SessionManager.partnerName = senderName
                                              iban = receiverIbanEdittext.text.toString() ?: ""
                                              
                                              if(address.isNullOrBlank() || address.equals(".")){
                                              address = receiverAddressEdittext.text.toString()
                                              }
                                              
                                              if(townName.isNullOrBlank() || townName.equals(".")){
                                              townName = receiverTownNameEdittext.text.toString()
                                              }
                                              
                                              if(countrySubdivision.isNullOrBlank() || countrySubdivision.equals(".")){
                                              countrySubdivision = receiverCountrySubdivisionEdittext.text.toString()
                                              }
                                              showDialogProgress()
                                              showMessage("Validating Inputs")
                                              validateAccount(
                                                  receiverFirstName,
                                                  receiverMiddleName,
                                                  receiverLastName,
                                                  receiverAcctNo)
                                                  }else{
                                                  showMessage("Receiver Resident Country Subdivision is required!")
                                                  }
                                                  }else{
                                                  showMessage("Receiver Resident Town Name is required!")
                                                  }
                                                  }else{
                                                  showMessage("Receiver Resident Address Line is required!")
                                                  }
                                                  }else{
                                                  showMessage("Receiver Sort or Swift Code is required!")
                                                  }
                                                  }else{
                                                  showMessage("Receiver Iso Code is required!")
                                                  }
                                                  }else{
                                                  showMessage("Receiver Routing Code is required!")
                                                  }
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
                              showMessage("Receiver Account Number or IBAN is required!")
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
    countryCode = bankData.country_code ?: receivingCountryCode
    
    showMessage(routingCode)
    showMessage(isoCode)
    
    if(routingCode.isNullOrEmpty() || routingCode.isNullOrBlank() || routingCode.equals(".") || routingCode.equals("-")){
    if (receiverRoutingCodeEdittext.visibility == View.GONE) {
      receiverRoutingCodeEdittext.visibility = View.VISIBLE
    }
    }else{
    if (receiverRoutingCodeEdittext.visibility == View.VISIBLE) {
      receiverRoutingCodeEdittext.visibility = View.GONE
    }
    }
    
    if(isoCode.isNullOrEmpty() || isoCode.isNullOrBlank() || isoCode.equals(".") || isoCode.equals("-")){
    if (receiverIsoCodeEdittext.visibility == View.GONE) {
      receiverIsoCodeEdittext.visibility = View.VISIBLE
    }
    }else{
    if (receiverIsoCodeEdittext.visibility == View.VISIBLE) {
      receiverIsoCodeEdittext.visibility = View.GONE
    }
    }
    
   /* if(sort.isNullOrEmpty() || sort.isNullOrBlank() || sort.equals(".") || sort.equals("-")){
    if (receiverSortCodeEdittext.visibility == View.GONE) {
      receiverSortCodeEdittext.visibility = View.VISIBLE
    }
    }else{
    if (receiverSortCodeEdittext.visibility == View.VISIBLE) {
      receiverSortCodeEdittext.visibility = View.GONE
    }
    }*/
    
    if(address.isNullOrEmpty() || address.isNullOrBlank() || address.equals(".") || address.equals("-")){
    if (receiverAddressEdittext.visibility == View.GONE) {
      receiverAddressEdittext.visibility = View.VISIBLE
    }
    }else{
    if (receiverAddressEdittext.visibility == View.VISIBLE) {
      receiverAddressEdittext.visibility = View.GONE
    }
    }
    
    if(townName.isNullOrEmpty() || townName.isNullOrBlank() || townName.equals(".") || townName.equals("-")){
    if (receiverTownNameEdittext.visibility == View.GONE) {
      receiverTownNameEdittext.visibility = View.VISIBLE
    }
    }else{
    if (receiverTownNameEdittext.visibility == View.VISIBLE) {
      receiverTownNameEdittext.visibility = View.GONE
    }
    }
    
    if(countrySubdivision.isNullOrEmpty() || countrySubdivision.isNullOrBlank() || countrySubdivision.equals(".") || countrySubdivision.equals("-")){
    if (receiverCountrySubdivisionEdittext.visibility == View.GONE) {
      receiverCountrySubdivisionEdittext.visibility = View.VISIBLE
    }
    }else{
    if (receiverCountrySubdivisionEdittext.visibility == View.VISIBLE) {
      receiverCountrySubdivisionEdittext.visibility = View.GONE
    }
    }
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
    if(receivingCountryCode.equals("UK") || receivingCountryCode.equals("EG") || receivingCountryCode.equals("PK") || receivingCountryCode.equals("AE")){
    Remittance.validateAccount(
            partnerName = senderNameEdittext.text.toString(),
            correspondent = correspondent,
            receiving_country_code = receivingCountryCode,
            receiving_mode = receivingMode,
            iso_code = isoCode,
            routing_code = null,
            sort_code = null,
            iban = iban,
            bank_id = bankId,
            branch_id = branchId,
            first_name = firstName,
            middle_name = middleName,
            last_name = lastName,
            account_number = null,
            object : ValidateAccountListener {
              override fun onSuccess(response: AccountValidationResponse) {
                sortValidateAccountResponse(response)
              }

              override fun onFailed(errorMessage: String) {
                dismissDialogProgress()
                showMessage(errorMessage)
              }
            })
    }else{
        Remittance.validateAccount(
            partnerName = senderNameEdittext.text.toString(),
            correspondent = correspondent,
            receiving_country_code = receivingCountryCode,
            receiving_mode = receivingMode,
            iso_code = isoCode,
            routing_code = null,
            sort_code = null,
            iban = null,
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
      if(response.message?.contains("Lookup on any one of the request parameter[iso_code, routing_code, sort_code") ?: false){
      showMessage("Check and verify your iso code or routing code or sort code")
      return
      }else{
		  showMessage(response.message ?: " An error occured")
		  return
          }
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
    dialog = AlertDialog.Builder(this)
        .setView(R.layout.custom_dialog) // Set custom layout as the dialog's content
        .setCancelable(false) // Disable back button dismiss
        .create()

    // Prevent dialog from dismissing on outside touch
    dialog.setCanceledOnTouchOutside(false)

    // Show the dialog
    dialog.show()
}

  private fun dismissDialogProgress() {
    if (dialog.isShowing == true) {
      dialog.dismiss()
    }
  }
  
  override fun onBottomSheetDismissed(
    bankId: String,
    branchId: String,
    branchName: String,
    branchFullName: String,
    countryCode: String,
    ifsc: String,
    bic: String,
    bankName: String,
    routingCode: String,
    swiftCode: String,
    sortCode: String,
    address: String,
    townName: String,
    countrySubdivision: String
) {
    receiverRoutingCodeEdittext.setText(routingCode)
receiverIsoCodeEdittext.setText(swiftCode)
receiverAddressEdittext.setText(address)
receiverTownNameEdittext.setText(townName)
receiverCountrySubdivisionEdittext.setText(countrySubdivision)


this.bankId = bankId
this.branchId = branchId
this.branchName = branchName
this.routingCode = routingCode
this.isoCode = swiftCode
this.sort = sortCode
this.bankName = bankName
this.bankBranchName = branchFullName
this.ifsc = ifsc 
this.bic = bic
this.address = address
this.townName = townName
this.countrySubdivision = countrySubdivision
this.countryCode = countryCode

if(address.isNullOrEmpty() || address.isNullOrBlank() || address.equals(".") || address.equals("-")){
    if (receiverAddressEdittext.visibility == View.GONE) {
      receiverAddressEdittext.visibility = View.VISIBLE
    }
    }else{
    if (receiverAddressEdittext.visibility == View.VISIBLE) {
      receiverAddressEdittext.visibility = View.GONE
    }
    }
    
    if(townName.isNullOrEmpty() || townName.isNullOrBlank() || townName.equals(".") || townName.equals("-")){
    if (receiverTownNameEdittext.visibility == View.GONE) {
      receiverTownNameEdittext.visibility = View.VISIBLE
    }
    }else{
    if (receiverTownNameEdittext.visibility == View.VISIBLE) {
      receiverTownNameEdittext.visibility = View.GONE
    }
    }
    
    if(countrySubdivision.isNullOrEmpty() || countrySubdivision.isNullOrBlank() || countrySubdivision.equals(".") || countrySubdivision.equals("-")){
    if (receiverCountrySubdivisionEdittext.visibility == View.GONE) {
      receiverCountrySubdivisionEdittext.visibility = View.VISIBLE
    }
    }else{
    if (receiverCountrySubdivisionEdittext.visibility == View.VISIBLE) {
      receiverCountrySubdivisionEdittext.visibility = View.GONE
    }
    }
}

  override fun onDestroy() {
    super.onDestroy()
    // Close the database when the activity is destroyed
    luluPayDB.closeDatabase()
  }
}
