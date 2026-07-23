package com.graphees.statspos.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.graphees.statspos.domain.models.DropdownItem
import com.graphees.statspos.utils.HP


@Composable
fun MOPSection(
    mop: DropdownItem,
    bank: DropdownItem,
    subBank: DropdownItem,
    onMOPChange: (DropdownItem) -> Unit,
    onBankSelected: (DropdownItem) -> Unit,
    onSubBankSelected: (DropdownItem) -> Unit,
) {
    ExpandableSection(
        title = "M.O.P Bank",
        initiallyExpanded = true,
    ) {
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.mop,
            selectedItem = mop,
            onItemSelected = onMOPChange,
            label = {
                Text("M.O.P")
            },
            placeholder = {
                PlaceHolder(text = "M.O.P")
            },
            outlined = true,
        )
        ComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.banks,
            selectedItem = bank,
            onItemSelected = onBankSelected,
            label = {
                Text("Bank")
            },
            placeholder = {
                PlaceHolder(text = "Bank")
            },
            outlined = true,
            addNone = true,
            enabled = mop.id == 2L,
        )
        SubComboBox(
            modifier = Modifier
                .fillMaxWidth(),
            items = HP.subBanks,
            selectedItem = subBank,
            onItemSelected = onSubBankSelected,
            label = {
                Text("Bank Account")
            },
            placeholder = {
                PlaceHolder(text = "Bank Account")
            },
            outlined = true,
            addNone = true,
            enabled = mop.id == 2L,
            mainId = bank.id
        )
    }
}
