package tcs.app.dev.homework1

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tcs.app.dev.R
import tcs.app.dev.homework1.data.Cart
import tcs.app.dev.homework1.data.Discount
import tcs.app.dev.homework1.data.MockData

@Composable
fun DiscountList(
    discounts: List<Discount>,
    cart: Cart,
    modifier: Modifier = Modifier,
    onAddDiscount: (Discount) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = discounts,
            key = { discount -> discount.hashCode() }
        ) { discount ->
            DiscountRow(
                discount = discount,
                enabled = discount !in cart.discounts,
                onAdd = onAddDiscount
            )
        }
    }
}

@Composable
private fun DiscountRow(
    discount: Discount,
    enabled: Boolean,
    onAdd: (Discount) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = when (discount) {
                    is Discount.Fixed -> Icons.Filled.PriceCheck
                    is Discount.Percentage -> Icons.Filled.Percent
                    is Discount.Bundle -> Icons.Filled.CardGiftcard
                },
                contentDescription = stringResource(R.string.label_discounts)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = discountTitle(discount),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Button(
                onClick = { onAdd(discount) },
                enabled = enabled
            ) {
                Text(text = stringResource(id = R.string.description_add_to_cart))
            }
        }
    }
}

@Composable
private fun discountTitle(discount: Discount): String = when (discount) {
    is Discount.Fixed -> stringResource(id = R.string.amount_off, discount.amount.toString())
    is Discount.Percentage -> stringResource(
        id = R.string.percentage_off,
        discount.value.toString()
    )

    is Discount.Bundle -> stringResource(
        id = R.string.pay_n_items_and_get,
        discount.amountItemsPay.toString(),
        stringResource(id = MockData.getName(discount.item)),
        discount.amountItemsGet.toString()
    )
}


